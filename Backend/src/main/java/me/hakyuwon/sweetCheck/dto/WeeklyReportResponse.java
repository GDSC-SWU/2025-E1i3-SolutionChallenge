package me.hakyuwon.sweetCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyReportResponse {
    double peopleAverage;       // 성별, 나이 기반 평균 정보
    double lastWeekAverage;     // g 단위
    double thisWeekAverage;
}
