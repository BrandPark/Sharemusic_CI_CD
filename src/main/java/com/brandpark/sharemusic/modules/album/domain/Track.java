package com.brandpark.sharemusic.modules.album.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Track extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @Column(name = "track_name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String artist;

    @JoinColumn(name = "album_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Album album;

    public void initAlbum(Album album) {
        if (album != null && this.album == null) {
            this.album = album;
        }
    }

    public static Track createTrack(String name, String artist) {
        Track track = new Track();

        track.name = name;
        track.artist = artist;

        return track;
    }

    public void updateTrack(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }
}
