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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private InputStream getCredentialsStream() {
        try {
            String envJson = System.getenv("FIREBASE_CREDENTIALS_JSON");
            if (envJson != null && !envJson.trim().isEmpty()) {
                return new ByteArrayInputStream(envJson.getBytes(StandardCharsets.UTF_8));
            }
            File file = new File("firebase-service-account.json");
            if (file.exists()) {
                return new FileInputStream(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostConstruct
    public void init() {
        try {
            InputStream is = getCredentialsStream();
            if (is != null) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized successfully.");
                }
            } else {
                System.out.println("INFO: Running in cloud fallback mode (no firebase-service-account.json).");
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization notice: " + e.getMessage());
        }
    }

    @Bean
    public Firestore firestore() {
        if (FirebaseApp.getApps().isEmpty()) return null;
        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    public Storage storage() {
        try {
            InputStream is = getCredentialsStream();
            if (is != null) {
                return StorageOptions.newBuilder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .setProjectId("ai-healthcare-assistant-98292")
                        .build()
                        .getService();
            }
        } catch (Exception e) {
            System.err.println("Storage bean notice: " + e.getMessage());
        }
        return null;
    }
}
