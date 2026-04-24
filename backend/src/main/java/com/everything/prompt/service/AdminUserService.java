package com.everything.prompt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final SysUserMapper userMapper;

    public Page<SysUser> getUserPage(String keyword, int page, int size) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(keyword != null, SysUser::getUsername, keyword)
                .orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(pageParam, wrapper);
    }

    public SysUser getUserDetail(Long id) {
        return userMapper.selectById(id);
    }

    public void updateUserStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void grantVip(Long id, Integer days) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setRole("VIP");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = user.getVipExpireTime();
        if (expireTime == null || expireTime.isBefore(now)) {
            expireTime = now.plusDays(days);
        } else {
            expireTime = expireTime.plusDays(days);
        }
        user.setVipExpireTime(expireTime);
        userMapper.updateById(user);
    }
}
