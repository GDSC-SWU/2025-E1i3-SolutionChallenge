package me.hakyuwon.sweetCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String uid;
    private String email;
    private String name;
    private String profileImage;
}
