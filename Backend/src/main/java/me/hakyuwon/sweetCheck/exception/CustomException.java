package me.hakyuwon.sweetCheck.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hakyuwon.sweetCheck.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
