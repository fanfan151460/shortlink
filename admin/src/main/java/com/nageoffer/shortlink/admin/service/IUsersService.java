package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
import com.nageoffer.shortlink.admin.dto.req.UserLoginDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2026-05-20
 */
public interface IUsersService extends IService<UserDo> {

    /**
     * 返回userDTO
     * @param userName 用户名
     * @return 用户响应信息
     */
    UserDTO getByUerName(String userName);

    /**
     * 查询用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */

    Boolean hasUerName(String username);

    /**
     * 注册
     * @param userRegisterDTO 用户请求参数
     */
    void saveUser(UserRegisterDTO userRegisterDTO);

    /**
     * 修改用户信息
     * @param userUpdateDTO 用户请求参数
     */
    void updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * 登录
     * @param userLoginDTO 用户请求参数
     * @return token
     */
    UserLoginRespDTO Login(UserLoginDTO userLoginDTO);

    /**
     * 验证是否登录
     * @param username 用户名
     * @param token uuid
     * @return 是否登录
     */
    Boolean hasLogin(String username, String token);

    /**
     * 注销
     * @param userName 用户名
     * @param token uuid
     */
    void logout(String userName, String token);
}
