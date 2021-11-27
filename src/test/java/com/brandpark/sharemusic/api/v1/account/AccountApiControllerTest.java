package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.account.dto.AccountInfoResponse;
import com.brandpark.sharemusic.api.v1.account.dto.CreateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdatePasswordRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class AccountApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountFactory accountFactory;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager entityManager;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    Account otherAccount;
    Account myAccount;
    Account verifiedMyAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("myAccount");
        verifiedMyAccount = accountFactory.persistAccount("verifiedMyAccount", Role.USER);
        otherAccount = accountFactory.persistAccount("otherAccount");
    }

    @DisplayName("모든 계정 페이징 조회 - 성공(꽉찬 첫번째 페이지 조회)")
    @Test
    public void findAllAccountByPage_Success_When_FirstPage() throws Exception {

        // given
        accountFactory.persistAccountList("otherAccounts", 10);

        int pageNum = 0;
        int pageSize = 10;
        int totalElementCount = 13;

        int expectedFindResultCount = 10;

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rs -> {
                    String resultJson = rs.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AccountInfoResponse> resultPage = objectMapper.readValue(resultJson, new TypeReference<>() {
                    });

                    // then
                    AssertUtil.assertPageResult(pageNum, pageSize, totalElementCount, resultPage);

                    List<AccountInfoResponse> result = resultPage.getContent();

                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    assertAccountInfoResponse(result.get(0));
                });
    }

    @DisplayName("모든 계정 페이징 조회 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void findAllAccountByPage_Success_When_LastPage() throws Exception {

        // given
        accountFactory.persistAccountList("otherAccounts", 10);

        int pageNum = 1;
        int pageSize = 10;
        int totalElementCount = 13;

        int expectedFindResultCount = 3;

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rs -> {

                    String resultJson = rs.getResponse().getContentAsString(StandardCharsets.UTF_8);

                    PageResult<AccountInfoResponse> resultPage = objectMapper.readValue(resultJson, new TypeReference<>() {
                    });

                    // then
                    AssertUtil.assertPageResult(pageNum, pageSize, totalElementCount, resultPage);

                    List<AccountInfoResponse> result = resultPage.getContent();

                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    AccountInfoResponse resultOne = result.get(0);
                    assertAccountInfoResponse(resultOne);
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(다른 사람 계정 수정 시도)")
    @Test
    public void UpdateAccount_Fail_When_NotMyAccount() throws Exception {

        // given
        String url = "/api/v1/accounts/" + otherAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정닉네임");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Name 이 비어있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_EmptyName() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정 닉네임");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Name 에 공백이 있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_WhiteSpaceInName() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("공 백");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정닉네임");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Nickname 에 특수문자가 있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_NameContainsSpecialCharacter() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름!");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정닉네임");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Nickname 이 비어있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_EmptyNickname() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname("");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Nickname 에 공백이 있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_WhiteSpaceInNickname() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname("공 백");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(Nickname 에 특수문자가 있는 경우)")
    @Test
    public void UpdateAccount_Fail_When_NicknameContainsSpecialCharacter() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정닉네임!");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 실패(이미 존재하는 닉네임인 경우)")
    @Test
    public void UpdateAccount_Fail_When_DuplicateNickname() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname(otherAccount.getNickname());
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.DUPLICATE_FIELD_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 수정 - 실패(로그인하지 않은 경우)")
    @Test
    public void UpdateAccount_Fail_When_UnAuthenticated() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname(otherAccount.getNickname());
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 수정 - 성공")
    @Test
    public void UpdateAccount_Success() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId();

        UpdateAccountRequest reqData = new UpdateAccountRequest();
        reqData.setName("수정이름");
        reqData.setBio("수정 소개");
        reqData.setNickname("수정닉네임");
        reqData.setProfileImage("수정 이미지");

        // when
        mockMvc.perform(put(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isOk());

        // then
        Account updatedAccount = findAccountById(myAccount.getId());

        assertThat(updatedAccount.getNickname()).isEqualTo(reqData.getNickname());
        assertThat(updatedAccount.getBio()).isEqualTo(reqData.getBio());
        assertThat(updatedAccount.getName()).isEqualTo(reqData.getName());
        assertThat(updatedAccount.getProfileImage()).isEqualTo(reqData.getProfileImage());
    }

    @DisplayName("계정 생성 - 실패(Name 이 비어있는 경우)")
    @Test
    public void CreateAccount_Fail_When_EmptyName() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Name 에 공백이 있는 경우)")
    @Test
    public void CreateAccount_Fail_When_WhiteSpaceInName() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("공 백");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Name 에 특수문자가 포함된 경우)")
    @Test
    public void CreateAccount_Fail_When_NameContainsSpecialCharacter() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("new-Account-Name");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password123");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Nickname 이 비어있는 경우)")
    @Test
    public void CreateAccount_Fail_When_EmptyNickname() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname("");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Nickname 에 공백이 있는 경우)")
    @Test
    public void CreateAccount_Fail_When_WhiteSpaceInNickname() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname("공 백");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Nickname 에 특수문자가 포함된 경우)")
    @Test
    public void CreateAccount_Fail_When_NickNameContainsSpecialCharacter() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname("new-Account-Nickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password123");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(Nickname 이 이미 존재하는 경우)")
    @Test
    public void CreateAccount_Fail_When_DuplicateNickname() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname(otherAccount.getNickname());
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.DUPLICATE_FIELD_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(이메일 형식이 잘못된 경우)")
    @Test
    public void CreateAccount_Fail_When_InvalidEmailForm() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("wrongEmail.naver.com");
        reqData.setName("newAccountName");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(이미 사용중인 이메일인 경우)")
    @Test
    public void CreateAccount_Fail_When_AlreadyUsedEmail() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail(otherAccount.getEmail());
        reqData.setName("newAccountName");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.DUPLICATE_FIELD_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 실패(비밀번호를 다르게 입력할 경우)")
    @Test
    public void CreateAccount_Fail_When_UnMatchPassword() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password123");

        String url = "/api/v1/accounts";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 생성 - 성공")
    @Test
    public void CreateAccount_Success() throws Exception {

        // given
        CreateAccountRequest reqData = new CreateAccountRequest();
        reqData.setEmail("newAccount@email.com");
        reqData.setName("newAccountName");
        reqData.setNickname("newAccountNickname");
        reqData.setPassword("password");
        reqData.setConfirmPassword("password");

        String url = "/api/v1/accounts";
        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isOk());

        // then
        Account newAccount = findAccountByNickname(reqData.getNickname());
        assertThat(newAccount.getId()).isNotNull();
        assertThat(newAccount.getEmail()).isEqualTo(reqData.getEmail());
        assertThat(newAccount.getName()).isEqualTo(reqData.getName());
        assertThat(newAccount.getNickname()).isEqualTo(reqData.getNickname());
        assertThat(passwordEncoder.matches(reqData.getPassword(), newAccount.getPassword())).isTrue();

        assertThat(newAccount.getRole()).isEqualTo(Role.GUEST);
        assertThat(newAccount.getEmailCheckToken()).isNotNull();
        assertThat(newAccount.getEmailCheckTokenGeneratedAt()).isNotNull();
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(다른 사람 계정 변경 시도)")
    @Test
    public void UpdatePassword_Fail_When_NotMyAccount() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();
        final String updatePassword = "123123123";

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword(updatePassword);
        reqData.setConfirmPassword(updatePassword);

        String url = "/api/v1/accounts/" + otherAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(현재 비밀번호가 일치하지 않는 경우)")
    @Test
    public void UpdatePassword_Fail_When_IncorrectCurrentPassword() throws Exception {

        // given
        final String wrongOriginPassword = "111111111";
        final String updatePassword = "123123123";

        assertThat(isSamePassword(wrongOriginPassword, myAccount.getPassword())).isFalse();

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(wrongOriginPassword);
        reqData.setUpdatePassword(updatePassword);
        reqData.setConfirmPassword(updatePassword);

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(현재 비밀번호를 입력하지 않은 경우)")
    @Test
    public void UpdatePassword_Fail_When_EmptyCurrentPassword() throws Exception {

        // given
        final String updatePassword = "123123123";

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword("");
        reqData.setUpdatePassword(updatePassword);
        reqData.setConfirmPassword(updatePassword);

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(변경할 비밀번호를 입력하지 않은 경우)")
    @Test
    public void UpdatePassword_Fail_When_EmptyUpdatePassword() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword("");
        reqData.setConfirmPassword("");

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(비밀번호 확인 입력이 일치하지 않는 경우)")
    @Test
    public void UpdatePassword_Fail_When_UnMatchConfirmPassword() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword("123123123");
        reqData.setConfirmPassword("111111111");

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 실패(변경할 비밀번호가 현재 비밀번호와 같은 경우)")
    @Test
    public void UpdatePassword_Fail_When_OriginEqualUpdate() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword(originPassword);
        reqData.setConfirmPassword(originPassword);

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("비밀번호 변경 - 실패(로그인 하지 않은 경우)")
    @Test
    public void UpdatePassword_Fail_When_UnAuthenticated() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();
        final String updatePassword = "123123123";

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword(updatePassword);
        reqData.setConfirmPassword(updatePassword);

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 - 성공")
    @Test
    public void UpdatePassword_Success() throws Exception {

        // given
        final String originPassword = accountFactory.getPassword();
        final String updatePassword = "123123123";

        assertThat(isSamePassword(originPassword, myAccount.getPassword())).isTrue();

        UpdatePasswordRequest reqData = new UpdatePasswordRequest();
        reqData.setOriginPassword(originPassword);
        reqData.setUpdatePassword(updatePassword);
        reqData.setConfirmPassword(updatePassword);

        String url = "/api/v1/accounts/" + myAccount.getId() + "/password";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqData)))
                .andExpect(status().isOk());

        // then
        Account updatedAccount = findAccountById(myAccount.getId());
        assertThat(isSamePassword(updatePassword, updatedAccount.getPassword())).isTrue();
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 이메일 인증 - 실패(토큰이 일치하지 않는 경우)")
    @Test
    public void VerifyEmail_Fail_When_UnMatchEmailToken() throws Exception {

        // given
        String reqData = "wrongEmailCheckToken";

        String url = "/api/v1/accounts/" + myAccount.getId() + "/verify";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("emailCheckToken", reqData))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 이메일 인증 - 실패(다른 사람의 계정 인증 시도)")
    @Test
    public void VerifyEmail_Fail_When_NotMyAccount() throws Exception {

        // given
        String reqData = otherAccount.getEmailCheckToken();

        String url = "/api/v1/accounts/" + otherAccount.getId() + "/verify";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("emailCheckToken", reqData))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "verifiedMyAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 이메일 인증 - 실패(이미 인증된 이메일 계정)")
    @Test
    public void VerifyEmail_Fail_When_AlreadyVerifiedEmail() throws Exception {

        // given
        String reqData = verifiedMyAccount.getEmailCheckToken();

        String url = "/api/v1/accounts/" + verifiedMyAccount.getId() + "/verify";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("emailCheckToken", reqData))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @DisplayName("계정 이메일 인증 - 실패(로그인하지 않은 경우)")
    @Test
    public void VerifyEmail_Fail_When_UnAuthenticated() throws Exception {

        // given
        String reqData = verifiedMyAccount.getEmailCheckToken();

        String url = "/api/v1/accounts/" + verifiedMyAccount.getId() + "/verify";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("emailCheckToken", reqData))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "myAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 이메일 인증 - 성공")
    @Test
    public void VerifyEmail_Success() throws Exception {

        // given
        assertThat(myAccount.getRole()).isEqualTo(Role.GUEST);

        String reqData = myAccount.getEmailCheckToken();

        String url = "/api/v1/accounts/" + myAccount.getId() + "/verify";

        // when
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("emailCheckToken", reqData))
                .andExpect(status().isOk());

        // then
        assertThat(myAccount.getRole()).isEqualTo(Role.USER);
    }

    private ExceptionResult getExceptionResult(MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ExceptionResult.class);
    }

    private void assertAccountInfoResponse(AccountInfoResponse resultOne) {
        assertThat(resultOne.getAccountId()).isNotNull();
        assertThat(resultOne.getCreatedDate()).isNotNull();
        assertThat(resultOne.getEmail()).containsIgnoringCase("account");
        assertThat(resultOne.getBio()).containsIgnoringCase("account");
        assertThat(resultOne.getName()).containsIgnoringCase("account");
        assertThat(resultOne.getNickname()).containsIgnoringCase("account");
        assertThat(resultOne.getProfileImage()).containsIgnoringCase("image");
        assertThat(resultOne.getRole()).isNotNull();
        assertThat(resultOne.getEmailVerified()).isNotNull();
    }

    private boolean isSamePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private Account findAccountById(Long accountId) {
        entityManager.flush();
        entityManager.clear();

        return accountRepository.findById(accountId).get();
    }

    private Account findAccountByNickname(String nickname) {
        entityManager.flush();
        entityManager.clear();

        return accountRepository.findByNickname(nickname);
    }
}