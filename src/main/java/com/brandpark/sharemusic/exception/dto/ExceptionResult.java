package com.brandpark.sharemusic.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ExceptionResult {
    private String errorCode;
    private String errorMessage;
}
