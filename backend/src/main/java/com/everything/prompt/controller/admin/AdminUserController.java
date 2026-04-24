package com.everything.prompt.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.controller.ApiResponse;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminUserController {

    private final AdminUserService userService;

    @GetMapping
    public ApiResponse<Page<SysUser>> getUserPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(userService.getUserPage(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUser> getUserDetail(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserDetail(id));
    }

    @PutMapping("/{id}/status")
    @Logged("更新用户状态")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/vip")
    @Logged("授权VIP")
    public ApiResponse<Void> grantVip(@PathVariable Long id, @RequestParam Integer days) {
        userService.grantVip(id, days);
        return ApiResponse.success();
    }
}
