package com.healthcare.controller;

import com.healthcare.model.SymptomCheck;
import com.healthcare.service.SymptomCheckerService;
import com.healthcare.service.PdfExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomCheckerController {
    private final SymptomCheckerService service;
    private final PdfExportService pdfService;

    public SymptomCheckerController(SymptomCheckerService service, PdfExportService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping("/check")
    public ResponseEntity<SymptomCheck> checkSymptoms(@RequestBody Map<String, String> payload) {
        String symptoms = payload.get("symptoms");
        if (symptoms == null || symptoms.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        SymptomCheck result = service.checkSymptoms(symptoms);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SymptomCheck>> getHistory() {
        return ResponseEntity.ok(service.getAllHistory());
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody java.util.Map<String, String> payload) {
        String symptoms = payload.get("symptoms");
        String aiResponse = payload.get("aiResponse");
        byte[] pdf = pdfService.generateSymptomReportPdf(symptoms, aiResponse);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "CareSync_Report.pdf");
        
        return new ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}
 
