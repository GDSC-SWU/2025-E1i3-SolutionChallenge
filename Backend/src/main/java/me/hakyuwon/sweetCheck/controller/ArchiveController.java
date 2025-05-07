package me.hakyuwon.sweetCheck.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.sweetCheck.dto.DailyImageResponse;
import me.hakyuwon.sweetCheck.dto.MonthlyResponse;
import me.hakyuwon.sweetCheck.service.ArchiveService;
import me.hakyuwon.sweetCheck.service.MealService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    // 날짜별 식단 이미지 반환
    @GetMapping("/api/meals/images")
    public ResponseEntity<DailyImageResponse> getDailyImageUrls(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyImageResponse response = archiveService.getDailyImageUrls(date, userId);
        return ResponseEntity.ok(response);
    }

    // 달력 볼드 표시용 (기록한 날짜 반환)
    @GetMapping("/api/meals/summary")
    public ResponseEntity<MonthlyResponse> getMonthlyMealDates(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        MonthlyResponse response = archiveService.getMonthlyMealDates(userId, month);
        return ResponseEntity.ok(response);
    }
}
