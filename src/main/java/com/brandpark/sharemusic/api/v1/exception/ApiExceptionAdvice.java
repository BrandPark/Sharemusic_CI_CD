package com.brandpark.sharemusic.api.v1.exception;

import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                .header("Content-Type", "application/json;charset=utf-8")
                .body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResult> exceptionHandler(MethodArgumentNotValidException ex) {

        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);

        String errorMessage = String.format("%s : {input value = %s}", error.getDefaultMessage(), error.getRejectedValue());

        ExceptionResult result = ExceptionResult.builder()
                .errorCode(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode())
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json;charset=utf-8")
                .body(result);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResult> exceptionHandler(Exception ex) {
//
//        String errorMessage = String.format("예기치 못한 상황이 발생했습니다.\n %s", ex.getMessage());
//
//        ExceptionResult result = ExceptionResult.builder()
//                .errorCode(Error.INTERNAL_SERVER_EXCEPTION.getCode())
//                .errorMessage(errorMessage)
//                .build();
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .header("Content-Type", "application/json;charset=utf-8")
//                .body(result);
//    }
}
