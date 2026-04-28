package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTemplateService {

    private final PromptTemplateMapper templateMapper;

    public List<PromptTemplate> getAllTemplates(String keyword, Integer status) {
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PromptTemplate::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(PromptTemplate::getStatus, status);
        }
        wrapper.orderByDesc(PromptTemplate::getCreateTime);
        return templateMapper.selectList(wrapper);
    }

    @CacheEvict(value = "templates", allEntries = true)
    public PromptTemplate createTemplate(PromptTemplate template) {
        template.setStatus(1);
        template.setIsOfficial(1);
        templateMapper.insert(template);
        return template;
    }

    @CacheEvict(value = "templates", allEntries = true)
    public PromptTemplate updateTemplate(PromptTemplate template) {
        if (template.getId() == null) {
            throw new BusinessException(400, "模板ID不能为空");
        }
        templateMapper.updateById(template);
        return templateMapper.selectById(template.getId());
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void deleteTemplate(Long id) {
        templateMapper.deleteById(id);
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void updateStatus(Long id, Integer status) {
        UpdateWrapper<PromptTemplate> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("status", status);
        templateMapper.update(null, wrapper);
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void batchDelete(List<Long> ids) {
        templateMapper.deleteBatchIds(ids);
    }
}
