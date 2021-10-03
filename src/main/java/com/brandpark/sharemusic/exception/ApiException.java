package com.brandpark.sharemusic.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final Error error;
    private final String message;

    public ApiException(Error error, String message) {
        this.error = error;
        this.message = message;
    }

    public ApiException(Error error) {
        this.error = error;
        this.message = error.getDefaultMessage();
    }
}
