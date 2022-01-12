package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.modules.album.domain.TrackStatus;
import com.brandpark.sharemusic.modules.album.dto.UpdateAlbumDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UpdateAlbumRequest {

    @NotBlank(message = "앨범 제목을 입력해 주세요.")
    private String title;

    private String description;

    private String albumImage;

    @Valid
    @NotEmpty(message = "트랙을 추가해 주세요.")
    private List<UpdateTrackRequest> tracks;

    public UpdateAlbumDto toModuleDto() {
        return new UpdateAlbumDto(
                title,
                description,
                albumImage,
                tracks.stream()
                        .map(t -> new UpdateAlbumDto.UpdateTrackDto(
                                        t.getId(),
                                        t.getName(),
                                        t.getArtist(),
                                        t.getStatus())
                        ).collect(Collectors.toList())
        );
    }

    @EqualsAndHashCode(of = {"name", "artist"})
    @Data
    public static class UpdateTrackRequest {
        private Long id;

        @NotBlank(message = "트랙의 이름을 입력해 주세요.")
        @Length(max = 100, message = "음원 명은 100자 이하로 작성해 주세요.")
        private String name;

        @NotBlank(message = "트랙의 아티스트를 입력해 주세요.")
        @Length(max = 100, message = "아티스트는 100자 이하로 작성해 주세요.")
        private String artist;

        @NotNull(message = "트랙의 상태를 입력해 주세요.")
        private TrackStatus status;
    }
}
