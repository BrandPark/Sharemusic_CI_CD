package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.*;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.TestUtil;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.DUPLICATE_FIELD_EXCEPTION;
import static com.brandpark.sharemusic.api.v1.exception.Error.ILLEGAL_ARGUMENT_EXCEPTION;
import static com.brandpark.sharemusic.testUtils.AssertUtil.*;
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

    @BeforeEach
    public void setUp() {
        userAccount = accountFactory.persistAccount("userAccount", Role.USER);
        guestAccount = accountFactory.persistAccount("guestAccount", Role.GUEST);
    }

    @DisplayName("모든 앨범 정보 페이징 조회 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void FindAllAlbumsByPaging_Success_When_FullFirstPage() throws Exception {

        // given
        int totalAlbumCount = 12;
        int trackCountPerAlbum = 10;

        int pageNum = 0;
        int pageSize = 5;

        Account albumCreator = userAccount;
        albumFactory.persistAlbumsWithTracks("앨범", totalAlbumCount, trackCountPerAlbum, albumCreator.getId());

        int expectedResultPageContentSize = pageSize;

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);
                    PageResult<AlbumInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    assertPage(pageNum, pageSize, totalAlbumCount, resultPage);

                    List<AlbumInfoResponse> resultAlbums = resultPage.getContent();
                    AlbumInfoResponse albumOne = resultAlbums.get(0);
                    List<TrackInfoResponse> resultTracks = albumOne.getTracks();
                    TrackInfoResponse trackOne = resultTracks.get(0);

                    assertThat(resultAlbums.size()).isEqualTo(expectedResultPageContentSize);

                    assertDtoIsNotEmpty(albumOne);

                    assertThat(resultTracks.size()).isEqualTo(trackCountPerAlbum);

                    assertDtoIsNotEmpty(trackOne);
                });
    }

    @DisplayName("모든 앨범 정보 페이징 조회 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void FindAllAlbumsByPaging_Success_When_LessLastPage() throws Exception {

        // given
        int totalAlbumCount = 12;
        int trackCountPerAlbum = 10;

        int pageNum = 2;
        int pageSize = 5;

        Account albumCreator = userAccount;
        albumFactory.persistAlbumsWithTracks("앨범", totalAlbumCount, trackCountPerAlbum, albumCreator.getId());

        int expectedResultPageContentSize = 2;

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);
                    PageResult<AlbumInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    assertPage(pageNum, pageSize, totalAlbumCount, resultPage);

                    List<AlbumInfoResponse> resultAlbums = resultPage.getContent();
                    AlbumInfoResponse albumOne = resultAlbums.get(0);
                    List<TrackInfoResponse> resultTracks = albumOne.getTracks();
                    TrackInfoResponse trackOne = resultTracks.get(0);

                    assertThat(resultAlbums.size()).isEqualTo(expectedResultPageContentSize);

                    assertDtoIsNotEmpty(albumOne);

                    assertThat(resultTracks.size()).isEqualTo(trackCountPerAlbum);

                    assertDtoIsNotEmpty(trackOne);
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(앨범 제목이 비어있을 때)")
    @Test
    public void CreateAlbum_Fail_When_EmptyAlbumTitle() throws Exception {

        // given
        int trackCount = 10;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";
        
        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(같은 제목의 앨범을 이미 사용자가 가지고 있을 경우)")
    @Test
    public void CreateAlbum_Fail_When_DuplicatedAlbumTitleInMyAccount() throws Exception {

        // given
        String duplicatedTitle = "title";
        albumFactory.persistAlbumWithTracks(duplicatedTitle, 5, userAccount.getId());

        int trackCount = 5;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle(duplicatedTitle);
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_FIELD_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(트랙이 비어있을 경우)")
    @Test
    public void CreateAlbum_Fail_When_EmptyTrack() throws Exception {

        // given
        int trackCount = 0;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(트랙이 5개보다 많을 경우)")
    @Test
    public void CreateAlbum_Fail_When_TrackCountGreaterThan5() throws Exception {

        // given
        int trackCount = 6;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(이름이 비어있는 트랙이 있는 경우)")
    @Test
    public void CreateAlbum_Fail_When_ExistsTrackThatEmptyName() throws Exception {

        // given
        int trackCount = 5;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDtoTracks.get(0).setName("");
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(아티스트가 비어있는 트랙이 있는 경우)")
    @Test
    public void CreateAlbum_Fail_When_ExistsTrackThatEmptyArtist() throws Exception {

        // given
        int trackCount = 5;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDtoTracks.get(0).setArtist("");
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 무시(중복된 트랙이 있는 경우 하나만 저장된다.)")
    @Test
    public void CreateAlbum_Ignore_When_DuplicatedTrack() throws Exception {

        // given
        int trackCount = 5;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDtoTracks.get(0).setName(reqDtoTracks.get(1).getName());
        reqDtoTracks.get(0).setArtist(reqDtoTracks.get(1).getArtist());
        reqDto.setTracks(reqDtoTracks);

        int expectedSavedTrackCount = 4;

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(UTF_8);

                    Long albumId = objectMapper.readValue(json, Long.class);

                    assertThat(albumId).isNotNull();
                });

        // then
        Album savedAlbum = albumRepository.findAll().get(0);

        assertEntityIsNotEmpty(savedAlbum, reqDto);
        assertThat(savedAlbum.getTrackCount()).isEqualTo(expectedSavedTrackCount);
        assertThat(savedAlbum.getAccountId()).isEqualTo(userAccount.getId());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 성공")
    @Test
    public void CreateAlbum_Success() throws Exception {

        // given
        int trackCount = 5;

        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        List<CreateTrackRequest> reqDtoTracks = getCreateTrackRequestList(reqDto, trackCount);
        reqDto.setTracks(reqDtoTracks);

        String url = "/api/v1/albums";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8.name())
                        .content(objectMapper.writeValueAsString(reqDto)))

                // then
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(UTF_8);

                    Long albumId = objectMapper.readValue(json, Long.class);

                    assertThat(albumId).isNotNull();
                });

        // then
        Album savedAlbum = albumRepository.findAll().get(0);

        assertEntityIsNotEmpty(savedAlbum, reqDto);
        assertThat(savedAlbum.getTrackCount()).isEqualTo(trackCount);
        assertThat(savedAlbum.getAccountId()).isEqualTo(userAccount.getId());
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(권한이 없는 경우 : Guest 계정)")
    @Test
    public void SaveAlbum_Fail_When_GuestAccount() throws Exception {

        // given
        CreateAlbumRequest albumDto = albumFactory.createAlbumSaveDto();

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
    @DisplayName("앨범 수정 - 실패(대상 앨범이 존재하지 않는 경우)")
    @Test
    public void UpdateAlbum_Fail_When_NotFoundAlbum() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        Long wrongAlbumId = 99999L;

        boolean isNotExistAlbum = !albumRepository.existsById(wrongAlbumId);
        assertThat(isNotExistAlbum).isTrue();

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        String url = "/api/v1/albums/" + wrongAlbumId;

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(앨범 제목이 비어있는 경우)")
    @Test
    public void UpdateAlbum_Fail_When_EmptyAlbumTitle() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        reqDto.setTitle("");

        String url = "/api/v1/albums/" + savedAlbum.getId();

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(바꿀 제목으로된 앨범을 이미 사용자가 갖고 있는 경우)")
    @Test
    public void UpdateAlbum_Fail_When_DuplicateModifiedAlbumTitleInMyAccount() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());
        Album savedOtherAlbum = albumFactory.persistAlbumWithTracks("savedOtherAlbum", 5, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        reqDto.setTitle(savedOtherAlbum.getTitle());

        String url = "/api/v1/albums/" + savedAlbum.getId();

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_FIELD_EXCEPTION.getCode());
                });
    }

    @DisplayName("앨범 수정 - 실패(트랙이 비어있을 경우)")
    @Test
    public void UpdateAlbum_Fail_When_EmptyTrack() throws Exception {
        // given
        
        // when
        
        // then
    }
