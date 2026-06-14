package com.healthcare.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthcare.model.SymptomCheck;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class SymptomCheckerService {
    
    @Autowired(required = false)
    private Firestore firestore;
    
    private final GeminiService geminiService;

    public SymptomCheckerService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @SuppressWarnings("null")
    public SymptomCheck checkSymptoms(String symptoms) {
        String aiResponse = geminiService.analyzeSymptoms(symptoms);
        SymptomCheck check = new SymptomCheck();
        check.setId(UUID.randomUUID().toString());
        check.setSymptoms(symptoms);
        check.setAiResponse(aiResponse);
        if (firestore != null) {
            firestore.collection("symptom_history").document(check.getId()).set(check);
        }
        return check;
    }

    public List<SymptomCheck> getAllHistory() {
        List<SymptomCheck> list = new ArrayList<>();
        if (firestore == null) return list;
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("symptom_history").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                list.add(document.toObject(SymptomCheck.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return list;
    }
}
 
