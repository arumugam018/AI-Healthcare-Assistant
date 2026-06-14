package com.healthcare.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthcare.model.ChatMessage;
import com.healthcare.model.ChatSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    @Autowired(required = false)
    private Firestore firestore;

    private final GeminiService geminiService;

    public ChatService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public ChatSession getSession(String sessionId) {
        if (firestore == null) return null;
        try {
            if (sessionId == null) return null;
            DocumentReference docRef = firestore.collection("chatSessions").document(sessionId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(ChatSession.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("null")
    public ChatSession createSession(String userId) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setCreatedAt(System.currentTimeMillis());
        session.setMessages(new ArrayList<>());
        
        if (firestore != null && session.getSessionId() != null) {
            firestore.collection("chatSessions").document(session.getSessionId()).set(session);
        }
        return session;
    }

    public ChatMessage processMessage(String sessionId, String userId, String content) {
        ChatSession session = getSession(sessionId);
        if (session == null) {
            session = createSession(userId);
            sessionId = session.getSessionId();
        }

        // Add user message
        ChatMessage userMessage = new ChatMessage(UUID.randomUUID().toString(), "user", content, System.currentTimeMillis());
        session.getMessages().add(userMessage);

        // Generate AI response
        String aiResponseText = geminiService.generateConversationalResponse(session.getMessages().subList(0, session.getMessages().size() - 1), content);
        
        // Add model message
        ChatMessage modelMessage = new ChatMessage(UUID.randomUUID().toString(), "model", aiResponseText, System.currentTimeMillis());
        session.getMessages().add(modelMessage);

        // Save session back to Firestore
        if (firestore != null && sessionId != null) {
            firestore.collection("chatSessions").document(sessionId).set(session);
            // Optionally, store messages separately in chatMessages if needed for complex queries
            // but for simple chat history, storing inside session document is sufficient.
        }

        return modelMessage;
    }
}
 
