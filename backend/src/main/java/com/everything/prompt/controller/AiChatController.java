package com.everything.prompt.controller;

import com.everything.prompt.entity.ChatMessage;
import com.everything.prompt.entity.ChatSession;
import com.everything.prompt.service.ChatService;
import com.everything.prompt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final ChatService chatService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PostMapping("/session")
    public ApiResponse<ChatSession> createSession(@RequestParam(required = false) Long templateId) {
        Long userId = SecurityUtil.getCurrentUserId();
        ChatSession session = chatService.createSession(userId, templateId);
        return ApiResponse.success(session);
    }

    @GetMapping("/sessions")
    public ApiResponse<List<ChatSession>> getSessions() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ChatSession> sessions = chatService.getUserSessions(userId);
        return ApiResponse.success(sessions);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        List<ChatMessage> messages = chatService.getSessionMessages(sessionId);
        return ApiResponse.success(messages);
    }

    @PostMapping("/sessions/{sessionId}/send")
    public ApiResponse<String> sendMessage(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            String response = chatService.sendMessage(sessionId, userMessage);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("AI response failed: " + e.getMessage());
        }
    }

    @GetMapping(value = "/sessions/{sessionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @PathVariable Long sessionId,
            @RequestParam String message) {
        SseEmitter emitter = new SseEmitter(300_000L);

        executor.execute(() -> {
            try {
                StringBuilder fullResponse = new StringBuilder();
                chatService.sendMessageStream(sessionId, message, chunk -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("message")
                                .data(chunk));
                    } catch (Exception ignored) {}
                });
                emitter.send(SseEmitter.event()
                        .name("done")
                        .data("[DONE]"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return ApiResponse.success(null);
    }
}
