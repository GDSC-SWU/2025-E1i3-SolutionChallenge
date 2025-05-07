package me.hakyuwon.sweetCheck.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealItem {
    private String name;
    private double sugarPer100;
    private String unit;
}
