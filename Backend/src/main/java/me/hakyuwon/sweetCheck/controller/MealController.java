package me.hakyuwon.sweetCheck.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.sweetCheck.domain.MealItem;
import me.hakyuwon.sweetCheck.dto.DailyMealResponse;
import me.hakyuwon.sweetCheck.dto.MealRequest;
import me.hakyuwon.sweetCheck.enums.MealType;
import me.hakyuwon.sweetCheck.service.AnalyzeService;
import me.hakyuwon.sweetCheck.service.MealService;
import me.hakyuwon.sweetCheck.util.SecurityUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MealController {
    private final MealService mealService;
    private final AnalyzeService analyzeService;

    @PostMapping("/api/analyze-meals")
    public ResponseEntity<?> analyzeMealDay(
            @RequestParam("morning") MultipartFile morning,
            @RequestParam("lunch") MultipartFile lunch,
            @RequestParam("dinner") MultipartFile dinner,
            @RequestParam("snack") MultipartFile snack,
            @RequestParam(value="mealDate",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mealDate,
            @RequestParam("userId") String userId
    ) {
        if (mealDate == null) {
            LocalDate today = LocalDate.now();
            mealDate = today;
        }
        try {
            Map<String, MultipartFile> images = Map.of(
                    "morning", morning,
                    "lunch", lunch,
                    "dinner", dinner,
                    "snack", snack
            );

            // 파일 이름 포함된 Resource 생성
            Map<String, Resource> resources = new HashMap<>();
            for (Map.Entry<String, MultipartFile> entry : images.entrySet()) {
                String filename = entry.getKey() + ".jpg";
                ByteArrayResource resource = new ByteArrayResource(entry.getValue().getBytes()) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                };
                resources.put(entry.getKey(), resource);
            }

            // FastAPI 분석 호출
            Map<String, Object> analysisResult = analyzeService.analyzeDay(resources).block();
            Map<String, Object> mealsMap = (Map<String, Object>) analysisResult.get("meals");

            // 식사별로 임시 저장
            for (String mealTypeKey : mealsMap.keySet()) {
                try {
                    MealType mealType = MealType.valueOf(mealTypeKey.toUpperCase());
                    Map<String, Object> mealData = (Map<String, Object>) mealsMap.get(mealTypeKey);

                    MealRequest mealRequest = new MealRequest();
                    mealRequest.setUserId(userId);
                    mealRequest.setMealDateTime(mealDate.atTime(8, 0).toString()); // default 시간
                    mealRequest.setMealType(mealType.name());
                    mealRequest.setImageUrl("https://dummy.url/" + mealTypeKey); // TODO: 실제 이미지 저장 URL로 변경
                    mealRequest.setTotalSugar(((Number) mealData.get("total_sugar")).doubleValue());

                    // 음식 정보
                    List<MealItem> items = new ArrayList<>();
                    Map<String, Object> foodSugarData = (Map<String, Object>) mealData.get("food_sugar_data");
                    for (Map.Entry<String, Object> entry : foodSugarData.entrySet()) {
                        MealItem item = new MealItem();
                        item.setName(entry.getKey());

                        Object sugarValue = entry.getValue();
                        if (sugarValue instanceof Number) {
                            item.setSugarPer100(((Number) sugarValue).doubleValue());
                        } else {
                            item.setSugarPer100(0.0); // 추정값이 문자열인 경우
                        }
                        items.add(item);
                    }
                    mealRequest.setItems(items);
                    mealService.saveMealDraft(mealRequest, mealType);

                } catch (IllegalArgumentException e) {
                    log.warn("알 수 없는 식사 유형 key: {}", mealTypeKey);
                    continue;
                }
            }
            return ResponseEntity.ok("분석 및 임시 저장 완료");
        } catch (Exception e) {
            log.error("식사 분석 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("분석 실패: " + e.getMessage());
        }
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
    public ResponseEntity<Map<String, Object>> getDailySugar(@RequestParam String userId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        {
            if (date == null) {
                date = LocalDate.now();
            }
            Map<String, Object> result = mealService.getDailySugar(userId, date);
            return ResponseEntity.ok(result);
        }
    }


}

