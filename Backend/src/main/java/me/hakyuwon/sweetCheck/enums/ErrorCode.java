package me.hakyuwon.sweetCheck.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 BAD_REQUEST : 잘못된 요청
    INVALID_PARAMETER(400, "파라미터 값을 확인해주세요."),
    IMAGE_EMPTY(400, "업로드된 이미지가 없습니다."),
    UNSUPPORTED_IMAGE_TYPE(400, "지원하지 않는 이미지 형식입니다."),

    // 404 NOT_FOUND : 리소스를 찾을 수 없음
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    MEAL_NOT_FOUND(404, "해당 날짜의 식단 기록이 없습니다."),
    MENU_EXTRACTION_FAIL(404, "메뉴를 추출할 수 없습니다."),

    // 401 UNAUTHORIZED : 인증/인가 실패
    UNAUTHORIZED(401, "인증 정보가 유효하지 않습니다."),

    // 403 FORBIDDEN : 접근 권한 없음
    FORBIDDEN(403, "접근 권한이 없습니다."),

    // 409 CONFLICT : 중복 요청 등
    DUPLICATE_RECORD(409, "이미 존재하는 기록입니다."),

    // 500 INTERNAL_SERVER_ERROR : 서버 내부 오류
    VISION_API_ERROR(500, "이미지 분석 중 오류가 발생했습니다."),
    FIRESTORE_ERROR(500, "데이터베이스 연동에 실패했습니다."),
    RECOMMENDATION_API_ERROR(500, "추천 API 호출에 실패했습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

    private final int status;
    private final String message;
}
