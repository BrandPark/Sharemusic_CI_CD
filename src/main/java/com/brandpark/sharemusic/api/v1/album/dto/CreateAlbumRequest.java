package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.modules.album.dto.CreateAlbumDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
        return new CreateAlbumDto(
                title,
                description,
                albumImage,
                tracks.stream()
                        .map(t -> new CreateAlbumDto.CreateTrackDto(
                                t.getName(),
                                t.getArtist()
                        ))
                        .collect(toList())
        );
    }

    @EqualsAndHashCode(of = {"name", "artist"})
    @Data
    public static class CreateTrackRequest {

        @NotBlank(message = "트랙 이름을 입력해 주세요.")
        private String name;

        @NotBlank(message = "트랙 아티스트를 입력해 주세요.")
        private String artist;
    }
}
