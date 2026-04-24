package com.everything.prompt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/templates")
    public ApiResponse<Page<PromptTemplate>> getTemplates(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(templateService.getTemplates(categoryId, keyword, page, size));
    }

    @GetMapping("/templates/{id}")
    public ApiResponse<PromptTemplate> getTemplate(@PathVariable Long id) {
        return ApiResponse.success(templateService.getTemplateById(id));
    }
}
