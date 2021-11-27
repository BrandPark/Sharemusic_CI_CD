package com.brandpark.sharemusic.api.v1.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentInfoResponse {
    private Long id;
    private Long accountId;
    private Long albumId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
