package com.everything.prompt.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.everything.prompt.entity.AiCallLog;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.AiCallLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final AiCallLogMapper aiCallLogMapper;

    @Value("${ai.minimax.api-key:}")
    private String apiKey;

    @Value("${ai.minimax.base-url:https://api.minimax.chat}")
    private String baseUrl;

    @Value("${ai.minimax.model:abab6.5s-chat}")
    private String model;

    @Value("${ai.minimax.timeout:30}")
    private int timeout;

    @Async("aiTaskExecutor")
    public CompletableFuture<String> optimizeAsync(Long userId, String content, String ipAddress) {
        String prompt = buildOptimizePrompt(content);
        String response = callMiniMax(prompt);
        saveCallLog(userId, "OPTIMIZE", content, response, ipAddress);
        return CompletableFuture.completedFuture(response);
    }

    @Async("aiTaskExecutor")
    public CompletableFuture<String> batchGenerateAsync(Long userId, String requirement, int count, String ipAddress) {
        String prompt = buildBatchPrompt(requirement, count);
        String response = callMiniMax(prompt);
        saveCallLog(userId, "BATCH", requirement, response, ipAddress);
        return CompletableFuture.completedFuture(response);
    }

    private String callMiniMax(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
            });

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            String response = HttpUtil.createPost(baseUrl + "/v1/text/chatcompletion_v2")
                    .headerMap(headers, false)
                    .timeout(timeout * 1000)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .execute()
                    .body();

            JSONObject jsonResponse = JSONUtil.parseObj(response);
            if (jsonResponse.containsKey("choices")) {
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getStr("content");
            }

            throw new BusinessException(500, "MiniMax API 调用失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("MiniMax API 调用异常", e);
            throw new BusinessException(500, "AI 服务暂时不可用，请稍后再试");
        }
    }

    private String buildOptimizePrompt(String content) {
        return String.format("""
            请优化以下提示词，使其更加专业、清晰、有效：

            原始提示词：
            %s

            请从以下方面进行优化：
            1. 逻辑补全
            2. 专业术语补充
            3. 指令拆分
            4. 约束强化
            5. 语气风格定制

            直接返回优化后的提示词，不要添加其他解释。
            """, content);
    }

    private String buildBatchPrompt(String requirement, int count) {
        return String.format("""
            根据以下需求，生成%d个不同版本的AI提示词：

            需求：%s

            要求：
            1. 每个版本要有差异化
            2. 涵盖不同的表达方式和角度
            3. 保持核心目标一致

            直接返回%d个版本的提示词，用编号分隔。
            """, count, requirement, count);
    }

    private void saveCallLog(Long userId, String callType, String input, String output, String ip) {
        try {
            AiCallLog callLog = new AiCallLog();
            callLog.setUserId(userId);
            callLog.setCallType(callType);
            callLog.setInputContent(input);
            callLog.setOutputContent(output);
            callLog.setModel(model);
            callLog.setIpAddress(ip);
            callLog.setStatus(1);
            aiCallLogMapper.insert(callLog);
        } catch (Exception e) {
            log.error("保存AI调用日志失败", e);
        }
    }
}
