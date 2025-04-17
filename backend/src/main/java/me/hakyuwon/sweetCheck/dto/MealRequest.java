package me.hakyuwon.sweetCheck.dto;

import lombok.Getter;
import me.hakyuwon.sweetCheck.domain.MealItem;

import java.util.List;

@Getter
public class MealRequest {
    private String userId;
    private String imageUrl;
    private String mealDateTime; // ISO-8601 문자열
    private String mealType;
    private double totalSugar;
    private List<MealItem> items;

}
