package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.controller.ApiResponse;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.service.AdminCategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    @GetMapping
    public ApiResponse<List<TemplateCategory>> getAll() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @PostMapping
    @Logged("创建分类")
    public ApiResponse<TemplateCategory> create(@RequestBody CategoryRequest request) {
        TemplateCategory category = new TemplateCategory();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setIsFree(request.getIsFree());
        return ApiResponse.success(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Logged("更新分类")
    public ApiResponse<TemplateCategory> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        TemplateCategory category = new TemplateCategory();
        category.setId(id);
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setIsFree(request.getIsFree());
        return ApiResponse.success(categoryService.updateCategory(category));
    }

    @DeleteMapping("/{id}")
    @Logged("删除分类")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/sort")
    @Logged("更新分类排序")
    public ApiResponse<Void> updateSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        categoryService.updateSortOrder(id, sortOrder);
        return ApiResponse.success();
    }

    @Data
    public static class CategoryRequest {
        private String name;
        private String code;
        private String icon;
        private String description;
        private Integer sortOrder;
        private Integer isFree;
    }
}
