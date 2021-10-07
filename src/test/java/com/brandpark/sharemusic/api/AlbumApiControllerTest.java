package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.exception.ApiException;
import com.brandpark.sharemusic.api.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.TrackSaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.brandpark.sharemusic.api.exception.Error.BLANK_FIELD_EXCEPTION;
import static com.brandpark.sharemusic.api.exception.Error.INVALID_TRACKS_COUNT_EXCEPTION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class AlbumApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired AlbumRepository albumRepository;
    Account guestAccount;
    Account userAccount;

    @BeforeEach
    public void setUp() {
        userAccount = accountFactory.createAccount("userAccount", Role.USER);

        guestAccount = accountFactory.createAccount("guestAccount", Role.GUEST);

        accountRepository.saveAll(List.of(userAccount, guestAccount));
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 입력 값 오류(누락된 필수 항목)")
    @Test
    public void SaveAlbum_Fail_When_NotInputEssentialValue() throws Exception {

        // given : 앨범 타이틀에 아무것도 적지 않는다.
        TrackSaveRequest trackDto = albumFactory.createTrackSaveDto("음원명1", "아티스트1");
        AlbumSaveRequest albumDto = new AlbumSaveRequest();
        albumDto.setTitle(" ");
        albumDto.setTracks(List.of(trackDto));

        String requestJson = objectMapper.writeValueAsString(albumDto);

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestJson))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(BLANK_FIELD_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).isEqualTo("'title' 이 비어있습니다.");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 입력 값 오류(너무 많은 Track)")
    @Test
    public void SaveAlbum_Fail_When_InputTooManyTracks() throws Exception {

        // given : 트랙들을 허용 값 이상 넣는다.
        int manyCount = 6;
        List<TrackSaveRequest> trackDtos = albumFactory.createTrackSaveDtoList("음원명", "아티스트", manyCount);
        assertThat(trackDtos.size()).isEqualTo(manyCount);

        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto("앨범 제목", trackDtos);
        String requestJson = objectMapper.writeValueAsString(albumDto);

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestJson))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(INVALID_TRACKS_COUNT_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).isEqualTo("tracks 의 요소는 1개 이상 5개 이하여야 합니다.");
                });
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 권한 오류(GUEST 계정 : 이메일 인증을 하지 않은 계정)")
    @Test
    public void SaveAlbum_Fail_When_GuestAccount() throws Exception {

        // given : 이메일 인증을 하지 않은 GUEST 계정으로 로그인한다.
        List<TrackSaveRequest> trackDtos = albumFactory.createTrackSaveDtoList("음원명", "아티스트", 5);

        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto("앨범 제목", trackDtos);
        String requestJson = objectMapper.writeValueAsString(albumDto);

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestJson))

                // then
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 성공")
    @Test
    public void SaveAlbum_Success() throws Exception {

        // given : 이메일 인증을 하지 않은 GUEST 계정으로 로그인한다.
        int trackCount = 5;
        List<TrackSaveRequest> trackDtos = albumFactory.createTrackSaveDtoList("음원명", "아티스트", trackCount);

        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto("앨범 제목", trackDtos);
        String requestJson = objectMapper.writeValueAsString(albumDto);

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestJson))

                // then
                .andExpect(status().isOk())
                .andExpect(result -> {

                    Long albumId = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), Long.class);
                    assertThat(albumId).isNotNull();

                    Album album = albumRepository.findById(albumId).get();
                    assertThat(album).isNotNull();
                    assertThat(album.getTitle()).isEqualTo(albumDto.getTitle());
                    assertThat(album.getBio()).isEqualTo(albumDto.getBio());
                    assertThat(album.getAlbumImage()).isEqualTo(albumDto.getAlbumImage());

                    List<Track> tracks = album.getTracks();
                    assertThat(tracks.size()).isEqualTo(trackCount);

                    Track track = tracks.get(0);
                    assertThat(track.getAlbum() == album).isTrue();
                    assertThat(track.getName()).contains("음원명");
                    assertThat(track.getArtist()).contains("아티스트");
                });
    }
}