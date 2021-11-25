package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.modules.album.dto.CreateAlbumDto;
import com.brandpark.sharemusic.modules.album.dto.CreateTrackDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class CreateAlbumRequest {

    @NotBlank(message = "앨범 제목을 입력해 주세요.")
    private String title;

    private String description;

    private String albumImage;

    @Valid
    @Size(max = 5, message = "트랙의 수는 5개가 최대입니다.")
    @NotEmpty(message = "트랙을 추가해 주세요.")
    private List<CreateTrackRequest> tracks;

    public CreateAlbumDto toModuleDto() {
        List<CreateTrackDto> tracks = this.tracks.stream()
                .map(track -> new CreateTrackDto(
                        track.getName(),
                        track.getArtist()
                ))
                .collect(toList());

        return new CreateAlbumDto(
                title,
                description,
                albumImage,
                tracks
        );
    }
}
