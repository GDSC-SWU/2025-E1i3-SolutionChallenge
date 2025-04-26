package me.hakyuwon.sweetCheck.dto;

import lombok.Setter;
import me.hakyuwon.sweetCheck.domain.Meal;

import java.util.List;

@Setter
public class DailyMealResponse {
    private String date;
    private double dailyTotalSugar;
    private List<Meal> meals;
}
