package com.brandpark.sharemusic.modules.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    ALBUM_NAME("앨범 이름"),
    USER_NAME("사용자 이름");

    private final String name;
}
