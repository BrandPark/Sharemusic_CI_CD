package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    Album savedAlbum;

    @BeforeEach
    public void setUp() {
        userAccount = accountFactory.createAccount("userAccount", Role.USER);
        guestAccount = accountFactory.createAccount("guestAccount", Role.GUEST);

        accountRepository.saveAll(List.of(userAccount, guestAccount));

        savedAlbum = albumFactory.createAlbumWithTracks("저장된 앨범 타이틀", 5, userAccount.getId());
        albumRepository.save(savedAlbum);
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 입력 값 오류(누락된 필수 항목)")
    @Test
    public void SaveAlbum_Fail_When_NotInputEssentialValue() throws Exception {

        // given
        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();

        int trackCount = 5;
        List<TrackSaveRequest> tracks = albumFactory.createTrackSaveDtos(trackCount);
        albumDto.setTracks(tracks);

        // when
        albumDto.setTitle(" ");

        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

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

        // given
        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();

        int tooManyTrackCount = 6;
        List<TrackSaveRequest> tooManyTracks = albumFactory.createTrackSaveDtos(tooManyTrackCount);

        // when
        albumDto.setTracks(tooManyTracks);

        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(INVALID_TRACKS_COUNT_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).contains("1개 이상 5개 이하여야 합니다.");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 입력 값 오류(동일한 앨범에 같은 음원 삽입 불가)")
    @Test
    public void SaveAlbum_Fail_When_InputDuplicateTrack() throws Exception {

        // given : 트랙들을 허용 값 이상 넣는다.
        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();

        List<TrackSaveRequest> duplicateTrackList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TrackSaveRequest duplicateTrackDto = albumFactory.createTrackSaveDto("중복음원명", "중복아티스트명");
            duplicateTrackList.add(duplicateTrackDto);
        }

        // when
        albumDto.setTracks(duplicateTrackList);

        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_TRACK_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).contains("중복된 트랙");
                });
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 권한 오류(GUEST 계정 : 이메일 인증을 하지 않은 계정)")
    @Test
    public void SaveAlbum_Fail_When_GuestAccount() throws Exception {

        // given
        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 실패 - 중복된 앨범 제목")
    @Test
    public void SaveAlbum_Fail_When_DuplicateTitle() throws Exception {

        // given
        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();

        int trackCount = 5;
        List<TrackSaveRequest> tracks = albumFactory.createTrackSaveDtos(trackCount);
        albumDto.setTracks(tracks);

        // when
        albumDto.setTitle(savedAlbum.getTitle());

        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_ALBUM_TITLE_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).contains("같은 이름의 앨범");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 성공")
    @Test
    public void SaveAlbum_Success() throws Exception {

        // given
        int trackCount = 5;
        List<TrackSaveRequest> trackDtos = albumFactory.createTrackSaveDtos(trackCount);

        AlbumSaveRequest albumDto = albumFactory.createAlbumSaveDto();
        albumDto.setTracks(trackDtos);

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().isOk())
                .andExpect(result -> {

                    Long albumId = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), Long.class);
                    assertThat(albumId).isNotNull();

                    Album album = albumRepository.findById(albumId).get();
                    assertThat(album).isNotNull();
                    assertThat(album.getTitle()).isEqualTo(albumDto.getTitle());
                    assertThat(album.getDescription()).isEqualTo(albumDto.getDescription());
                    assertThat(album.getAlbumImage()).isEqualTo(albumDto.getAlbumImage());

                    List<Track> tracks = album.getTracks();
                    assertThat(tracks.size()).isEqualTo(trackCount);

                    Track track = tracks.get(0);
                    assertThat(track.getAlbum() == album).isTrue();
                    assertThat(track.getName()).contains("이름");
                    assertThat(track.getArtist()).contains("아티스트");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 실패 - 입력 값 오류(누락된 필수 항목)")
    @Test
    public void UpdateAlbum_Fail_When_NotInputEssentialValue() throws Exception {

        // given : 앨범을 수정할 때 앨범 제목에 아무것도 적지 않는다.

        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when
        albumDto.setTitle("");

        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

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
    @DisplayName("앨범 수정 실패 - 입력 값 오류(너무 많은 Track)")
    @Test
    public void UpdateAlbum_Fail_When_InputTooManyTracks() throws Exception {

        // given
        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when
        albumDto.getTracks().add(albumFactory.createTrackUpdateDto("6번째 트랙의 이름", "6번째 트랙의 아티스트"));
        assertThat(albumDto.getTracks().size()).isGreaterThan(5);

        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

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
    @DisplayName("앨범 수정 실패 - 권한 오류(GUEST 계정 : 이메일 인증을 하지 않은 계정)")
    @Test
    public void UpdateAlbum_Fail_When_GuestAccount() throws Exception {

        // given
        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when
        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 실패 - 중복된 앨범 제목")
    @Test
    public void UpdateAlbum_Fail_When_DuplicateTitle() throws Exception {

        // given
        Album otherSavedAlbum = albumFactory.createAlbumWithTracks("다른 앨범의 제목", 5, userAccount.getId());
        albumRepository.save(otherSavedAlbum);

        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when
        albumDto.setTitle(otherSavedAlbum.getTitle());

        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_ALBUM_TITLE_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).contains("같은 이름의 앨범");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 실패 - 입력 값 오류(동일한 앨범에 같은 음원 삽입 불가)")
    @Test
    public void UpdateAlbum_Fail_When_InputDuplicateTrack() throws Exception {

        // given
        List<TrackUpdateRequest> duplicateTrackList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TrackUpdateRequest duplicateTrackDto = albumFactory.createTrackUpdateDto("중복음원명", "중복아티스트명");
            duplicateTrackList.add(duplicateTrackDto);
        }

        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when
        albumDto.setTracks(duplicateTrackList);
        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_TRACK_EXCEPTION.getCode());
                    assertThat(exceptionResult.getErrorMessage()).contains("중복된 트랙");
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 성공 - (트랙 추가, 트랙 삭제, 트랙 수정)")
    @Test
    public void UpdateAlbum_Success() throws Exception {
        // given
        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);

        // when : 앨범 정보 수정, 트랙 추가, 삭제, 수정
        albumDto.setTitle("수정된 제목");
        albumDto.setAlbumImage("수정된 이미지");
        albumDto.setDescription("수정된 소개");

        List<TrackUpdateRequest> trackDtos = albumDto.getTracks();
        trackDtos.add(0, albumFactory.createTrackUpdateDto("추가된 트랙 이름", "추가된 트랙 아티스트"));
        trackDtos.get(1).setName("수정된 트랙 이름");
        trackDtos.get(1).setArtist("수정된 트랙 아티스트");
        trackDtos.remove(4);
        trackDtos.remove(4);

        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(albumDto)))

                // then
                .andExpect(status().isOk())
                .andExpect(result -> {

                    Long albumId = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), Long.class);
                    assertThat(albumId).isNotNull();

                    Album album = albumRepository.findById(albumId).get();
                    assertThat(album).isNotNull();
                    assertThat(album.getTitle()).isEqualTo("수정된 제목");
                    assertThat(album.getDescription()).isEqualTo("수정된 소개");
                    assertThat(album.getAlbumImage()).isEqualTo("수정된 이미지");

                    // 트랙 이름순 정렬
                    List<Track> tracks = album.getTracks().stream()
                            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());

                    assertThat(tracks.size()).isEqualTo(trackDtos.size());
                    assertThat(tracks.get(0).getName()).isEqualTo("수정된 트랙 이름");
                    assertThat(tracks.get(0).getArtist()).isEqualTo("수정된 트랙 아티스트");
                    assertThat(tracks.get(3).getName()).isEqualTo("추가된 트랙 이름");
                    assertThat(tracks.get(3).getArtist()).isEqualTo("추가된 트랙 아티스트");
                });
    }

    @DisplayName("앨범 모두 페이징으로 조회 - 성공")
    @Test
    public void RetrieveAllAlbumsByPaging_Success() throws Exception {

        // given
        albumRepository.deleteAll();

        Album album1 = albumFactory.createAlbumWithTracks("앨범1", 5, userAccount.getId());
        Album album2 = albumFactory.createAlbumWithTracks("앨범2", 5, userAccount.getId());
        List<Album> savedAlbums = new ArrayList<>(List.of(album1, album2));

        albumRepository.saveAll(savedAlbums);

        // when
        // then
        String url = "/api/v1/albums";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(UTF_8);
                    PagingDto<AlbumShortDto> responseDto = objectMapper.readValue(json, new TypeReference<PagingDto<AlbumShortDto>>() {});

                    List<AlbumShortDto> resultAlbums = responseDto.getContents();
                    assertThat(resultAlbums.size()).isEqualTo(savedAlbums.size());

                    AlbumShortDto firstAlbum = resultAlbums.get(0);
                    Album expectedAlbumInfo = album2;
                    assertThat(firstAlbum.getCreatedDate()).isAfterOrEqualTo(resultAlbums.get(1).getCreatedDate());
                    assertThat(firstAlbum.getTitle()).isEqualTo("앨범2");
                    assertThat(firstAlbum.getAlbumImage()).isEqualTo(expectedAlbumInfo.getAlbumImage());
                    assertThat(firstAlbum.getCreatorNickname()).isEqualTo(userAccount.getNickname());
                    assertThat(firstAlbum.getCreatorProfileImage()).isEqualTo(userAccount.getProfileImage());
                    assertThat(firstAlbum.getDescription()).isEqualTo(expectedAlbumInfo.getDescription());
                    assertThat(firstAlbum.getTrackCount()).isEqualTo(expectedAlbumInfo.getTrackCount());
                });
    }
}