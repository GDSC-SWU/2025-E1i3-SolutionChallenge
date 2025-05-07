package me.hakyuwon.sweetCheck.service;

import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.dto.LoginResponse;
import me.hakyuwon.sweetCheck.dto.ProfileRequest;
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

    // 처음 구글 로그인 시 구글 정보 저장
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

    // 사용자 프로필 입력 정보 따로 저장
    public void saveUserProfile(ProfileRequest profileRequest) {
        try {
            DocumentReference docRef = firestore.collection("user_profiles").document(profileRequest.getUid());

            Map<String, Object> userProfileData = new HashMap<>();
            userProfileData.put("gender", profileRequest.getGender());
            userProfileData.put("height", profileRequest.getHeight());
            userProfileData.put("weight", profileRequest.getWeight());
            userProfileData.put("age", profileRequest.getAge());

            // 'user_profiles' 컬렉션에 추가 정보 저장
            docRef.set(userProfileData, SetOptions.merge()).get();
            log.info("User profile saved or updated.");
        } catch (Exception e) {
            log.error("Error saving user profile", e);
            throw new RuntimeException("Failed to save or update user profile");
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
