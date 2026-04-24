package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.entity.UserWord;
import com.everything.prompt.service.WordService;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    @RequireRole({"VIP", "ADMIN"})
    public ApiResponse<List<UserWord>> getMyWords() {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.getUserWords(user.getId()));
    }

    @PostMapping
    @RequireRole({"VIP", "ADMIN"})
    @Logged("添加词条")
    public ApiResponse<UserWord> addWord(@RequestBody WordRequest request) {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.addWord(user.getId(), request.getTitle(), request.getContent(), request.getGroupName()));
    }

    @PutMapping("/{id}")
    @RequireRole({"VIP", "ADMIN"})
    @Logged("更新词条")
    public ApiResponse<UserWord> updateWord(@PathVariable Long id, @RequestBody WordRequest request) {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.updateWord(user.getId(), id, request.getTitle(), request.getContent(), request.getGroupName()));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"VIP", "ADMIN"})
    @Logged("删除词条")
    public ApiResponse<Void> deleteWord(@PathVariable Long id) {
        SysUser user = SecurityUtil.getCurrentUser();
        wordService.deleteWord(user.getId(), id);
        return ApiResponse.success();
    }

    @Data
    public static class WordRequest {
        private String title;
        private String content;
        private String groupName;
    }
}
