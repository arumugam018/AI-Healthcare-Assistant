package com.healthcare.model;

import java.time.LocalDateTime;

public class SymptomCheck {
    private String id;
    private String symptoms;
    private String aiResponse;
    private String timestamp = LocalDateTime.now().toString();

    public SymptomCheck() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
 
