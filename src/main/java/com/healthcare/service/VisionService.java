package com.healthcare.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.healthcare.model.VisionRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VisionService {

    private final Firestore firestore;
    private final GeminiService geminiService;
    private final Storage storage;

    public VisionService(Firestore firestore, GeminiService geminiService, Storage storage) {
        this.firestore = firestore;
        this.geminiService = geminiService;
        this.storage = storage;
    }

    @SuppressWarnings("null")
    public VisionRecord processVisionRequest(MultipartFile image, String userId) throws Exception {
        if (storage == null || firestore == null) {
            throw new RuntimeException("AI service temporarily unavailable.");
        }

        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed.");
        }

        String contentType = image.getContentType();
        if (contentType == null) {
            contentType = "image/jpeg";
        }
        String originalFileName = image.getOriginalFilename();
        if (originalFileName == null) {
            originalFileName = "captured_image.jpg";
        }

        // 1. Upload image to Firebase Storage
        String recordId = UUID.randomUUID().toString();
        String extension = "jpg";
        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        }
        String storagePath = "vision/" + recordId + "." + extension;

        String imageUrl = null;
        Blob blob = null;

        // Try primary bucket
        String primaryBucket = "ai-healthcare-assistant-98292.appspot.com";
        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(primaryBucket, storagePath)
                    .setContentType(contentType)
                    .build();
            blob = storage.create(blobInfo, imageBytes);
            imageUrl = blob.signUrl(365, TimeUnit.DAYS).toString();
        } catch (Exception e) {
            System.err.println("Upload to primary bucket " + primaryBucket + " failed: " + e.getMessage());
            
            // Try fallback bucket
            String fallbackBucket = "ai-healthcare-assistant-98292.firebasestorage.app";
            try {
                BlobInfo blobInfo = BlobInfo.newBuilder(fallbackBucket, storagePath)
                        .setContentType(contentType)
                        .build();
                blob = storage.create(blobInfo, imageBytes);
                imageUrl = blob.signUrl(365, TimeUnit.DAYS).toString();
            } catch (Exception ex) {
                System.err.println("Upload to fallback bucket " + fallbackBucket + " failed: " + ex.getMessage());
                System.out.println("Using base64 Data URL fallback for image storage.");
                
                // Fallback to base64 Data URL so the application remains fully functional
                String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                imageUrl = "data:" + contentType + ";base64," + base64Image;
            }
        }

        // 2. Send image to Gemini Vision AI
        String aiResponse;
        try {
            aiResponse = geminiService.analyzeVisionImage(imageBytes, contentType);
            if (aiResponse == null || aiResponse.contains("temporarily busy")) {
                throw new RuntimeException("AI service temporarily unavailable.");
            }
        } catch (Exception e) {
            System.err.println("Gemini Vision Error: " + e.getMessage());
            throw new RuntimeException("AI service temporarily unavailable.");
        }

        // 3. Store metadata in Firestore
        VisionRecord record = new VisionRecord(recordId, userId, imageUrl, aiResponse, originalFileName, contentType);
        try {
            firestore.collection("vision_records").document(recordId).set(record).get();
        } catch (Exception e) {
            System.err.println("Firestore Error: " + e.getMessage());
            throw new RuntimeException("Please try again.");
        }

        return record;
    }
}
 
