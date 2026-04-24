package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.mapper.TemplateCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final TemplateCategoryMapper categoryMapper;

    public List<TemplateCategory> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<TemplateCategory>()
                .orderByAsc(TemplateCategory::getSortOrder)
        );
    }

    @CacheEvict(value = "categories", allEntries = true)
    public TemplateCategory createCategory(TemplateCategory category) {
        categoryMapper.insert(category);
        return category;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public TemplateCategory updateCategory(TemplateCategory category) {
        categoryMapper.updateById(category);
        return category;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void updateSortOrder(Long id, Integer sortOrder) {
        categoryMapper.update(null,
            new LambdaQueryWrapper<TemplateCategory>()
                .eq(TemplateCategory::getId, id)
                .set(TemplateCategory::getSortOrder, sortOrder)
        );
    }
}
