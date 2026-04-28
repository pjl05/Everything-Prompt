package com.everything.prompt.util;

import com.everything.prompt.config.MiniMaxConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MiniMaxUtil {

    private final MiniMaxConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(List<Map<String, String>> messages) throws Exception {
        URL url = new URL(config.getBaseUrl() + "/v1/text/chatcompletion_v2");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
        conn.setDoOutput(true);
        conn.setConnectTimeout(config.getTimeout() * 1000);
        conn.setReadTimeout(config.getTimeout() * 1000);

        String requestBody = buildRequest(messages);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("MiniMax API error: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return parseResponse(response.toString());
    }

    public String chatStream(List<Map<String, String>> messages, StringBuilder fullResponse) throws Exception {
        URL url = new URL(config.getBaseUrl() + "/v1/text/chatcompletion_v2");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
        conn.setDoOutput(true);
        conn.setConnectTimeout(config.getTimeout() * 1000);
        conn.setReadTimeout(config.getTimeout() * 1000 * 10);

        String requestBody = buildRequest(messages);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("MiniMax API error: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String jsonStr = line.substring(6);
                    if ("[DONE]".equals(jsonStr)) {
                        break;
                    }
                    String content = parseStreamResponse(jsonStr);
                    if (content != null) {
                        fullResponse.append(content);
                    }
                }
            }
        }

        return fullResponse.toString();
    }

    private String buildRequest(List<Map<String, String>> messages) throws Exception {
        Map<String, Object> request = Map.of(
                "model", config.getModel(),
                "tokens_to_generate", 1024,
                "temperature", 0.7,
                "messages", messages
        );
        return objectMapper.writeValueAsString(request);
    }

    private String parseResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            return choices.get(0).path("messages").get(0).path("text").asText();
        }
        return "";
    }

    private String parseStreamResponse(String jsonStr) {
        try {
            JsonNode node = objectMapper.readTree(jsonStr);
            JsonNode choices = node.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("messages").get(0).path("text").asText();
            }
        } catch (Exception e) {
            log.debug("Parse stream response failed: {}", e.getMessage());
        }
        return "";
    }
}
