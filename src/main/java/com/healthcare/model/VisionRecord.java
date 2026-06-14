package com.healthcare.model;

import java.time.LocalDateTime;

public class VisionRecord {
    private String recordId;
    private String userId;
    private String imageUrl;
    private String timestamp;
    private String aiResponse;
    private String imageName;
    private String contentType;

    public VisionRecord() {}

    public VisionRecord(String recordId, String userId, String imageUrl, String aiResponse, String imageName, String contentType) {
        this.recordId = recordId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.aiResponse = aiResponse;
        this.imageName = imageName;
        this.contentType = contentType;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
 
