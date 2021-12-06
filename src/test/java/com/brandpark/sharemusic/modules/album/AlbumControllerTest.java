package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class AlbumControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired AlbumFactory albumFactory;
    @Autowired AlbumRepository albumRepository;
    Account userAccount;
    Account otherAccount;
    Account guestAccount;

    @BeforeEach
    public void setUp() {
        userAccount = accountFactory.createAccount("userAccount", Role.USER);
        guestAccount = accountFactory.createAccount("guestAccount", Role.GUEST);
        otherAccount = accountFactory.createAccount("otherAccount", Role.USER);

        accountRepository.saveAll(List.of(userAccount, guestAccount, otherAccount));
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 생성 화면 출력 실패 - 권한 오류(GUEST : 이메일 인증을 하지 않은 계정)")
    @Test
    public void CreateAlbumView_Fail_When_Unauthorized() throws Exception {

        // given, when, then
        mockMvc.perform(get("/albums"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 생성 화면 출력 성공")
    @Test
    public void CreateAlbumView_Success() throws Exception {

        // given, when, then
        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("albums/create"));
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 화면 출력 실패 - 권한 오류(본인의 앨범이 아닌 경우)")
    @Test
    public void UpdateAlbumView_Fail_When_NotAlbumHost() throws Exception {

        // given
        Album savedAlbum = albumFactory.createAlbumWithTracks("다른 사람의 앨범", 5, otherAccount.getId());
        albumRepository.save(savedAlbum);
        
        // given, when, then
         mockMvc.perform(get("/albums/" + savedAlbum.getId() + "/update"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/error"));
    }

    @WithUserDetails(value = "userAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("앨범 수정 화면 출력 성공")
    @Test
    public void UpdateAlbumView_Success() throws Exception {

        // given
        Album savedAlbum = albumFactory.createAlbumWithTracks("자신의 앨범", 5, userAccount.getId());
        albumRepository.save(savedAlbum);

        // given, when, then
        mockMvc.perform(get("/albums/" + savedAlbum.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "album", "tracks"))
                .andExpect(view().name("albums/update"));
    }
}
