package com.everything.prompt.service;

import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VipService {

    private final SysUserMapper userMapper;

    public void upgradeVip(Long userId, Integer durationDays) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = user.getVipExpireTime();

        if (expireTime == null || expireTime.isBefore(now)) {
            expireTime = now.plusDays(durationDays);
        } else {
            expireTime = expireTime.plusDays(durationDays);
        }

        user.setRole("VIP");
        user.setVipExpireTime(expireTime);
        userMapper.updateById(user);
    }

    public boolean isVipValid(SysUser user) {
        if (user == null || !"VIP".equals(user.getRole())) {
            return false;
        }
        LocalDateTime expireTime = user.getVipExpireTime();
        return expireTime == null || expireTime.isAfter(LocalDateTime.now());
    }
}
