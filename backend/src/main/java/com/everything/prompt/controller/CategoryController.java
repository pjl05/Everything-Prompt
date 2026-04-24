package com.everything.prompt.controller;

import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final TemplateService templateService;

    @GetMapping("/categories")
    public ApiResponse<List<TemplateCategory>> getCategories() {
        return ApiResponse.success(templateService.getAllCategories());
    }
}
