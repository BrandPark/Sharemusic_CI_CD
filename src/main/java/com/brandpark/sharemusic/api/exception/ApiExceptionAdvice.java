package com.brandpark.sharemusic.api.exception;

import com.brandpark.sharemusic.api.exception.dto.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResult> exceptionHandler(ApiException ex) {

        Error error = ex.getError();

        ExceptionResult result = ExceptionResult.builder()
                .errorCode(error.getCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity
                .status(error.getStatus())
                .body(result);
    }
}
