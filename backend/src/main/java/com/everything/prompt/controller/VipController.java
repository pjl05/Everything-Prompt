package com.everything.prompt.controller;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.VipService;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vip")
@RequiredArgsConstructor
public class VipController {

    private final VipService vipService;

    @PostMapping("/upgrade")
    @RequireRole({"USER", "VIP"})
    public ApiResponse<Void> upgradeVip(@RequestBody UpgradeRequest request) {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        vipService.upgradeVip(currentUser.getId(), request.getDays());
        return ApiResponse.success();
    }

    @GetMapping("/status")
    @RequireRole({"USER", "VIP"})
    public ApiResponse<Boolean> checkVipStatus() {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        return ApiResponse.success(vipService.isVipValid(currentUser));
    }

    @Data
    public static class UpgradeRequest {
        private Integer days;
    }
}
