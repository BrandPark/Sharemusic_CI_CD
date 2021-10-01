package com.brandpark.sharemusic.modules.albums;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import com.brandpark.sharemusic.modules.tracks.Track;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UK_ALBUM", columnNames = {"album_id", "album_title"})
)
@Entity
public class Album extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long id;

    @Column(name = "album_title", nullable = false, length = 50)
    private String title;

    @Lob
    private String albumImage;

    @Column(nullable = false)
    private Long accountId;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();
}
