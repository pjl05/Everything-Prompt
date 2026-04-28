package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.ChatMessage;
import com.everything.prompt.entity.ChatSession;
import com.everything.prompt.mapper.ChatMessageMapper;
import com.everything.prompt.mapper.ChatSessionMapper;
import com.everything.prompt.util.MiniMaxUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;
    private final MiniMaxUtil miniMaxUtil;

    public ChatSession createSession(Long userId, Long templateId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTemplateId(templateId);
        session.setTitle("新对话");
        sessionMapper.insert(session);
        return session;
    }

    public List<ChatSession> getUserSessions(Long userId) {
        return sessionMapper.selectList(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getUserId, userId)
                        .orderByDesc(ChatSession::getUpdateTime)
        );
    }

    public List<ChatMessage> getSessionMessages(Long sessionId) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, sessionId)
                        .orderByAsc(ChatMessage::getTimestamp)
        );
    }

    @Transactional
    public String sendMessage(Long sessionId, String userMessage) throws Exception {
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        messageMapper.insert(userMsg);

        List<ChatMessage> history = getSessionMessages(sessionId);
        List<Map<String, String>> messages = history.stream()
                .map(msg -> Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ))
                .collect(Collectors.toList());

        String aiResponse = miniMaxUtil.chat(messages);

        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(aiResponse);
        messageMapper.insert(assistantMsg);

        updateSessionTitle(sessionId, userMessage);
        return aiResponse;
    }

    @Transactional
    public String sendMessageStream(Long sessionId, String userMessage, java.util.function.Consumer<String> onChunk) throws Exception {
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        messageMapper.insert(userMsg);

        List<ChatMessage> history = getSessionMessages(sessionId);
        List<Map<String, String>> messages = history.stream()
                .map(msg -> Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ))
                .collect(Collectors.toList());

        StringBuilder fullResponse = new StringBuilder();
        String aiResponse = miniMaxUtil.chatStream(messages, fullResponse);

        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(aiResponse);
        messageMapper.insert(assistantMsg);

        updateSessionTitle(sessionId, userMessage);
        return aiResponse;
    }

    private void updateSessionTitle(Long sessionId, String userMessage) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session != null && "新对话".equals(session.getTitle())) {
            String title = userMessage.length() > 20
                    ? userMessage.substring(0, 20) + "..."
                    : userMessage;
            session.setTitle(title);
            sessionMapper.updateById(session);
        }
    }

    public void deleteSession(Long sessionId) {
        messageMapper.delete(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId));
        sessionMapper.deleteById(sessionId);
    }
}
