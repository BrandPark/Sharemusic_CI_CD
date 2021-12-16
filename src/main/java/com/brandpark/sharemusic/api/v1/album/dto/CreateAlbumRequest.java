package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.modules.album.dto.CreateAlbumDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

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

        @NotBlank(message = "이름을 입력하지 않은 트랙이 있습니다.")
        @Length(max = 100, message = "음원 명은 100자 이하로 작성해 주세요.")
        private String name;

        @NotBlank(message = "아티스트를 입력하지 않은 트랙이 있습니다.")
        @Length(max = 100, message = "아티스트는 100자 이하로 작성해 주세요.")
        private String artist;
    }
}
