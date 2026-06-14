package com.healthcare.controller;

import com.healthcare.model.ChatMessage;
import com.healthcare.model.ChatSession;
import com.healthcare.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String userId = payload.getOrDefault("userId", "anonymous");
        String message = payload.get("message");

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ChatMessage responseMessage = chatService.processMessage(sessionId, userId, message);
        
        // If sessionId was null, we need to return the newly created one
        // Wait, how do we get the sessionId if we just created it? 
        // We can just fetch it from the ChatService or modify processMessage to return the whole session.
        // Let's create a response map.
        // We actually need the session id in case it was just generated.
        // A better way is to ensure sessionId is passed, or if null, we generate one here or in frontend.
        // Usually, the frontend generates a UUID for the session, or the backend returns it.
        // Let's assume frontend passes a generated sessionId if new, or null.
        // If null, we'll fetch the session created from the responseMessage? No, message doesn't have sessionId.
        // Let's just return the message and let frontend handle sessionId generation before sending.

        Map<String, Object> response = new HashMap<>();
        response.put("message", responseMessage);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ChatSession> getHistory(@PathVariable String sessionId) {
        ChatSession session = chatService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }
}
 
