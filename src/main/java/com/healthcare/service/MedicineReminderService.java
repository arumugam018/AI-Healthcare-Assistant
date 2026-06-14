package com.healthcare.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthcare.model.MedicineReminder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class MedicineReminderService {
    
    @Autowired(required = false)
    private Firestore firestore;

    @SuppressWarnings("null")
    public MedicineReminder addReminder(@NonNull MedicineReminder reminder) {
        reminder.setId(UUID.randomUUID().toString());
        if (firestore != null) {
            firestore.collection("reminders").document(reminder.getId()).set(reminder);
        }
        return reminder;
    }

    public List<MedicineReminder> getAllReminders() {
        List<MedicineReminder> list = new ArrayList<>();
        if (firestore == null) return list;
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("reminders").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                list.add(document.toObject(MedicineReminder.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return list;
    }

    @SuppressWarnings("null")
    public void deleteReminder(@NonNull String id) {
        if (firestore == null) return;
        firestore.collection("reminders").document(id).delete();
    }
}
 
