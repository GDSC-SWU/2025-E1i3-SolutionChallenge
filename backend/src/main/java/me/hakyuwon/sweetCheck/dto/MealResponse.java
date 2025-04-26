package me.hakyuwon.sweetCheck.dto;

import lombok.Getter;
import lombok.Setter;
import me.hakyuwon.sweetCheck.domain.MealItem;

import java.util.List;

@Getter
@Setter
public class MealResponse {
    private String mealType;  // 아침, 점심, 저녁, 간식
    private String imageUrl;  // 이미지 URL
    private double totalSugar;  // 총 당류
    private List<MealItem> mealItems;
}
