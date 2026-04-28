package com.everything.prompt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptShare;
import com.everything.prompt.service.ShareService;
import com.everything.prompt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @PostMapping
    public ApiResponse<PromptShare> createShare(@RequestBody PromptShare share) {
        Long userId = SecurityUtil.getCurrentUserId();
        share.setUserId(userId);
        PromptShare result = shareService.createShare(share);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<Page<PromptShare>> getShareList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(shareService.getShareList(categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromptShare> getShareById(@PathVariable Long id) {
        shareService.incrementUsage(id);
        return ApiResponse.success(shareService.getShareById(id));
    }

    @GetMapping("/my")
    public ApiResponse<Page<PromptShare>> getMyShares(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(shareService.getMyShares(userId, page, size));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Map<String, Boolean>> toggleLike(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean liked = shareService.toggleLike(id, userId);
        return ApiResponse.success(Map.of("liked", liked));
    }

    @GetMapping("/{id}/like/status")
    public ApiResponse<Map<String, Boolean>> getLikeStatus(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean hasLiked = shareService.hasLiked(id, userId);
        return ApiResponse.success(Map.of("hasLiked", hasLiked));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShare(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        shareService.deleteShare(id, userId);
        return ApiResponse.success(null);
    }
}
