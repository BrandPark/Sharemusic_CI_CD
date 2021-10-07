package com.brandpark.sharemusic.modules.album.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UK_TRACK", columnNames = {"album_id", "track_name", "artist"})
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

    public void initAlbum(Album album) {
        if (album == null) {
            this.album = album;
        }
    }
}
