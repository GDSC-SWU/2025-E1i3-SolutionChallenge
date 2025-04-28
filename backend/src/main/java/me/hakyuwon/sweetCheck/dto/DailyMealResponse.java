package me.hakyuwon.sweetCheck.dto;

import lombok.Getter;
import lombok.Setter;
import me.hakyuwon.sweetCheck.domain.Meal;

import java.util.List;

@Getter
@Setter
public class DailyMealResponse {
    private String date;
    private double dailyTotalSugar;
    private List<Meal> meals;
}
