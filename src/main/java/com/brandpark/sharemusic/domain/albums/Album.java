package com.brandpark.sharemusic.domain.albums;

import com.brandpark.sharemusic.domain.BaseTimeEntity;
import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.web.dto.tracks.TrackUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
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

    public void update(String name , List<TrackUpdateRequestDto> trackUpdateRequestDtoList) {
        this.name = name;

        Map<Long,Track> trackMap = tracks.stream().collect(Collectors.toMap(Track::getId, Function.identity()));

        for (TrackUpdateRequestDto dto : trackUpdateRequestDtoList) {
            Long trackId = dto.getId();
            String state = dto.getState();

            if (state == null) continue;

            switch (state) {
                case "I" : addTrack(dto.toEntity());break;  //트랙을 추가
                case "D" : removeTrack(dto.toEntity());break;   //트랙을 삭제
                case "U" : trackMap.get(trackId).update(dto.getName(), dto.getArtist());break;  //트랙을 변경
            }
        }
    }
}
