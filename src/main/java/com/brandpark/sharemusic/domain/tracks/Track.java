package com.brandpark.sharemusic.domain.tracks;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.albums.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Track extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String artist;

    @JoinColumn(name = "album_id", foreignKey = @ForeignKey(name = "FK_TRACK_ALBUM"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Album album;

    @Builder
    public Track(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public void updateAlbum(Album album) {
        this.album = album;
    }
}
