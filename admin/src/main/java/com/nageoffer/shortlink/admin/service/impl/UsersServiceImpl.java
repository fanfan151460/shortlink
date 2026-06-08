package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.exception.ClientException;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dao.mapper.UsersMapper;
import com.nageoffer.shortlink.admin.service.IGroupService;
import com.nageoffer.shortlink.admin.dto.req.UserLoginDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOGIN;
import static com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode.USER_REGISTER_ERROR;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCode.USER_HAD;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-20
 */
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, UserDO> implements IUsersService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final IGroupService groupService;

    private static final int MAX_CONCURRENT_LOGIN = 3;

    @Override
    public UserDTO getByUerName(String userName) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userName);
        UserDO userDo = baseMapper.selectOne(wrapper);
        if (userDo == null||userName == null) {
            throw new ClientException("用户为空");
        }
        return BeanUtil.copyProperties(userDo, UserDTO.class);
    }

    @Override
    public Boolean hasUerName(String userName) {
        return !userRegisterCachePenetrationBloomFilter.contains(userName);
    }

    @Override
    public void saveUser(UserRegisterDTO userRegisterDTO) {
        if (!hasUerName(userRegisterDTO.getUsername())) {
            throw new ClientException(USER_REGISTER_ERROR);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + userRegisterDTO.getUsername());
        if (!lock.tryLock()) {
            throw new ClientException(USER_HAD);
        }
        try {
            int insert = baseMapper.insert(BeanUtil.copyProperties(userRegisterDTO, UserDO.class));
            if (insert < 1) {
                throw new ClientException(USER_REGISTER_ERROR);
            }
            groupService.saveGroup("默认分组", userRegisterDTO.getUsername());
            userRegisterCachePenetrationBloomFilter.add(userRegisterDTO.getUsername());
        }catch(DuplicateKeyException e) {
            throw new ClientException("用户已存在");
        }
        finally{
            lock.unlock();
        }
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        if (Objects.equals(userUpdateDTO.getUsername(), UserContext.getUsername())) {
            throw new ClientException("当前登录用户修改请求异常");
        }
        LambdaUpdateWrapper<UserDO> wrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, userUpdateDTO.getUsername());
        baseMapper.update(BeanUtil.copyProperties(userUpdateDTO, UserDO.class), wrapper);
    }

    @Override
    public UserLoginRespDTO Login(UserLoginDTO userLoginDTO) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, userLoginDTO.getUsername())
                .eq(UserDO::getPassword, userLoginDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDo = baseMapper.selectOne(wrapper);
        if (userDo == null) {
            throw new ClientException("账号或密码错误");
        }

        String key = LOGIN + userLoginDTO.getUsername();
        // 限制并发登录数
        Long loginCount = stringRedisTemplate.opsForHash().size(key);
        if (loginCount != null && loginCount >= MAX_CONCURRENT_LOGIN) {
            throw new ClientException("登录设备已达上限");
        }
        /*
         * hash
         * key: username
         *  key: token
         *  val: userDTO
         */
        UUID uuid = UUID.randomUUID(false);
        stringRedisTemplate.opsForHash()
                .put(key, uuid.toString(), JSONUtil.toJsonStr(userLoginDTO));
        stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid.toString());
    }

    @Override
    public Boolean hasLogin(String username, String token) {
        if (username == null || token == null) {
            return false;
        }
        return stringRedisTemplate.opsForHash().hasKey(LOGIN + token, username);
    }

    @Override
    public void logout(String username, String token) {
        if (hasLogin(username, token)) {
            stringRedisTemplate.opsForHash().delete(LOGIN + username, token);
        } else {
            throw new ClientException("用户未登录或token已过期");
        }
    }
}
