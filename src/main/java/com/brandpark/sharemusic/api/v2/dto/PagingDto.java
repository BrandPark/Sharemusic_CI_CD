package com.brandpark.sharemusic.api.v2.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagingDto<T> {
    private List<T> contents;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private int numberOfElements;
    private int paginationUnit = 10;
    private long offset;

    public boolean hasPrevious() {
        return pageNumber - 1 >= 0;
    }

    public boolean hasNext() {
        return pageNumber + 1 < totalPages;
    }
}
