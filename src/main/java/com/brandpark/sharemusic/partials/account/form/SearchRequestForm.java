package com.brandpark.sharemusic.partials.account.form;

import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.Data;

@Data
public class SearchRequestForm {
    private String q;
    private SearchType type;
}
