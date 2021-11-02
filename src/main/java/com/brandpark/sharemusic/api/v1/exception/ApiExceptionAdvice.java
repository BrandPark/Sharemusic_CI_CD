package com.brandpark.sharemusic.api.v1.exception;

import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResult> exceptionHandler(ApiException ex) {

        Error error = ex.getError();
        log.error("ApiException : errorCode={}, errorMessage={}", error.getCode(), ex.getMessage());

        ExceptionResult result = ExceptionResult.builder()
                .errorCode(error.getCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity
                .status(error.getStatus())
                .body(result);
    }
}
