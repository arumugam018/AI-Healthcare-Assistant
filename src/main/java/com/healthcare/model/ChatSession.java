package com.healthcare.model;

import java.util.ArrayList;
import java.util.List;

public class ChatSession {
    private String sessionId;
    private String userId;
    private List<ChatMessage> messages = new ArrayList<>();
    private long createdAt;

    public ChatSession() {
    }

    public ChatSession(String sessionId, String userId, long createdAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
 
