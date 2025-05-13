package me.hakyuwon.sweetCheck.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing Firebase.");
        FileInputStream serviceAccount =
                new FileInputStream("/home/sonnet829/sweet-check-firebase-adminsdk-fbsvc-c98e76606d.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp app;
        if (FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized: " + app.getName());
        } else {
            app = FirebaseApp.getInstance();
            log.info("FirebaseApp already initialized: " + app.getName());
        }
        return app;
    }

        @Bean
        public FirebaseAuth getFirebaseAuth() throws IOException {
            return FirebaseAuth.getInstance(firebaseApp());
        }

    @Bean
    public Firestore getFirestore() throws IOException {
        return FirestoreClient.getFirestore();
    }
}
