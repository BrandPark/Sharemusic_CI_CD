package com.brandpark.sharemusic.modules.album.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(length = 100)
    private String bio;

    @Lob
    private String albumImage;

    @Column(nullable = false)
    private Long accountId;

    @Builder.Default
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();
}
