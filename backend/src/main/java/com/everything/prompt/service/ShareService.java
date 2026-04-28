package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptShare;
import com.everything.prompt.entity.ShareLike;
import com.everything.prompt.mapper.PromptShareMapper;
import com.everything.prompt.mapper.ShareLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final PromptShareMapper shareMapper;
    private final ShareLikeMapper likeMapper;

    @Transactional
    public PromptShare createShare(PromptShare share) {
        share.setLikeCount(0);
        share.setUsageCount(0);
        share.setStatus(1);
        shareMapper.insert(share);
        return share;
    }

    public Page<PromptShare> getShareList(Long categoryId, int page, int size) {
        Page<PromptShare> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PromptShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromptShare::getStatus, 1)
               .orderByDesc(PromptShare::getLikeCount)
               .orderByDesc(PromptShare::getCreateTime);

        if (categoryId != null) {
            wrapper.eq(PromptShare::getTemplateId, categoryId);
        }

        return shareMapper.selectPage(pageParam, wrapper);
    }

    public PromptShare getShareById(Long id) {
        return shareMapper.selectById(id);
    }

    public Page<PromptShare> getMyShares(Long userId, int page, int size) {
        Page<PromptShare> pageParam = new Page<>(page, size);
        return shareMapper.selectPage(pageParam,
                new LambdaQueryWrapper<PromptShare>()
                        .eq(PromptShare::getUserId, userId)
                        .orderByDesc(PromptShare::getCreateTime));
    }

    @Transactional
    public boolean toggleLike(Long shareId, Long userId) {
        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<ShareLike>()
                .eq(ShareLike::getShareId, shareId)
                .eq(ShareLike::getUserId, userId);

        ShareLike existingLike = likeMapper.selectOne(wrapper);

        if (existingLike != null) {
            likeMapper.deleteById(existingLike.getId());
            PromptShare share = shareMapper.selectById(shareId);
            if (share != null) {
                share.setLikeCount(Math.max(0, share.getLikeCount() - 1));
                shareMapper.updateById(share);
            }
            return false;
        } else {
            ShareLike newLike = new ShareLike();
            newLike.setShareId(shareId);
            newLike.setUserId(userId);
            likeMapper.insert(newLike);

            PromptShare share = shareMapper.selectById(shareId);
            if (share != null) {
                share.setLikeCount(share.getLikeCount() + 1);
                shareMapper.updateById(share);
            }
            return true;
        }
    }

    public boolean hasLiked(Long shareId, Long userId) {
        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<ShareLike>()
                .eq(ShareLike::getShareId, shareId)
                .eq(ShareLike::getUserId, userId);
        return likeMapper.exists(wrapper);
    }

    @Transactional
    public void deleteShare(Long shareId, Long userId) {
        shareMapper.delete(new LambdaQueryWrapper<PromptShare>()
                .eq(PromptShare::getId, shareId)
                .eq(PromptShare::getUserId, userId));
        likeMapper.delete(new LambdaQueryWrapper<ShareLike>()
                .eq(ShareLike::getShareId, shareId));
    }

    @Transactional
    public void incrementUsage(Long shareId) {
        PromptShare share = shareMapper.selectById(shareId);
        if (share != null) {
            share.setUsageCount(share.getUsageCount() + 1);
            shareMapper.updateById(share);
        }
    }
}
