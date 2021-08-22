package com.brandpark.sharemusic.domain.album;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.Theme;
import com.brandpark.sharemusic.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Album extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Track> tracks = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "THEME", joinColumns = @JoinColumn(name = "album_id"))
    private Set<Theme> themes = new HashSet<>();

    private String title;
    private String imgUrl;

    public void addTrack(Track track) {
        tracks.add(track);
        track.updateAlbum(this);
    }

    public static Album createAlbum(String title, String imgUrl, List<Track> tracks, Set<Theme> themes) {
        Album album = new Album();
        album.title = title;
        album.imgUrl = imgUrl;
        for (Track track : tracks) {
            album.addTrack(track);
        }

        album.themes.addAll(themes);

        return album;
    }
}
