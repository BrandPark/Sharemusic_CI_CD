package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


abstract public class PagingDtoFactory {
    public static final <T> PagingDto createPagingDto(List<T> contents, Pageable pageable, long total, int paginationUnit) {
        PageImpl<T> page = new PageImpl<T>(contents, pageable, total);

        PagingDto result = new PagingDto<T>();
        result.setContents(contents);
        result.setTotalPages(page.getTotalPages());
        result.setTotalElements(page.getTotalElements());
        result.setPageNumber(page.getNumber());
        result.setNumberOfElements(page.getNumberOfElements());
        result.setOffset(pageable.getOffset());
        result.setPageSize(page.getSize());
        result.setPaginationUnit(paginationUnit);

        return result;
    }
}
