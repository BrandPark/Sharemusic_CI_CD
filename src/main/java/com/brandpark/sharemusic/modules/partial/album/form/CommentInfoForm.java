package com.brandpark.sharemusic.modules.partial.album.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentInfoForm {
    private Long id;
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String writerProfileImage;
}
