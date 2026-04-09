package com.creator.controller;

import com.creator.annotation.OperLog;
import com.creator.common.Result;
import com.creator.dto.LoginDTO;
import com.creator.dto.RechargeDTO;
import com.creator.dto.RegisterDTO;
import com.creator.entity.User;
import com.creator.service.UserService;
import com.creator.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }
    
    @PostMapping("/register")
    @OperLog("用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        User user = userService.getCurrentUser();
        user.setPassword(null);
        return Result.success(user);
    }
    
    @PutMapping("/profile")
    @OperLog("更新个人资料")
    public Result<Void> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.success();
    }
    
    @PostMapping("/recharge")
    @OperLog("账户充值")
    public Result<Void> recharge(@Valid @RequestBody RechargeDTO dto) {
        userService.recharge(dto);
        return Result.success();
    }
    
    @PostMapping("/reset-password")
    @OperLog("重置密码")
    public Result<Void> resetPassword(@RequestBody java.util.Map<String, String> params) {
        userService.resetPassword(params.get("username"), params.get("newPassword"));
        return Result.success();
    }
}
