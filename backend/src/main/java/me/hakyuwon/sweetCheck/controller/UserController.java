package me.hakyuwon.sweetCheck.controller;

import lombok.AllArgsConstructor;
import me.hakyuwon.sweetCheck.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "home"; // home.html 렌더링
    }


}
