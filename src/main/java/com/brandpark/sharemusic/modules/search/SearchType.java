package com.brandpark.sharemusic.modules.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    ALBUM_NAME("앨범 이름"),
    USER_NAME("사용자 이름"),
    TRACK_NAME("음원 이름"),
    TRACK_ARTIST("음원 아티스트");

    private final String name;
}
