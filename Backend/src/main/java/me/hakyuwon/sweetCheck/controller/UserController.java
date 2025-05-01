package me.hakyuwon.sweetCheck.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.sweetCheck.dto.DailyMealResponse;
import me.hakyuwon.sweetCheck.dto.LoginResponse;
import me.hakyuwon.sweetCheck.dto.TokenRequest;
import me.hakyuwon.sweetCheck.service.MealService;
import me.hakyuwon.sweetCheck.service.UserService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MealService mealService;

    @GetMapping("/")
    public String init () {
        return "firebase-login";
    }

    @ResponseBody
    @GetMapping("/api/home")
    public DailyMealResponse home(@AuthenticationPrincipal UserDetails userDetails) {
        String uid = userDetails.getUsername();
        DailyMealResponse dailyMealResponse = new DailyMealResponse();
        dailyMealResponse = mealService.getDailyMeals(uid, LocalDate.now());
        return dailyMealResponse ;
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<LoginResponse> login(@RequestBody TokenRequest tokenRequest) {
        LoginResponse response = userService.login(tokenRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
