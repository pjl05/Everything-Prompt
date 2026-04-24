package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.FavoriteService;
import com.everything.prompt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @RequireRole({"USER", "VIP", "ADMIN"})
    public ApiResponse<List<PromptTemplate>> getMyFavorites() {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(favoriteService.getUserFavorites(user.getId()));
    }

    @PostMapping("/{templateId}")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @Logged("添加收藏")
    public ApiResponse<Void> addFavorite(@PathVariable Long templateId) {
        SysUser user = SecurityUtil.getCurrentUser();
        favoriteService.addFavorite(user.getId(), templateId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{templateId}")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @Logged("取消收藏")
    public ApiResponse<Void> removeFavorite(@PathVariable Long templateId) {
        SysUser user = SecurityUtil.getCurrentUser();
        favoriteService.removeFavorite(user.getId(), templateId);
        return ApiResponse.success();
    }
}
