package com.brandpark.sharemusic.api.v1.album.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateAlbumRequest {

    @NotBlank(message = "앨범 제목을 입력해 주세요.")
    private String title;

    private String description;

    private String albumImage;

    @Valid
    @Size(max = 5, message = "트랙의 수는 최대 5개 입니다.")
    @NotEmpty(message = "트랙을 추가해 주세요.")
    private List<UpdateTrackRequest> tracks;

    @EqualsAndHashCode(of = {"name", "artist"})
    @Data
    public static class UpdateTrackRequest {
        private Long id;

        @NotBlank(message = "트랙의 이름을 입력해 주세요.")
        private String name;

        @NotBlank(message = "트랙의 아티스트를 입력해 주세요.")
        private String artist;
    }
}
