package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.SysUserMapper;
import com.everything.prompt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(String username, String password, String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);

        return generateToken(user);
    }

    public Map<String, Object> login(String username, String password) {
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(401, "账号已被禁用");
        }

        return generateToken(user);
    }

    public void logout(String token) {
        redisTemplate.opsForValue().set("jwt:blacklist:" + token, "1", 7, TimeUnit.DAYS);
    }

    public SysUser getCurrentUser(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userMapper.selectById(userId);
    }

    private Map<String, Object> generateToken(SysUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        String token = jwtUtil.generateToken(claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
}
