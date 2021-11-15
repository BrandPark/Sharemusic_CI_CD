package com.brandpark.sharemusic.modules.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    USER_NAME("사용자 이름"),
    TRACK_NAME("음원 이름"),
    ARTIST_NAME("아티스트 이름"),
    ALBUM_NAME("앨범 이름");

    private final String name;
}
