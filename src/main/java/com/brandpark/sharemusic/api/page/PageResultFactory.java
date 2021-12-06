package com.brandpark.sharemusic.api.page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class PageResultFactory {
    public static <T> PageResult<T> createPageResult(List<T> content, Pageable pageable, long total) {
        PageImpl<T> page = new PageImpl<>(content, pageable, total);
        return new PageResult<T>(page);
    }
}
