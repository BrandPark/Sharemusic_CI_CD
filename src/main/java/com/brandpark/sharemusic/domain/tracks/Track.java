package com.brandpark.sharemusic.domain.tracks;

import com.brandpark.sharemusic.domain.albums.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String artist;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "album_id", foreignKey = @ForeignKey(name = "FK_TRACK_ALBUM"))
    private Album album;

    @Builder
    public Track(String name, String artist, Album album) {
        this.name = name;
        this.artist = artist;
        this.album = album;
    }

    public void updateAlbum(Album album) {
        this.album = album;
    }
}
