package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import com.everything.prompt.mapper.TemplateCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final PromptTemplateMapper templateMapper;
    private final TemplateCategoryMapper categoryMapper;

    @Cacheable(value = "categories", key = "'all'")
    public List<TemplateCategory> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<TemplateCategory>()
                .eq(TemplateCategory::getStatus, 1)
                .orderByAsc(TemplateCategory::getSortOrder)
        );
    }

    public Page<PromptTemplate> getTemplates(Long categoryId, String keyword, int page, int size) {
        Page<PromptTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromptTemplate::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(PromptTemplate::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PromptTemplate::getTitle, keyword)
                   .or()
                   .like(PromptTemplate::getDescription, keyword);
        }

        wrapper.orderByDesc(PromptTemplate::getUsageCount);
        return templateMapper.selectPage(pageParam, wrapper);
    }

    public PromptTemplate getTemplateById(Long id) {
        PromptTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(404, "模板不存在");
        }
        templateMapper.update(null,
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<PromptTemplate>()
                .eq(PromptTemplate::getId, id)
                .setSql("usage_count = usage_count + 1")
        );
        return template;
    }
}
