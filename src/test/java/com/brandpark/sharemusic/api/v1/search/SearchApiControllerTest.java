package com.brandpark.sharemusic.api.v1.search;

import com.brandpark.sharemusic.api.v1.search.dto.UserNameSearchResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.search.SearchType;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class SearchApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired ObjectMapper objectMapper;
    List<Account> otherAccounts;
    Account myAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("내 계정");
        accountRepository.save(myAccount);

        otherAccounts = accountFactory.createAccountList("otherAccount", 10);
        accountRepository.saveAll(otherAccounts);
    }

    @DisplayName("사용자 이름으로 검색 - 성공")
    @Test
    public void UserNameSearch_Success() throws Exception {

        // given
        String searchType = SearchType.USER_NAME.name();
        String searchQuery = "other";

        // when
        mockMvc.perform(get("/api/v1/search")
                        .param("page", "0")
                        .param("type", searchType)
                        .param("q", searchQuery))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    var resultPage = objectMapper.readValue(json, new TypeReference<PagingDto<UserNameSearchResult>>() {
                    });

                    assertThat(resultPage.getTotalElements()).isEqualTo(otherAccounts.size());
                });

        // then

    }
}