package com.brandpark.sharemusic.api.v1.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExceptionResult {
    private String errorCode;
    private String errorMessage;
}
