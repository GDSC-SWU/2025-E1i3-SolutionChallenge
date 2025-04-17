package me.hakyuwon.sweetCheck.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.hakyuwon.sweetCheck.service.MealService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MealController {
    private final MealService mealService;


}
