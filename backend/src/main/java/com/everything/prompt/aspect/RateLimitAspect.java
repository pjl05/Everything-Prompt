package com.everything.prompt.aspect;

import com.everything.prompt.annotation.RateLimit;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.util.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint point, RateLimit rateLimit) {
        HttpServletRequest request = getRequest();
        String key = generateKey(request, rateLimit.type());

        Long current = redisTemplate.opsForValue().increment(key);
        if (current == null) {
            current = 1L;
            redisTemplate.opsForValue().set(key, "1", rateLimit.period(), TimeUnit.SECONDS);
        }

        if (current > rateLimit.limit()) {
            log.warn("触发限流: key={}, current={}, limit={}", key, current, rateLimit.limit());
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
    }

    private String generateKey(HttpServletRequest request, String type) {
        String value = switch (type) {
            case "ip" -> IpUtil.getIpAddress(request);
            case "user" -> request.getHeader("X-User-Id");
            default -> IpUtil.getIpAddress(request);
        };
        return String.format("ratelimit:%s:%s", type, value != null ? value : "unknown");
    }

    private HttpServletRequest getRequest() {
        return ((HttpServletRequest) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).resolveRequestAttribute("javax.servlet.http.HttpServletRequest");
    }
}
