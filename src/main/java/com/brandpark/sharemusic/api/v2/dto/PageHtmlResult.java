package com.brandpark.sharemusic.api.v2.dto;

import lombok.Data;

@Data
public class PageHtmlResult {
    private final String listHtml;
    private final String paginationHtml;
}
