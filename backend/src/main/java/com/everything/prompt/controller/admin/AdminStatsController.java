package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.controller.ApiResponse;
import com.everything.prompt.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminStatsController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard() {
        return ApiResponse.success(statsService.getDashboardStats());
    }
}
