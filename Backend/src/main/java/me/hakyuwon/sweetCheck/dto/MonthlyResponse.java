package me.hakyuwon.sweetCheck.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyResponse {
    private String month;
    private List<String> recordedDates;
}
