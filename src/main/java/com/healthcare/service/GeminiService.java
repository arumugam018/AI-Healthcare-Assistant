package com.healthcare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }
    
    private boolean isApiKeyMissing() {
        return apiKey == null || apiKey.trim().isEmpty() || apiKey.equalsIgnoreCase("YOUR_API_KEY");
    }

    @SuppressWarnings("unchecked")
    public String analyzeSymptoms(String symptoms) {
        if (isApiKeyMissing()) {
            return "Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable (e.g., set in `.env` or run `.\\run.bat YOUR_API_KEY`) to enable AI features.";
        }

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
                requestBody.put("generationConfig", Map.of(
                    "temperature", 0.4,
                    "maxOutputTokens", 1024
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
                int status = e.getStatusCode().value();
                System.err.println("Gemini API Http Error (" + status + "): " + e.getResponseBodyAsString());
                if (status == 503 && i < 2) {
                    try { Thread.sleep(500); } catch (InterruptedException ie) {}
                    continue;
                }
                if (status == 400 || status == 403) {
                    return "Invalid Gemini API key or request format (HTTP " + status + "). Please verify your GEMINI_API_KEY.";
                }
                if (status == 429) {
                    return "Gemini API rate limit exceeded. Please wait a moment and try again.";
                }
                break;
            } catch (Exception e) {
                System.err.println("Gemini API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily unavailable. Please verify API key and network connection.";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String analyzePillBottle(MultipartFile image) {
        if (isApiKeyMissing()) {
            return "{\"medicineName\":\"API Key Not Configured\", \"dosage\":\"Set GEMINI_API_KEY\"}";
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }

            String prompt = "You are a medical assistant reading a pill bottle. Output ONLY a JSON object with keys 'medicineName' and 'dosage'. If unreadable, return Unknown. No markdown, raw JSON only.";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt),
                    Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64Image))
                ))
            ));
            requestBody.put("generationConfig", Map.of(
                "temperature", 0.1,
                "maxOutputTokens", 256
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
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
            return "{\"medicineName\":\"Error\", \"dosage\":\"" + e.getMessage() + "\"}";
        }
        return "{\"medicineName\":\"Unknown\", \"dosage\":\"Unknown\"}";
    }

    @SuppressWarnings("unchecked")
    public String generateConversationalResponse(List<ChatMessage> history, String newMessage) {
        if (isApiKeyMissing()) {
            return "Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable (e.g., set in `.env` or run `.\\run.bat YOUR_API_KEY`) to enable AI features.";
        }

        String systemPrompt = "You are CareSync AI, an intelligent and friendly healthcare assistant. " +
            "Your role: provide preliminary healthcare guidance, explain symptoms clearly, suggest general precautions, and recommend consulting doctors for serious symptoms. " +
            "Guidelines: answer conversationally and naturally, keep responses supportive and professional, avoid short robotic replies, explain possible causes simply, " +
            "provide useful healthcare suggestions, never claim final diagnosis, and encourage medical consultation when symptoms are severe.";
        
        for (int i = 0; i < 3; i++) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                
                requestBody.put("system_instruction", Map.of(
                    "parts", List.of(Map.of("text", systemPrompt))
                ));
                
                List<Map<String, Object>> contents = new ArrayList<>();
                if (history != null) {
                    for (ChatMessage msg : history) {
                        contents.add(Map.of(
                            "role", msg.getRole(),
                            "parts", List.of(Map.of("text", msg.getContent()))
                        ));
                    }
                }
                
                contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", newMessage))
                ));
                
                requestBody.put("contents", contents);
                requestBody.put("generationConfig", Map.of(
                    "temperature", 0.5,
                    "maxOutputTokens", 1024
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
                int status = e.getStatusCode().value();
                System.err.println("Gemini API Http Error (" + status + "): " + e.getResponseBodyAsString());
                if (status == 503 && i < 2) {
                    try { Thread.sleep(500); } catch (InterruptedException ie) {}
                    continue;
                }
                if (status == 400 || status == 403) {
                    return "Invalid Gemini API key or request format (HTTP " + status + "). Please verify your GEMINI_API_KEY.";
                }
                if (status == 429) {
                    return "Gemini API rate limit exceeded. Please wait a moment and try again.";
                }
                break;
            } catch (Exception e) {
                System.err.println("Gemini API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily unavailable. Please verify API key and network connection.";
    }

    @SuppressWarnings("unchecked")
    public String analyzeVisionImage(byte[] imageBytes, String mimeType) {
        if (isApiKeyMissing()) {
            return "Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable (e.g., set in `.env` or run `.\\run.bat YOUR_API_KEY`) to enable AI features.";
        }

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
                requestBody.put("generationConfig", Map.of(
                    "temperature", 0.3,
                    "maxOutputTokens", 1024
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
                int status = e.getStatusCode().value();
                System.err.println("Gemini Vision API Http Error (" + status + "): " + e.getResponseBodyAsString());
                if (status == 503 && i < 2) {
                    try { Thread.sleep(500); } catch (InterruptedException ie) {}
                    continue;
                }
                if (status == 400 || status == 403) {
                    return "Invalid Gemini API key or request format (HTTP " + status + "). Please verify your GEMINI_API_KEY.";
                }
                if (status == 429) {
                    return "Gemini API rate limit exceeded. Please wait a moment and try again.";
                }
                break;
            } catch (Exception e) {
                System.err.println("Gemini Vision API Error: " + e.getMessage());
                break;
            }
        }
        return "AI service is temporarily unavailable. Please verify API key and network connection.";
    }
}
