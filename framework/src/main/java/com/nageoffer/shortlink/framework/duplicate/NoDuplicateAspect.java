package com.nageoffer.shortlink.framework.duplicate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.nageoffer.shortlink.framework.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.nageoffer.shortlink.framework.constant.Constant.NODUPLICATE_SUBMIT;
import static com.nageoffer.shortlink.framework.constant.Constant.USER_HEADER;

/**
 * 防止重复提交切面
 * <p>
 * 排序钉在最前面：切面和 Spring 事务是同一个代理上的一条拦截器链（不是套娃），
 * 顺序靠 @Order 决定。事务通知默认 LOWEST_PRECEDENCE，这里排队靠前可以让防重
 * 确定性地跑在事务外层——抢不到 key 就直接抛，不必白开一个事务；同时下面的
 * finally 删 key 会落在事务提交之后，不会出现"锁已释放但提交失败"。
 * <p>
 * 注意这里是 HIGHEST_PRECEDENCE + 2 而不是 HIGHEST_PRECEDENCE：ExposeInvocationInterceptor
 * 恒占 HIGHEST_PRECEDENCE + 1，@annotation 参数绑定依赖它把 JoinPointMatch 挂到当前
 * MethodInvocation 上（AspectJExpressionPointcut#matches 里 pmi 为 null 时会静默跳过
 * bindParameters）。一旦排到它前面，绑定就会失败并抛
 * "Required to bind 2 arguments, but only bound 1 (JoinPointMatch was NOT bound in invocation)"。
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@RequiredArgsConstructor
@Component
@Aspect
public class NoDuplicateAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(noDuplicateSubmit)")
    public Object noDuplicate(ProceedingJoinPoint joinPoint, NoDuplicateSubmit noDuplicateSubmit) throws Throwable {
        String key = String.format(NODUPLICATE_SUBMIT, getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));

        Boolean setIfAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", noDuplicateSubmit.expire(), noDuplicateSubmit.timeUnit());
        if (!Boolean.TRUE.equals(setIfAbsent)) {
            throw new ClientException(noDuplicateSubmit.msg());
        }
        try {
            return joinPoint.proceed();
        } finally {
            // 方法执行完（成功或异常）释放 key，避免正常完成后仍被误判为重复提交
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private String getServletPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest().getServletPath();
    }

    /**
     * @return 获取当前 username
     */
    private String getCurrentUserId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String userName = attrs.getRequest().getHeader(USER_HEADER);
        if (StrUtil.isBlank(userName)) {
            return "empty-user";
        }
        return userName;
    }

    /**
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }
}
