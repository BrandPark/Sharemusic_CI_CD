package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.account.dto.AccountInfoResponse;
import com.brandpark.sharemusic.api.v1.account.dto.CreateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdatePasswordRequest;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AccountApiController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final DtoValidator validator;

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
            , @RequestBody @Valid UpdateAccountRequest reqDto) {

        validator.validateSameAccount(loginAccount, targetAccountId);
        validator.validateUpdateAccountData(reqDto, loginAccount, targetAccountId);

        Account targetAccount = accountRepository.findById(targetAccountId).get();

        accountService.updateAccountInfo(reqDto.toModuleAccount(), targetAccount);

        return targetAccountId;
    }

    @PostMapping("/accounts")
    public Long createAccount(@RequestBody @Valid CreateAccountRequest reqDto) {

        validator.validateCreateAccountData(reqDto);

        return accountService.createAccount(reqDto.toModuleDto());
    }

    @PostMapping("/accounts/{targetAccountId}/password")
    public Long updatePassword(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId
            , @RequestBody UpdatePasswordRequest reqDto) {

        validator.validateSameAccount(loginAccount, targetAccountId);
        validator.validateUpdatePassword(reqDto, targetAccountId);

        accountService.updatePassword(targetAccountId, reqDto.toModuleDto());

        return targetAccountId;
    }

    @PostMapping("/accounts/{targetAccountId}/verify")
    public Long verifyEmail(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId
            , String emailCheckToken) {

        validator.validateSameAccount(loginAccount, targetAccountId);
        validator.validateEmailCheckToken(targetAccountId, emailCheckToken);

        accountService.succeedVerifyEmailCheckToken(targetAccountId);

        return targetAccountId;
    }
}
