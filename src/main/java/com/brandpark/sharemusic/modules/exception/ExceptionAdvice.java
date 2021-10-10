package com.brandpark.sharemusic.modules.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenAccessException.class)
    public String forbiddenAccessHandler() {
        return "error/error";
    }
}
