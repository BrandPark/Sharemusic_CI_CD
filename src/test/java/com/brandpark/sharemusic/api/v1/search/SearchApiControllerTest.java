package com.brandpark.sharemusic.api.v1.search;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.search.dto.AccountSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.modules.search.SearchType;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class SearchApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumFactory albumFactory;
    @Autowired ObjectMapper objectMapper;
    List<Account> accounts;
    List<Album> albums;
    List<Track> tracks;

    @BeforeEach
    public void setUp() {
        accounts = accountFactory.persistAccountList("savedAccount", 12);
        albums = albumFactory.persistAlbumsWithTracks("savedAlbum", 12, 5, accounts.get(0).getId());
        tracks = new ArrayList<>();
        albums.stream()
                .forEach(a -> {
                    tracks.addAll(a.getTracks());
                });
    }

    @DisplayName("사용자 이름으로 검색 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void AccountNameSearch_Success_FullFirstPage() throws Exception {

        // given
        SearchType searchType = SearchType.USER_NAME;
        String searchQuery = " savedAc ";

        int pageNum = 0;
        int pageSize = 10;

        int expectedTotalAccountCount = accounts.size();
        int expectedElementsCountInPage = pageSize;

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AccountSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);

                    AccountSearchResult resultOne = resultPage.getContent().get(0);
                    AssertUtil.assertDtoIsNotEmpty(resultOne);
                });
    }

    @DisplayName("사용자 이름으로 검색 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void AccountNameSearch_Success_LessLastPage() throws Exception {

        // given
        SearchType searchType = SearchType.USER_NAME;
        String searchQuery = " savedAc ";

        int pageNum = 1;
        int pageSize = 10;

        int expectedTotalAccountCount = accounts.size();
        int expectedElementsCountInPage = accounts.size() - (pageSize * pageNum);

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AccountSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);

                    AccountSearchResult resultOne = resultPage.getContent().get(0);
                    AssertUtil.assertDtoIsNotEmpty(resultOne);
                });
    }

    @DisplayName("사용자 이름으로 검색 - 성공(검색 결과 0)")
    @Test
    public void AccountNameSearch_Success_Result0() throws Exception {

        // given
        SearchType searchType = SearchType.USER_NAME;
        String searchQuery = "notFoundKeyword";

        int pageNum = 0;
        int pageSize = 10;

        int expectedTotalAccountCount = 0;
        int expectedElementsCountInPage = 0;

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AccountSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);
                });
    }

    @DisplayName("앨범 이름으로 검색 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void AlbumNameSearch_Success_FullFirstPage() throws Exception {

        // given
        SearchType searchType = SearchType.ALBUM_NAME;
        String searchQuery = " savedAl ";

        int pageNum = 0;
        int pageSize = 10;

        int expectedTotalAccountCount = albums.size();
        int expectedElementsCountInPage = pageSize;

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AlbumSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);

                    AlbumSearchResult resultOne = resultPage.getContent().get(0);
                    AssertUtil.assertDtoIsNotEmpty(resultOne);
                });
    }

    @DisplayName("앨범 이름으로 검색 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void AlbumNameSearch_Success_LessLastPage() throws Exception {

        // given
        SearchType searchType = SearchType.ALBUM_NAME;
        String searchQuery = " savedAl ";

        int pageNum = 1;
        int pageSize = 10;

        int expectedTotalAccountCount = accounts.size();
        int expectedElementsCountInPage = accounts.size() - (pageSize * pageNum);

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AlbumSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);

                    AlbumSearchResult resultOne = resultPage.getContent().get(0);
                    AssertUtil.assertDtoIsNotEmpty(resultOne);
                });
    }

    @DisplayName("앨범 이름으로 검색 - 성공(검색 결과 0)")
    @Test
    public void AlbumNameSearch_Success_Result0() throws Exception {

        // given
        SearchType searchType = SearchType.ALBUM_NAME;
        String searchQuery = "notFoundKeyword";

        int pageNum = 0;
        int pageSize = 10;

        int expectedTotalAccountCount = 0;
        int expectedElementsCountInPage = 0;

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", searchType.name())
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AlbumSearchResult> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, expectedTotalAccountCount, resultPage);

                    assertThat(resultPage.getContent().size()).isEqualTo(expectedElementsCountInPage);
                });
    }
}