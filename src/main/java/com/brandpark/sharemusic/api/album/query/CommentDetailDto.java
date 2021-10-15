package com.brandpark.sharemusic.api.album.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDetailDto {
    private Long id;
    private String writer;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private String writerProfileImage;
}
