package com.brandpark.sharemusic.api.v1.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum Error {
    BLANK_FIELD_EXCEPTION(BAD_REQUEST, "E0001", "비어있는 필드가 있습니다."),
    INVALID_TRACKS_COUNT_EXCEPTION(BAD_REQUEST, "E0002", "트랙의 수가 유효하지 않습니다."),
    DUPLICATE_TRACK_EXCEPTION(BAD_REQUEST, "E0003", "앨범에 중복된 트랙이 존재합니다."),
    DUPLICATE_ALBUM_TITLE_EXCEPTION(BAD_REQUEST, "E0004", "같은 이름의 앨범을 갖고 있습니다."),

    ILLEGAL_ACCESS_EXCEPTION(BAD_REQUEST, "E0005", "잘못된 접근입니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(BAD_REQUEST, "E0006", "잘못된 인자입니다."),
    NOT_FOUND_ACCOUNT_EXCEPTION(BAD_REQUEST, "E0007", "계정을 찾을 수 없습니다."),
    NOT_FOUND_ALBUM_EXCEPTION(BAD_REQUEST, "E0008", "앨범을 찾을 수 없습니다."),
    FORBIDDEN_EXCEPTION(FORBIDDEN, "E0009", "접근할 권한이 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    Error(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.defaultMessage = message;
    }
}
