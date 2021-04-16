package com.brandpark.sharemusic.domain.tracks;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.albums.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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
    public Track(String name, String artist, Album album) {
        this.name = name;
        this.artist = artist;
        this.album = album;
    }

    public void updateAlbum(Album album) {
        this.album = album;
    }

    public void update(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return id.equals(track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
