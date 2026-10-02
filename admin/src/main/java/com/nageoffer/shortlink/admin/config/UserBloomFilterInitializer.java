package com.nageoffer.shortlink.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dao.mapper.UsersMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户名布隆过滤器启动预热。
 * 挂在 @PostConstruct 而不是 ApplicationRunner：注册链路的 contains 是门禁，
 * 空过滤器等于所有用户名都放行，必须在对外提供服务之前建完。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserBloomFilterInitializer {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final UsersMapper usersMapper;

    @PostConstruct
    public void init() {
        // true = 这次是我初始化的（key 原本不存在：新 Redis / 迁移删过 key）→ 过滤器是空的，要重建
        // false = 配置早已存在（正常重启）→ 内容还在，不扫表
        // ⚠️ 已知边界：配置正是 tryInit 写进去的，所以重建中途抛异常时 context 起不来，
        //    而下次启动 tryInit 返回 false 会跳过重建，带着空过滤器上线。
        //    这里空过滤器的后果是放宽用户名判重（fail-open），DB 唯一键仍是最后一道。
        //    补救方向：失败时先删掉 {userName}:config 再抛出。
        if (userRegisterCachePenetrationBloomFilter.tryInit(RBloomFilterConfiguration.EXPECTED_INSERTIONS,
                RBloomFilterConfiguration.FALSE_PROBABILITY)) {
            log.warn("用户名布隆过滤器为空，开始重建");
            rebuild();
        }
    }

    private void rebuild() {
        // 不带 username 会广播到 16 张分片表，这正是重建想要的
        // 不按 del_flag 过滤：过滤器本来就只有 add、从不 remove，
        // 已注销用户的名字也算"已占用"，保持和存量语义一致
        List<String> usernames = usersMapper.selectList(
                        new LambdaQueryWrapper<UserDO>().select(UserDO::getUsername)).stream()
                .map(UserDO::getUsername)
                .distinct()
                .toList();
        usernames.forEach(userRegisterCachePenetrationBloomFilter::add);
        log.warn("用户名布隆过滤器重建完成，写入 {} 条", usernames.size());
    }
}
