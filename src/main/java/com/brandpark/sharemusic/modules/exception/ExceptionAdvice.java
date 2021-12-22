package com.brandpark.sharemusic.modules.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenAccessException.class)
    public String forbiddenAccessHandler() {
        log.error("권한이 없습니다.");
        return "error/error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception ex, Model model) {
        String message = "서버에서 데이터를 처리하는 중 예기치 못한 에러가 발생했습니다.";

        log.error(message);
        log.error(ex.getMessage());

        model.addAttribute("message", message);
        return "error/error";
    }
}
