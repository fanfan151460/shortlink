package com.nageoffer.shortlink.project.mq.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.IDEMPOTENT_KEY;

@RequiredArgsConstructor
@Component
public class MsgQueueIdempotentHandler {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 判断是否已消费
     * @param msgKey 消息标识
     * @return 判断结果
     */
    public Boolean hasConsume(String msgKey) {
        Boolean setIfAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent(String.format(IDEMPOTENT_KEY, msgKey), "0", 10, TimeUnit.MINUTES);
        return !Boolean.TRUE.equals(setIfAbsent);
    }

    /**
     * 是否成功消费
     * @param msgKey 消息标识
     * @return 消费结果
     */
    public Boolean isSuccessConsume(String msgKey) {
        return Objects.equals(stringRedisTemplate.opsForValue().get(msgKey), "1");
    }

    /**
     * 成功消费
     * @param msgKey 消息标识
     */
    public void successConsume(String msgKey) {
        stringRedisTemplate.opsForValue().set(String.format(IDEMPOTENT_KEY, msgKey), "1", 10, TimeUnit.MINUTES);
    }

    /**
     * 消费结束或失败
     * @param msgKey 消息标识
     */
    public void delConsume(String msgKey) {
        stringRedisTemplate.delete(String.format(IDEMPOTENT_KEY, msgKey));
    }
}