package com.brandpark.sharemusic.api.v1.search.dto;

import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.Data;

@Data
public class SearchRequest {

    private String query;
    private SearchType type;
}
