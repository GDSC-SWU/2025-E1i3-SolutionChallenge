package me.hakyuwon.sweetCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponse {
        private String gender;
        private Integer height;
        private Integer weight;
        private Integer age;
}
