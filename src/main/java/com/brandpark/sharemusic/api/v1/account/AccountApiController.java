package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.account.dto.AccountInfoResponse;
import com.brandpark.sharemusic.api.v1.account.dto.CreateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdatePasswordRequest;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.Validator;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordDto;
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
    private final Validator validator;

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

        final UpdateAccountDto updateData = reqDto.toModuleDto();

        validator.validateUpdateAccountLogic(loginAccount, targetAccountId, updateData);

        accountService.updateAccountInfo(updateData, targetAccountId);

        return targetAccountId;
    }

    @PostMapping("/accounts")
    public Long createAccount(@RequestBody @Valid CreateAccountRequest reqDto) {

        final CreateAccountDto createData = reqDto.toModuleDto();

        validator.validateCreateAccountLogic(createData);

        return accountService.createAccount(createData);
    }

    @PostMapping("/accounts/{targetAccountId}/password")
    public Long updatePassword(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId
            , @RequestBody @Valid UpdatePasswordRequest reqDto) {

        final UpdatePasswordDto updateData = reqDto.toModuleDto();

        validator.validateUpdatePasswordLogic(loginAccount, targetAccountId, updateData);

        accountService.updatePassword(targetAccountId, updateData);

        return targetAccountId;
    }

    @PostMapping("/accounts/{targetAccountId}/verify")
    public Long verifyEmail(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId
            , String emailCheckToken) {

        validator.validateVerifyEmailCheckTokenLogic(loginAccount, targetAccountId, emailCheckToken);

        accountService.succeedVerifyEmailCheckToken(targetAccountId);

        return targetAccountId;
    }
}
