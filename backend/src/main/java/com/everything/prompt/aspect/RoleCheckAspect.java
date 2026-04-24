package com.everything.prompt.aspect;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint point, RequireRole requireRole) {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(401, "请先登录");
        }

        String userRole = currentUser.getRole();
        boolean hasRole = Arrays.asList(requireRole.value()).contains(userRole);

        if (!hasRole && !"ADMIN".equals(userRole)) {
            log.warn("用户 {} 权限不足，角色: {}，需要: {}", currentUser.getUsername(), userRole, Arrays.toString(requireRole.value()));
            throw new BusinessException(403, "权限不足");
        }
    }
}
