package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.controller.ApiResponse;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.service.AdminTemplateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminTemplateController {

    private final AdminTemplateService adminTemplateService;

    @GetMapping
    public ApiResponse<List<PromptTemplate>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminTemplateService.getAllTemplates(keyword, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromptTemplate> getById(@PathVariable Long id) {
        return ApiResponse.success(adminTemplateService.getAllTemplates(null, null)
                .stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null));
    }

    @PostMapping
    @Logged("创建模板")
    public ApiResponse<PromptTemplate> create(@RequestBody TemplateRequest request) {
        PromptTemplate template = new PromptTemplate();
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        template.setCategoryId(request.getCategoryId());
        template.setTags(request.getTags());
        template.setIsPremium(request.getIsPremium());
        return ApiResponse.success(adminTemplateService.createTemplate(template));
    }

    @PutMapping("/{id}")
    @Logged("更新模板")
    public ApiResponse<PromptTemplate> update(@PathVariable Long id, @RequestBody TemplateRequest request) {
        PromptTemplate template = new PromptTemplate();
        template.setId(id);
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        template.setCategoryId(request.getCategoryId());
        template.setTags(request.getTags());
        template.setIsPremium(request.getIsPremium());
        return ApiResponse.success(adminTemplateService.updateTemplate(template));
    }

    @DeleteMapping("/{id}")
    @Logged("删除模板")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminTemplateService.deleteTemplate(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    @Logged("更新模板状态")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminTemplateService.updateStatus(id, status);
        return ApiResponse.success();
    }

    @PostMapping("/batch-delete")
    @Logged("批量删除模板")
    public ApiResponse<Void> batchDelete(@RequestBody List<Long> ids) {
        adminTemplateService.batchDelete(ids);
        return ApiResponse.success();
    }

    @Data
    public static class TemplateRequest {
        private String title;
        private String description;
        private String content;
        private Long categoryId;
        private String tags;
        private Integer isPremium;
    }
}
