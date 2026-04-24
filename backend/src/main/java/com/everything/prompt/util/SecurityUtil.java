package com.everything.prompt.util;

import com.everything.prompt.entity.SysUser;

public class SecurityUtil {

    private static final ThreadLocal<SysUser> userThreadLocal = new ThreadLocal<>();

    public static void setCurrentUser(SysUser user) {
        userThreadLocal.set(user);
    }

    public static SysUser getCurrentUser() {
        return userThreadLocal.get();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
