package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.UserFavorite;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import com.everything.prompt.mapper.UserFavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteMapper favoriteMapper;
    private final PromptTemplateMapper templateMapper;

    public List<PromptTemplate> getUserFavorites(Long userId) {
        List<Long> templateIds = favoriteMapper.selectList(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
        ).stream().map(UserFavorite::getTemplateId).toList();

        if (templateIds.isEmpty()) {
            return List.of();
        }

        return templateMapper.selectList(
            new LambdaQueryWrapper<PromptTemplate>()
                .in(PromptTemplate::getId, templateIds)
                .eq(PromptTemplate::getStatus, 1)
        );
    }

    public void addFavorite(Long userId, Long templateId) {
        Long count = favoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        );
        if (count > 0) {
            throw new BusinessException(400, "已收藏过该模板");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setTemplateId(templateId);
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long templateId) {
        favoriteMapper.delete(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        );
    }

    public boolean isFavorited(Long userId, Long templateId) {
        return favoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        ) > 0;
    }
}
