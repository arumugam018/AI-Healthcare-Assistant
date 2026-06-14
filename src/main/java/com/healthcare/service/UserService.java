package com.healthcare.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthcare.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    
    @Autowired(required = false)
    private Firestore firestore;

    public User findByUsername(String username) {
        if (firestore == null) return null;
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection("users").whereEqualTo("username", username).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            System.out.println("UserService found " + documents.size() + " documents for username: " + username);
            if (!documents.isEmpty()) {
                User user = documents.get(0).toObject(User.class);
                System.out.println("Mapped User: ID=" + user.getId() + ", Username=" + user.getUsername() + ", Password=" + user.getPassword());
                return user;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("null")
    public void save(User user) {
        if (firestore == null) return;
        user.setId(UUID.randomUUID().toString());
        DocumentReference docRef = firestore.collection("users").document(user.getId());
        try {
            docRef.set(user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
 
