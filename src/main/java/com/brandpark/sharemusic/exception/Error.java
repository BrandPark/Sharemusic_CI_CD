package com.brandpark.sharemusic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
public enum Error {
    BLANK_FIELD_EXCEPTION(BAD_REQUEST, "E0001", "비어있는 필드가 있습니다."),
    INVALID_TRACKS_COUNT_EXCEPTION(BAD_REQUEST, "E0002", "트랙의 수가 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    Error(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.defaultMessage = message;
    }
}
