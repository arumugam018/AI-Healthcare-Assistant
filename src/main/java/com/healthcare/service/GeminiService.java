package com.healthcare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.healthcare.model.ChatMessage;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.util.Base64;

@Service
public class GeminiService {
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    @Value("${gemini.api.url:}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    
    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }
    
    @SuppressWarnings("unchecked")
    public String analyzeSymptoms(String symptoms) {
        String prompt = "You are a helpful AI healthcare assistant symptom checker. A patient reports the following symptoms: " + 
        symptoms + ". Provide a brief, professional, and empathetic preliminary assessment. Additionally, suggest common over-the-counter medications or home remedies that could help alleviate these symptoms. " +
        "DISCLAIMER: Always emphasize that this is an AI assessment, not medical advice, and they must consult a doctor or pharmacist before taking any medication. Format the output in clear Markdown.";
        
        for (int i = 0; i < 3; i++) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                ));
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                Map<String, Object> response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, entity, Map.class);
                
                if (response != null && response.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        return (String) parts.get(0).get("text");
                    }
                }
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 503 && i < 2) {
                    try { Thread.sleep(2000); } catch (InterruptedException ie) {}
                    continue;
                }
                System.err.println("Gemini API Http Error: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Gemini API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily busy. Please try again in a few moments.";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String analyzePillBottle(MultipartFile image) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg"; // fallback
            }

            String prompt = "You are a medical assistant reading a pill bottle. Please output ONLY a JSON object containing the keys 'medicineName' and 'dosage' extracted from the label. If you cannot find them, guess or return Unknown. Do not use markdown blocks, just raw JSON.";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt),
                    Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64Image))
                ))
            ));

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(apiUrl + "?key=" + apiKey, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"medicineName\":\"Error\", \"dosage\":\"Error\"}";
        }
        return "{\"medicineName\":\"Unknown\", \"dosage\":\"Unknown\"}";
    }

    @SuppressWarnings("unchecked")
    public String generateConversationalResponse(List<ChatMessage> history, String newMessage) {
        String systemPrompt = "You are CareSync AI, an intelligent and friendly healthcare assistant. " +
            "Your role: provide preliminary healthcare guidance, explain symptoms clearly, suggest general precautions, and recommend consulting doctors for serious symptoms. " +
            "Guidelines: answer conversationally and naturally, keep responses supportive and professional, avoid short robotic replies, explain possible causes simply, " +
            "provide useful healthcare suggestions, never claim final diagnosis, and encourage medical consultation when symptoms are severe.";
        
        for (int i = 0; i < 3; i++) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                
                // System Instruction
                requestBody.put("system_instruction", Map.of(
                    "parts", List.of(Map.of("text", systemPrompt))
                ));
                
                List<Map<String, Object>> contents = new ArrayList<>();
                
                // Add history
                if (history != null) {
                    for (ChatMessage msg : history) {
                        contents.add(Map.of(
                            "role", msg.getRole(),
                            "parts", List.of(Map.of("text", msg.getContent()))
                        ));
                    }
                }
                
                // Add new message
                contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", newMessage))
                ));
                
                requestBody.put("contents", contents);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                Map<String, Object> response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, entity, Map.class);
                
                if (response != null && response.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        return (String) parts.get(0).get("text");
                    }
                }
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 503 && i < 2) {
                    try { Thread.sleep(2000); } catch (InterruptedException ie) {}
                    continue;
                }
                System.err.println("Gemini API Http Error: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Gemini API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily busy. Please try again in a few moments.";
    }

    @SuppressWarnings("unchecked")
    public String analyzeVisionImage(byte[] imageBytes, String mimeType) {
        String systemPrompt = "You are an AI healthcare assistant providing preliminary healthcare guidance only.\n\n" +
                "Analyze the uploaded healthcare-related image and provide:\n" +
                "- general observation\n" +
                "- possible healthcare guidance\n" +
                "- precautions\n" +
                "- recommendation to consult healthcare professionals if necessary\n\n" +
                "Do not provide final medical diagnosis.\n" +
                "Do not claim disease detection.\n" +
                "Keep responses supportive and professional.";
        
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        for (int i = 0; i < 3; i++) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                
                // System Instruction
                requestBody.put("system_instruction", Map.of(
                    "parts", List.of(Map.of("text", systemPrompt))
                ));
                
                requestBody.put("contents", List.of(
                    Map.of(
                        "role", "user",
                        "parts", List.of(
                            Map.of("text", "Please analyze this image and provide general healthcare guidance based on what you see."),
                            Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64Image))
                        )
                    )
                ));
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                Map<String, Object> response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, entity, Map.class);
                
                if (response != null && response.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        return (String) parts.get(0).get("text");
                    }
                }
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 503 && i < 2) {
                    try { Thread.sleep(2000); } catch (InterruptedException ie) {}
                    continue;
                }
                System.err.println("Gemini Vision API Http Error: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Gemini Vision API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily busy. Please try again in a few moments.";
    }
}
 
