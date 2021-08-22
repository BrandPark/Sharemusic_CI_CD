package com.brandpark.sharemusic.domain.album;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "track_name")
    private String name;

    private String artist;
    private String imgUrl;
    private String videoUrl;

    public void updateAlbum(Album album) {
        this.album = album;
    }

    @Builder
    private Track(Album album, String name, String artist, String imgUrl, String videoUrl) {
        this.album = album;
        this.name = name;
        this.artist = artist;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }
}
