package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Logged("用户注册")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request.getUsername(), request.getPassword(), request.getEmail()));
    }

    @PostMapping("/login")
    @Logged("用户登录")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token.substring(7));
        }
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(authService.getCurrentUser(token.substring(7)));
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
