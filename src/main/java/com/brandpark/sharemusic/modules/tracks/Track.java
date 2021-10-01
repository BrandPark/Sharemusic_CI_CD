package com.brandpark.sharemusic.modules.tracks;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import com.brandpark.sharemusic.modules.albums.Album;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UK_TRACK", columnNames = {"track_name", "artist"})
)
@Entity
public class Track extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @Column(name = "track_name", nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String artist;

    @JoinColumn(name = "album_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Album album;
}
