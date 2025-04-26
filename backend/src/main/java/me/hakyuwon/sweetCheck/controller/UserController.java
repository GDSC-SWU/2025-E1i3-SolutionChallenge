package me.hakyuwon.sweetCheck.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import me.hakyuwon.sweetCheck.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/home")
    public String home() {
        return "home"; // home.html 렌더링
    }

    @DeleteMapping("/api/users/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uid) {
        try {
            userService.deleteUser(uid);
            return ResponseEntity.noContent().build();
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
