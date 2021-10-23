package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class CommentListPagingDto {
    private List<CommentDetailDto> comments;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private int numberOfElements;
    private long offset;

    public boolean hasPrevious() {
        return pageNumber - 1 >= 0;
    }

    public boolean hasNext() {
        return pageNumber + 1 < totalPages;
    }
}