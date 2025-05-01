package me.hakyuwon.sweetCheck.controller;


import lombok.RequiredArgsConstructor;
import me.hakyuwon.sweetCheck.dto.DailyMealResponse;
import me.hakyuwon.sweetCheck.dto.MealRequest;
import me.hakyuwon.sweetCheck.dto.SugarResponse;
import me.hakyuwon.sweetCheck.enums.MealType;
import me.hakyuwon.sweetCheck.service.MealService;
import me.hakyuwon.sweetCheck.util.SecurityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
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

    // 식단 확정
    @PostMapping("/api/meals/{mealId}/confirm")
    public ResponseEntity<String> confirmMeal(@PathVariable String mealId) {
        String userId = SecurityUtil.getCurrentUserId();
        mealService.confirmMeal(userId, mealId);
        return ResponseEntity.ok("Meal confirmed successfully");
    }

    // 일별 식단 조회
    @GetMapping("/api/meals/{date}")
    public ResponseEntity<DailyMealResponse> getDailyMeals(@PathVariable String date
    ) {
        String userId = SecurityUtil.getCurrentUserId();
        LocalDate localDate = LocalDate.parse(date);
        DailyMealResponse response = mealService.getDailyMeals(userId, localDate);
        return ResponseEntity.ok(response);
    }

    // 오늘 당류 분석
    @GetMapping("/api/meals/result")
    public ResponseEntity<List<SugarResponse>> getDailySugar(@RequestParam String userId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        {
            // 기본값 오늘
            if (date == null) {
                date = LocalDate.now();
            }

            List<SugarResponse> sugarResponses = mealService.getDailySugar(userId, date);
            return ResponseEntity.ok(sugarResponses);
        }
    }

}

