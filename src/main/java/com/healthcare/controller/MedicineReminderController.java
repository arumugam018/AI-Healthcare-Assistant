package com.healthcare.controller;

import com.healthcare.model.MedicineReminder;
import com.healthcare.service.MedicineReminderService;
import com.healthcare.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class MedicineReminderController {
    private final MedicineReminderService service;
    private final GeminiService geminiService;

    public MedicineReminderController(MedicineReminderService service, GeminiService geminiService) {
        this.service = service;
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<MedicineReminder> addReminder(@RequestBody MedicineReminder reminder) {
        return ResponseEntity.ok(service.addReminder(reminder));
    }

    @GetMapping
    public ResponseEntity<List<MedicineReminder>> getAllReminders() {
        return ResponseEntity.ok(service.getAllReminders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable String id) {
        service.deleteReminder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/scan-pill")
    public ResponseEntity<String> scanPillBottle(@RequestParam("image") MultipartFile image) {
        try {
            String jsonResult = geminiService.analyzePillBottle(image);
            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Failed to scan image\"}");
        }
    }
}
 
