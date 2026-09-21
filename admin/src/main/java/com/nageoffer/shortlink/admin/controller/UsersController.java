package com.nageoffer.shortlink.admin.controller;


import com.nageoffer.shortlink.admin.dto.req.UserLoginDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.service.IUsersService;
import com.nageoffer.shortlink.framework.result.Result;
import com.nageoffer.shortlink.framework.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "注册、登录、注销、信息修改")
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class UsersController {
    private final IUsersService usersService;

    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/user/{username}")
    public Result<UserDTO> getUsers(@PathVariable("username") String userName) {
        UserDTO UserDTO = usersService.getByUerName(userName);
        return Results.success(UserDTO);
    }

    @Operation(summary = "测试连接")
    @GetMapping("test")
    public Result<String> test() {
        return Results.success("admin");
    }

    @Operation(summary = "检查用户名是否存在")
    @GetMapping("/user/has-username")
    public Result<Boolean> HasYourName(String username) {
        return Results.success(usersService.hasUerName(username));
    }

    @Operation(summary = "注册用户", description = "布隆过滤器判重 + 分布式锁防并发 + 密码加密存储")
    @PostMapping("/user")
    public Result<Void> addUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        usersService.saveUser(userRegisterDTO);
        return Results.success();
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/user")
    public Result<Void> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        //TODO 判断是否为当前用户

        usersService.updateUser(userUpdateDTO);
        return Results.success();
    }

    @Operation(summary = "用户登录", description = "验证密码后生成token存入Redis，并发限制3个会话")
    @PostMapping("/user/login")
    public Result<UserLoginRespDTO> Login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginRespDTO token = usersService.Login(userLoginDTO);
        return Results.success(token);
    }

    @Operation(summary = "检查登录状态")
    @GetMapping("/user/check-login")
    public Result<Boolean> hasLogin(String username, String token) {
        Boolean isLogin = usersService.hasLogin(username, token);
        return Results.success(isLogin);
    }

    @Operation(summary = "用户注销")
    @DeleteMapping("/user/logout")
    public Result<Void> logout(String username, String token) {
        usersService.logout(username, token);
        return Results.success();
    }
}
