package com.brandpark.sharemusic.api.page;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@Data
public class PageResult<T> {
    private List<T> content;
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

    public PageResult(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getPageable().getPageNumber();
        this.pageSize = page.getPageable().getPageSize();
        this.numberOfElements = page.getNumberOfElements();
        this.offset = page.getPageable().getOffset();
    }
}
