package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.annotation.RateLimit;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.AiService;
import com.everything.prompt.service.VipService;
import com.everything.prompt.util.IpUtil;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final VipService vipService;

    @PostMapping("/optimize")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @RateLimit(limit = 10, period = 60, type = "user")
    @Logged("AI优化")
    public ApiResponse<CompletableFuture<String>> optimize(@RequestBody OptimizeRequest request, HttpServletRequest httpRequest) {
        SysUser user = SecurityUtil.getCurrentUser();
        String ip = IpUtil.getIpAddress(httpRequest);
        CompletableFuture<String> result = aiService.optimizeAsync(user.getId(), request.getContent(), ip);
        return ApiResponse.success(result);
    }

    @PostMapping("/batch")
    @RequireRole({"VIP", "ADMIN"})
    @RateLimit(limit = 5, period = 60, type = "user")
    @Logged("批量生成")
    public ApiResponse<CompletableFuture<String>> batchGenerate(@RequestBody BatchRequest request, HttpServletRequest httpRequest) {
        SysUser user = SecurityUtil.getCurrentUser();
        String ip = IpUtil.getIpAddress(httpRequest);
        CompletableFuture<String> result = aiService.batchGenerateAsync(user.getId(), request.getRequirement(), request.getCount(), ip);
        return ApiResponse.success(result);
    }

    @Data
    public static class OptimizeRequest {
        private String content;
    }

    @Data
    public static class BatchRequest {
        private String requirement;
        private Integer count = 5;
    }
}
