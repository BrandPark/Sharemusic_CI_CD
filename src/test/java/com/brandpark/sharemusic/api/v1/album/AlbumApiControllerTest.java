package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse.TrackInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest.CreateTrackRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest.UpdateTrackRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.account.service.AccountService;
import com.brandpark.sharemusic.modules.album.domain.*;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.CommentFactory;
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

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;
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
    @Autowired TrackRepository trackRepository;
    @Autowired AccountService accountService;
    @Autowired AlbumRepository albumRepository;
    @Autowired EntityManager entityManager;
    @Autowired CommentFactory commentFactory;
    @Autowired CommentRepository commentRepository;
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

                    assertPageResult(pageNum, pageSize, totalAlbumCount, resultPage);

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

                    assertPageResult(pageNum, pageSize, totalAlbumCount, resultPage);

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
    @DisplayName("앨범 저장 - 실패(중복된 트랙이 있는 경우)")
    @Test
    public void CreateAlbum_Fail_When_DuplicatedTrack() throws Exception {

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
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

        assertEntityIsNotEmpty(savedAlbum);
        assertThat(savedAlbum.getTracks().size()).isEqualTo(trackCount);
        assertThat(savedAlbum.getTitle()).isEqualTo(reqDto.getTitle());
        assertThat(savedAlbum.getDescription()).isEqualTo(reqDto.getDescription());
        assertThat(savedAlbum.getAlbumImage()).isEqualTo(reqDto.getAlbumImage());
        assertThat(savedAlbum.getAccountId()).isEqualTo(userAccount.getId());
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 저장 - 실패(권한이 없는 경우 : Guest 계정)")
    @Test
    public void SaveAlbum_Fail_When_GuestAccount() throws Exception {

        // given
        CreateAlbumRequest reqDto = new CreateAlbumRequest();
        reqDto.setTitle("title");
        reqDto.setDescription("description");
        reqDto.setAlbumImage("albumImage");

        // when
        mockMvc.perform(post("/api/v1/albums")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))

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
    @DisplayName("앨범 수정 - 실패(바꿀 제목으로 된 앨범을 이미 사용자가 갖고 있는 경우)")
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

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(트랙이 모두 REMOVE 상태인 경우)")
    @Test
    public void UpdateAlbum_Fail_When_AllTracksRemoveStatus() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        for (UpdateTrackRequest trackDto : reqDto.getTracks()) {
            trackDto.setStatus(TrackStatus.REMOVE);
        }

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(저장할 트랙이 5개 보다 많을 경우)")
    @Test
    public void UpdateAlbum_Fail_When_TrackToSaveGreaterThan5() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        UpdateTrackRequest moreTrackData = new UpdateTrackRequest();
        moreTrackData.setStatus(TrackStatus.INSERT);
        moreTrackData.setName("newTrackName");
        moreTrackData.setArtist("newTrackArtist");

        reqDto.getTracks().add(moreTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(INSERT 상태의 트랙이 ID가 있을 경우)")
    @Test
    public void UpdateAlbum_Fail_When_InsertStatusTrackHasId() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        UpdateTrackRequest notValidTrackData = new UpdateTrackRequest();
        notValidTrackData.setStatus(TrackStatus.INSERT);
        notValidTrackData.setId(20L);
        notValidTrackData.setName("newTrackName");
        notValidTrackData.setArtist("newTrackArtist");

        reqDto.getTracks().add(notValidTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(UPDATE 상태의 트랙이 ID가 없을 경우)")
    @Test
    public void UpdateAlbum_Fail_When_UpdateStatusTrackIdNull() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        UpdateTrackRequest notValidTrackData = new UpdateTrackRequest();
        notValidTrackData.setStatus(TrackStatus.UPDATE);
        notValidTrackData.setId(null);
        notValidTrackData.setName("newTrackName");
        notValidTrackData.setArtist("newTrackArtist");

        reqDto.getTracks().add(notValidTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(UPDATE 상태의 트랙이 기존에 없는 트랙인 경우)")
    @Test
    public void UpdateAlbum_Fail_When_UpdateStatusTrackIsNotExistsInOriginAlbum() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        Long notExistsTrackId = 9999L;

        UpdateTrackRequest notValidTrackData = new UpdateTrackRequest();
        notValidTrackData.setStatus(TrackStatus.UPDATE);
        notValidTrackData.setId(notExistsTrackId);
        notValidTrackData.setName("newTrackName");
        notValidTrackData.setArtist("newTrackArtist");

        boolean notExistsTrack = !trackRepository.existsTrackByIdAndAlbumId(notExistsTrackId, savedAlbum.getId());
        assertThat(notExistsTrack).isTrue();

        reqDto.getTracks().add(notValidTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(REMOVE 상태의 트랙이 ID가 없을 경우)")
    @Test
    public void UpdateAlbum_Fail_When_RemoveStatusTrackIdNull() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        UpdateTrackRequest notValidTrackData = new UpdateTrackRequest();
        notValidTrackData.setStatus(TrackStatus.REMOVE);
        notValidTrackData.setId(null);
        notValidTrackData.setName("newTrackName");
        notValidTrackData.setArtist("newTrackArtist");

        reqDto.getTracks().add(notValidTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(REMOVE 상태의 트랙이 기존에 없는 트랙인 경우)")
    @Test
    public void UpdateAlbum_Fail_When_RemoveStatusTrackIsNotExistsInOriginAlbum() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        Long notExistsTrackId = 9999L;

        UpdateTrackRequest notValidTrackData = new UpdateTrackRequest();
        notValidTrackData.setStatus(TrackStatus.REMOVE);
        notValidTrackData.setId(notExistsTrackId);
        notValidTrackData.setName("newTrackName");
        notValidTrackData.setArtist("newTrackArtist");

        boolean notExistsTrack = !trackRepository.existsTrackByIdAndAlbumId(notExistsTrackId, savedAlbum.getId());
        assertThat(notExistsTrack).isTrue();

        reqDto.getTracks().add(notValidTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(INSERT 상태의 트랙이 저장될 다른 트랙과 중복될 경우)")
    @Test
    public void UpdateAlbum_Fail_When_InsertStatusTrackIsDuplicateWithOtherTrackToSave() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        UpdateTrackRequest savedTrack = reqDto.getTracks().get(0);

        UpdateTrackRequest duplicateTrackData = new UpdateTrackRequest();
        duplicateTrackData.setStatus(TrackStatus.INSERT);
        duplicateTrackData.setName(savedTrack.getName());
        duplicateTrackData.setArtist(savedTrack.getArtist());

        reqDto.getTracks().add(duplicateTrackData);

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(Update 상태의 트랙이 저장될 다른 트랙과 중복될 경우)")
    @Test
    public void UpdateAlbum_Fail_When_UpdateStatusTrackIsDuplicateWithOtherTrackToSave() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        UpdateTrackRequest savedTrack = reqDto.getTracks().get(0);

        UpdateTrackRequest duplicateTrackData = reqDto.getTracks().get(1);
        duplicateTrackData.setStatus(TrackStatus.UPDATE);
        duplicateTrackData.setName(savedTrack.getName());
        duplicateTrackData.setArtist(savedTrack.getArtist());

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(None 상태의 트랙이 저장될 다른 트랙과 중복될 경우)")
    @Test
    public void UpdateAlbum_Fail_When_NoneStatusTrackIsDuplicateWithOtherTrackToSave() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);
        UpdateTrackRequest savedTrack = reqDto.getTracks().get(0);

        UpdateTrackRequest duplicateTrackData = reqDto.getTracks().get(1);
        duplicateTrackData.setStatus(TrackStatus.NONE);
        duplicateTrackData.setName(savedTrack.getName());
        duplicateTrackData.setArtist(savedTrack.getArtist());

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

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(다른 사람의 앨범을 수정하려 할 경우)")
    @Test
    public void UpdateAlbum_Fail_When_OtherAccountAlbum() throws Exception {

        // given
        Account otherAccount = accountFactory.persistAccount("otherAccount", Role.USER);
        Album otherAccountAlbum = albumFactory.persistAlbumWithTracks("otherAccountAlbum", 3, otherAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(otherAccountAlbum);
        UpdateTrackRequest savedTrack = reqDto.getTracks().get(0);

        UpdateTrackRequest duplicateTrackData = reqDto.getTracks().get(1);
        duplicateTrackData.setStatus(TrackStatus.NONE);
        duplicateTrackData.setName(savedTrack.getName());
        duplicateTrackData.setArtist(savedTrack.getArtist());

        String url = "/api/v1/albums/" + otherAccountAlbum.getId();

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(권한이 없을 경우 : GUEST 계정)")
    @Test
    public void UpdateAlbum_Fail_When_NotAuthority() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        // when
        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 실패(로그인을 하지 않은 경우)")
    @Test
    public void UpdateAlbum_Fail_When_NotAuthenticated() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        // when
        mockMvc.perform(put("/api/v1/albums/" + savedAlbum.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 - 성공")
    @Test
    public void UpdateAlbum_Success() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 3, userAccount.getId());

        UpdateAlbumRequest reqDto = transformToUpdateAlbumRequest(savedAlbum);

        UpdateTrackRequest insertTrack = new UpdateTrackRequest();
        insertTrack.setStatus(TrackStatus.INSERT);
        insertTrack.setName("newTrackName");
        insertTrack.setArtist("newTrackArtist");
        reqDto.getTracks().add(insertTrack);

        UpdateTrackRequest updateTrack = reqDto.getTracks().get(0);
        updateTrack.setStatus(TrackStatus.UPDATE);
        updateTrack.setName("updateTrackName");
        updateTrack.setArtist("updateTrackArtist");

        UpdateTrackRequest removeTrack = reqDto.getTracks().get(1);
        removeTrack.setStatus(TrackStatus.REMOVE);

        int expectedTotalTrackCount = 3;
        Long updateTrackId = updateTrack.getId();
        Long removeTrackId = removeTrack.getId();

        String url = "/api/v1/albums/" + savedAlbum.getId();

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk());

        assertThat(trackRepository.count()).isEqualTo(expectedTotalTrackCount);

        Track updateResult = trackRepository.findById(updateTrackId).get();
        assertThat(updateResult.getName()).isEqualTo(updateTrack.getName());
        assertThat(updateResult.getArtist()).isEqualTo(updateTrack.getArtist());
        assertThat(updateResult.getAlbum().getId()).isEqualTo(savedAlbum.getId());

        boolean notExistsRemovedTrack = !trackRepository.existsTrackByIdAndAlbumId(removeTrackId, savedAlbum.getId());
        assertThat(notExistsRemovedTrack).isTrue();
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 삭제 - 실패(해당 앨범이 존재하지 않는 경우)")
    @Test
    public void DeleteAlbum_Fail_When_NotFoundAlbum() throws Exception {

        // given
        Long notExistsAlbumId = 9999L;
        boolean notExistsAlbum = !albumRepository.existsById(notExistsAlbumId);

        assertThat(notExistsAlbum).isTrue();

        String url = "/api/v1/albums/" + notExistsAlbumId;

        // when
        mockMvc.perform(delete(url)
                        .with(csrf())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 삭제 - 실패(내 앨범이 아닌 경우)")
    @Test
    public void DeleteAlbum_Fail_When_NotAuthority() throws Exception {

        // given
        Account otherAccount = accountFactory.persistAccount("otherAccount", Role.USER);

        Album otherAccountAlbum = albumFactory.persistAlbumWithTracks("otherAccountAlbum", 5, otherAccount.getId());

        String url = "/api/v1/albums/" + otherAccountAlbum.getId();

        // when
        mockMvc.perform(delete(url)
                        .with(csrf())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @DisplayName("앨범 삭제 - 실패(로그인하지 않은 경우)")
    @Test
    public void DeleteAlbum_Fail_When_NotAuthenticated() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        String url = "/api/v1/albums/" + savedAlbum.getId();

        // when
        mockMvc.perform(delete(url)
                        .with(csrf())
                        .characterEncoding("UTF-8"))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 삭제 - 성공")
    @Test
    public void DeleteAlbum_Success() throws Exception {

        // given
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, userAccount.getId());

        List<Long> savedTrackIdList = savedAlbum.getTracks().stream()
                .map(Track::getId)
                .collect(Collectors.toList());

        List<Long> savedCommentIdList =
                commentFactory.persistComments("comment", userAccount.getId(), savedAlbum.getId(), 5).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        String url = "/api/v1/albums/" + savedAlbum.getId();

        // when
        mockMvc.perform(delete(url)
                        .with(csrf())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        // then
        boolean notExists = !albumRepository.existsById(savedAlbum.getId());
        assertThat(notExists).isTrue();

        for (Long trackId : savedTrackIdList) {
            boolean notExistsTrack = !trackRepository.existsById(trackId);
            assertThat(notExistsTrack).isTrue();
        }

        for (Long commentId : savedCommentIdList) {
            boolean notExistsComment = !commentRepository.existsById(commentId);
            assertThat(notExistsComment).isTrue();
        }
    }

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
        List<UpdateTrackRequest> tracks = savedAlbum.getTracks().stream()
                .map(t -> {
                    UpdateTrackRequest result = new UpdateTrackRequest();
                    result.setId(t.getId());
                    result.setName(t.getName());
                    result.setArtist(t.getArtist());
                    result.setStatus(TrackStatus.NONE);

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