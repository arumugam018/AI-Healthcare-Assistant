package com.healthcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            File file = new File("firebase-service-account.json");
            if (file.exists()) {
                FileInputStream serviceAccount = new FileInputStream(file);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized successfully.");
                }
            } else {
                System.err.println("WARNING: firebase-service-account.json NOT FOUND! Firebase connections will fail. Please create this file in the project root.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public Firestore firestore() {
        if (FirebaseApp.getApps().isEmpty()) return null;
        return FirestoreClient.getFirestore();
    }

    @Bean
    public Storage storage() {
        try {
            File file = new File("firebase-service-account.json");
            if (file.exists()) {
                FileInputStream serviceAccount = new FileInputStream(file);
                return StorageOptions.newBuilder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId("ai-healthcare-assistant-98292")
                        .build()
                        .getService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
 
