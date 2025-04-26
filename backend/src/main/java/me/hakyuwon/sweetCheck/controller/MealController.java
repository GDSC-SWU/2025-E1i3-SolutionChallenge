package me.hakyuwon.sweetCheck.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.hakyuwon.sweetCheck.dto.MealRequest;
import me.hakyuwon.sweetCheck.enums.MealType;
import me.hakyuwon.sweetCheck.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MealController {
    private final MealService mealService;

    // 오늘 식단 분석 결과 임시저장
    @PostMapping("/api/meals/{mealType}")
    public ResponseEntity<String> saveMeal(@PathVariable MealType mealType, @RequestBody MealRequest request
    ) {
        mealService.saveMealDraft(request, mealType);
        return ResponseEntity.ok("Meal saved successfully");
    }

    // 오늘 식단 내용 수정
    @PutMapping("/api/meals/{mealId}")
    public ResponseEntity<String> updateMealDraft(@PathVariable String mealId,
                                                @RequestBody MealRequest request) {
        mealService.updateMealDraft(request.getUserId(), mealId, request);
        return ResponseEntity.ok("Meal updated successfully");
    }

    @PostMapping("/api/meals/{mealId}/confirm")
    public ResponseEntity<String> confirmMeal(@PathVariable String mealId,
                                            @RequestParam String userId) {
        mealService.confirmMeal(userId, mealId);
        return ResponseEntity.ok("Meal confirmed successfully");
    }

}
