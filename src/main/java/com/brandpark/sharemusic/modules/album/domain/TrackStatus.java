package com.brandpark.sharemusic.modules.album.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrackStatus {
    NONE(""),
    INSERT("삽입"),
    UPDATE("수정"),
    REMOVE("삭제");

    private String name;
}
