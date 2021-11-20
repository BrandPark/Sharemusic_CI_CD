package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.account.dto.AccountInfoResponse;
import com.brandpark.sharemusic.api.v1.account.dto.CreateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.NOT_FOUND_ACCOUNT_EXCEPTION;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AccountApiController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping("/accounts")
    public PageResult<AccountInfoResponse> getAllAccountInfosByPage(@PageableDefault Pageable pageable) {

        Page<Account> allAccountPage = accountRepository.findAll(pageable);

        List<AccountInfoResponse> resultContent = allAccountPage.getContent()
                .stream()
                .map(AccountInfoResponse::new)
                .collect(Collectors.toList());

        return PageResultFactory.createPageResult(resultContent, pageable, allAccountPage.getTotalElements());
    }

    @PutMapping("/accounts/{targetAccountId}")
    public Long updateAccount(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId
            , @RequestBody UpdateAccountRequest reqDto) {

        if (!loginAccount.getId().equals(targetAccountId)) {
            throw new ApiException(Error.FORBIDDEN_EXCEPTION);
        }

        Account targetAccount = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION, targetAccountId + " 계정을 찾을 수 없습니다."));

        accountService.updateInfo(new UpdateAccountDto(reqDto), targetAccount);

        return targetAccountId;
    }

    @PostMapping("/accounts")
    public Long createAccount(CreateAccountRequest reqDto) {
        return accountService.createAccount(reqDto.toModuleDto());
    }
}
