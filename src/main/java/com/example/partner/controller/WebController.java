package com.example.partner.controller;

import com.example.partner.common.Result;
import com.example.partner.controller.domain.UserRequest;
import com.example.partner.entity.User;
import com.example.partner.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "无权限接口列表")
@Slf4j
@RestController
public class WebController {
    @Resource
    private IUserService userService;

    @GetMapping(value = "/")
    @ApiOperation(value = "版本校验接口")
    public String version() {
        String ver = "partner-0.0.1-SNAPSHOT";  // 应用版本号
        Package aPackage = WebController.class.getPackage();
        String title = aPackage.getImplementationTitle();
        String version = aPackage.getImplementationVersion();
        if (title != null && version != null) {
            ver = String.join("-", title, version);
        }
        return ver;
    }

    @ApiOperation(value = "用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        User res = userService.login(user);
        return Result.success(res);
    }


    @ApiOperation(value = "注册接口")
    @PostMapping("/register")
    public Result register(@RequestBody UserRequest user){
        User res = userService.register(user);
        return Result.success(res);
    }

    @ApiOperation(value = "邮箱接口")
    @GetMapping ("/email")
    public Result sendEmail(@RequestParam String email, @RequestParam String type){
        userService.sendEmail(email,type);
        return Result.success();
    }
    @ApiOperation(value = "重置密码接口")
    @GetMapping ("/password/reset")
    public Result passwordReset(@RequestBody UserRequest userRequest){
       String newPassword= userService.passwordReset(userRequest);
        return Result.success(newPassword);
    }

}
