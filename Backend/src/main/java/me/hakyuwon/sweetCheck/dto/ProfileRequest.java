package me.hakyuwon.sweetCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileRequest {
    private String uid;  // 구글 UID
    private String gender;
    private Integer height;
    private Integer weight;
    private Integer age;
}
