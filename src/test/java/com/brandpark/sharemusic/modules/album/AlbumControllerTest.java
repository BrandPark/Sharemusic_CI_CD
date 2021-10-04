package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
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
    Account userAccount;
    Account guestAccount;

    @BeforeEach
    public void setUp() {
        userAccount = accountFactory.createAccount("userAccount", Role.USER);
        guestAccount = accountFactory.createAccount("guestAccount", Role.GUEST);
        accountRepository.saveAll(List.of(userAccount, guestAccount));
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

}
