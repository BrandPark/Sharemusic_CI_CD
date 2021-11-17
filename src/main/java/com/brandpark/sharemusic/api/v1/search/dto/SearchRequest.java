package com.brandpark.sharemusic.api.v1.search.dto;

import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.Data;

@Data
public class SearchRequest {
    private String q;
    private SearchType type;
}
