package com.brandpark.sharemusic.modules.search.form;

import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.Data;

@Data
public class SearchForm {
    private String q;
    private SearchType type;
}
