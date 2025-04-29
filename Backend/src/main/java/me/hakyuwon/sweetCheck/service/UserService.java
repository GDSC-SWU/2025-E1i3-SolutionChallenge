package me.hakyuwon.sweetCheck.service;

import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.dto.LoginResponse;
import me.hakyuwon.sweetCheck.dto.TokenRequest;
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

    public String saveOrUpdateUser(String uid, String email, String name, String picture) {
        try {
            DocumentReference docRef = firestore.collection("users").document(uid);

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("name", name);
            userData.put("profileImage", picture);

            DocumentSnapshot snapshot = docRef.get().get();
            if (!snapshot.exists()) {
                userData.put("createdAt", new Date());
            }

            docRef.set(userData, SetOptions.merge()).get();  // 동기 처리
            log.info("User saved or updated.");
            return uid;

        } catch (Exception e) {
            log.error("Error saving user", e);
            throw new RuntimeException("Failed to save or update user");
        }
    }
    public LoginResponse login(TokenRequest tokenRequest) {
        try {
            String idToken = tokenRequest.getToken();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = (String) decodedToken.getClaims().get("name");
            String picture = (String) decodedToken.getClaims().get("picture");

            return new LoginResponse(uid, email, name, picture);

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Firebase ID token");
        }
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
    }
}
