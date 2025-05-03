package me.hakyuwon.sweetCheck.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class DailyImageResponse {
    private String date;
    private List<String> imageUrls;
}
