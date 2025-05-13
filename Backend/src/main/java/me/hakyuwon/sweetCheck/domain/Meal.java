package me.hakyuwon.sweetCheck.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.hakyuwon.sweetCheck.enums.MealStatus;
import me.hakyuwon.sweetCheck.enums.MealType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    private String imageUrl;
    private Date mealDateTime;
    private MealType mealType; // morning, lunch, dinner, snack
    private double totalSugar;
    private MealStatus status;
}
