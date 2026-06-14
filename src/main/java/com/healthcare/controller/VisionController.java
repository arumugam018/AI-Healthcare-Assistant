package com.healthcare.controller;

import com.healthcare.model.VisionRecord;
import com.healthcare.service.VisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    private final VisionService visionService;

    public VisionController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId) {
        
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image upload failed.", "status", "error"));
        }

        try {
            VisionRecord record = visionService.processVisionRequest(image, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", record.getImageUrl());
            response.put("aiResponse", record.getAiResponse());
            response.put("timestamp", record.getTimestamp());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null || (!msg.equals("Image upload failed.") 
                    && !msg.equals("AI service temporarily unavailable.") 
                    && !msg.equals("Please try again."))) {
                msg = "Please try again.";
            }
            return ResponseEntity.status(500).body(Map.of(
                "error", msg,
                "status", "error"
            ));
        }
    }
}
 
