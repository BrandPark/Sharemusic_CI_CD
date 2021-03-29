package com.brandpark.sharemusic.domain.albums;

import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Entity
public class Album {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Track> tracks = new ArrayList<>();

    @Builder
    public Album(String name, List<Track> tracks) {
        this.name = name;
        if(tracks != null)
            this.tracks = tracks;
    }
    public void addTrack(Track track) {
        this.tracks.add(track);
        track.updateAlbum(this);
    }

    public void removeTrack(Track track) {
        this.tracks.remove(track);
    }

    public void update(String name) {
        this.name = name;
    }
}
