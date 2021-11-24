package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.page.PageResult;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AssertUtil {

    public static <T> void assertPage(int pageNum, int pageSize, int totalElementCount, PageResult<T> page) {
        assertThat(page.getPageNumber()).isEqualTo(pageNum);
        assertThat(page.getPageSize()).isEqualTo(pageSize);
        assertThat(page.getOffset()).isEqualTo(pageSize * pageNum);
        assertThat(page.getTotalPages()).isGreaterThan(0);
        assertThat(page.getNumberOfElements()).isGreaterThan(0);
        assertThat(page.getTotalElements()).isEqualTo(totalElementCount);
    }
}