//
//    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("앨범 수정 실패 - 권한 오류(GUEST 계정 : 이메일 인증을 하지 않은 계정)")
//    @Test
//    public void UpdateAlbum_Fail_When_GuestAccount() throws Exception {
//
//        // given
//        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);
//
//        // when
//        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8")
//                        .content(objectMapper.writeValueAsString(albumDto)))
//
//                // then
//                .andExpect(status().isForbidden());
//    }
//
//    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("앨범 수정 실패 - 중복된 앨범 제목")
//    @Test
//    public void UpdateAlbum_Fail_When_DuplicateTitle() throws Exception {
//
//        // given
//        Album otherSavedAlbum = albumFactory.createAlbumWithTracks("다른 앨범의 제목", 5, userAccount.getId());
////        albumRepository.save(otherSavedAlbum);
//
//        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);
//
//        // when
//        albumDto.setTitle(otherSavedAlbum.getTitle());
//
//        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8")
//                        .content(objectMapper.writeValueAsString(albumDto)))
//
//                // then
//                .andExpect(status().is4xxClientError())
//                .andExpect(result -> {
//                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();
//
//                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);
//
//                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_ALBUM_TITLE_EXCEPTION.getCode());
//                    assertThat(exceptionResult.getErrorMessage()).contains("같은 이름의 앨범");
//                });
//    }
//
//    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("앨범 수정 실패 - 입력 값 오류(동일한 앨범에 같은 음원 삽입 불가)")
//    @Test
//    public void UpdateAlbum_Fail_When_InputDuplicateTrack() throws Exception {
//
//        // given
//        List<TrackUpdateRequest> duplicateTrackList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            TrackUpdateRequest duplicateTrackDto = albumFactory.createTrackUpdateDto("중복음원명", "중복아티스트명");
//            duplicateTrackList.add(duplicateTrackDto);
//        }
//
//        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);
//
//        // when
//        albumDto.setTracks(duplicateTrackList);
//        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8")
//                        .content(objectMapper.writeValueAsString(albumDto)))
//
//                // then
//                .andExpect(status().is4xxClientError())
//                .andExpect(result -> {
//                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();
//
//                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);
//
//                    assertThat(exceptionResult.getErrorCode()).isEqualTo(DUPLICATE_TRACK_EXCEPTION.getCode());
//                    assertThat(exceptionResult.getErrorMessage()).contains("중복된 트랙");
//                });
//    }
//
//    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @DisplayName("앨범 수정 성공 - (트랙 추가, 트랙 삭제, 트랙 수정)")
//    @Test
//    public void UpdateAlbum_Success() throws Exception {
//        // given
//        AlbumUpdateRequest albumDto = albumFactory.createAlbumUpdateDtoByEntity(savedAlbum);
//
//        // when : 앨범 정보 수정, 트랙 추가, 삭제, 수정
//        albumDto.setTitle("수정된 제목");
//        albumDto.setAlbumImage("수정된 이미지");
//        albumDto.setDescription("수정된 소개");
//
//        List<TrackUpdateRequest> trackDtos = albumDto.getTracks();
//        trackDtos.add(0, albumFactory.createTrackUpdateDto("추가된 트랙 이름", "추가된 트랙 아티스트"));
//        trackDtos.get(1).setName("수정된 트랙 이름");
//        trackDtos.get(1).setArtist("수정된 트랙 아티스트");
//        trackDtos.remove(4);
//        trackDtos.remove(4);
//
//        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8")
//                        .content(objectMapper.writeValueAsString(albumDto)))
//
//                // then
//                .andExpect(status().isOk())
//                .andExpect(result -> {
//
//                    Long albumId = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), Long.class);
//                    assertThat(albumId).isNotNull();
//
//                    Album album = null;
//                    assertThat(album).isNotNull();
//                    assertThat(album.getTitle()).isEqualTo("수정된 제목");
//                    assertThat(album.getDescription()).isEqualTo("수정된 소개");
//                    assertThat(album.getAlbumImage()).isEqualTo("수정된 이미지");
//
//                    // 트랙 이름순 정렬
//                    List<Track> tracks = album.getTracks().stream()
//                            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
//
//                    assertThat(tracks.size()).isEqualTo(trackDtos.size());
//                    assertThat(tracks.get(0).getName()).isEqualTo("수정된 트랙 이름");
//                    assertThat(tracks.get(0).getArtist()).isEqualTo("수정된 트랙 아티스트");
//                    assertThat(tracks.get(3).getName()).isEqualTo("추가된 트랙 이름");
//                    assertThat(tracks.get(3).getArtist()).isEqualTo("추가된 트랙 아티스트");
//                });
//    }

    private List<CreateTrackRequest> getCreateTrackRequestList(CreateAlbumRequest reqDto, int trackCount) {
        List<CreateTrackRequest> tracks = new ArrayList<>();
        for (int i = 0; i < trackCount; i++) {
            CreateTrackRequest track = new CreateTrackRequest();
            track.setName(reqDto.getTitle() + ".track.name" + i);
            track.setArtist(reqDto.getTitle() + ".track.artist" + i);

            tracks.add(track);
        }
        return tracks;
    }

    private UpdateAlbumRequest transformToUpdateAlbumRequest(Album savedAlbum) {
        List<UpdateAlbumRequest.UpdateTrackRequest> tracks = savedAlbum.getTracks().stream()
                .map(t -> {
                    UpdateAlbumRequest.UpdateTrackRequest result = new UpdateAlbumRequest.UpdateTrackRequest();
                    result.setId(t.getId());
                    result.setName(t.getName());
                    result.setArtist(t.getArtist());

                    return result;
                }).collect(Collectors.toList());

        UpdateAlbumRequest album = new UpdateAlbumRequest();
        album.setTitle(savedAlbum.getTitle());
        album.setDescription(savedAlbum.getDescription());
        album.setAlbumImage(savedAlbum.getAlbumImage());
        album.setTracks(tracks);

        return album;
    }
}