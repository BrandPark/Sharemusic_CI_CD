package com.brandpark.sharemusic.domain.albums;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
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
public class Album extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    private int trackCount;

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
        trackCount++;
    }

    public void removeTrack(Track track) {
        this.tracks.remove(track);
        trackCount--;
    }

    public void update(String name, List<Track> tracks) {
        this.name = name;
        if(tracks != null)
            this.tracks = tracks;
    }
}
