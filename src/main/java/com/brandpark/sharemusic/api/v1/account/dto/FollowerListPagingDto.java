package com.brandpark.sharemusic.api.v1.account.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerListPagingDto {
    private List<FollowerInfoDto> followers;
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
