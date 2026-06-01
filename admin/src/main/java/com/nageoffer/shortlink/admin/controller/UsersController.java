package com.nageoffer.shortlink.admin.controller;


import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.UserLoginDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author author
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class UsersController {
    private final IUsersService usersService;

    /**
     * 根据username查询数据
     */
    @GetMapping("/user/{username}")
    public Result<UserDTO> getUsers(@PathVariable("username") String userName) {
        UserDTO UserDTO = usersService.getByUerName(userName);
        return Results.success(UserDTO);
    }

    //测试连接
    @GetMapping("test")
    public Result<String> test() {
        return Results.success("admin");
    }

    //判断用户名是否存在
    @GetMapping("/user/has-username")
    public Result<Boolean> HasYourName(String username) {
        return Results.success(usersService.hasUerName(username));
    }

    /**
     * 注册用户
     */
    @PostMapping("/user")
    public Result<Void> addUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        usersService.saveUser(userRegisterDTO);
        return Results.success();
    }

    /**
     * 修改用户参数
     */
    @PutMapping("/user")
    public Result<Void> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        //TODO 判断是否为当前用户

        usersService.updateUser(userUpdateDTO);
        return Results.success();
    }

    @PostMapping("/user/login")
    public Result<UserLoginRespDTO> Login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginRespDTO token = usersService.Login(userLoginDTO);
        return Results.success(token);
    }

    @GetMapping("/user/check-login")
    public Result<Boolean> hasLogin(String username, String token) {
        Boolean isLogin = usersService.hasLogin(username, token);
        return Results.success(isLogin);
    }

    @DeleteMapping("/user/logout")
    public Result<Void> logout(String username, String token) {
        usersService.logout(username, token);
        return Results.success();
    }
}
