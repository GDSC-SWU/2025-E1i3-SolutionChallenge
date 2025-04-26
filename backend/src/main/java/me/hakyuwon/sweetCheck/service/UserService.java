package me.hakyuwon.sweetCheck.service;

import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private final Firestore firestore;

    public void saveOrUpdateUser(String email, String name, String picture) {
        DocumentReference docRef = firestore.collection("users").document(email);

        docRef.get().addListener(() -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("name", name);
            userData.put("profileImage", picture);

            // 새 유저면 createdAt 추가
            try {
                DocumentSnapshot snapshot = docRef.get().get();
                if (!snapshot.exists()) {
                    userData.put("createdAt", new Date());
                }
                docRef.set(userData, SetOptions.merge()).get();
                log.info("User saved or updated.");
            } catch (Exception e) {
                log.error("Error saving user", e);
            }
        }, Runnable::run);
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
    }
}
