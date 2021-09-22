package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.CurrentAccount;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.dto.EmailCheckToken;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.validator.EmailCheckTokenValidator;
import com.brandpark.sharemusic.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;
    private final EmailCheckTokenValidator emailCheckTokenValidator;

    @InitBinder("signUpForm")
    public void initSignUpForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @InitBinder("emailCheckToken")
    public void initEmailCheckToken(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(emailCheckTokenValidator);
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {

        model.addAttribute(new SignUpForm());
        return "accounts/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm form, BindingResult errors) {

        if (errors.hasErrors()) {
            return "accounts/signup";
        }

        Account newAccount = accountService.createAccount(form);
        accountService.login(newAccount);

        return "redirect:/accounts/sendmail";
    }

    @GetMapping("/sendmail")
    public String sendMail(@CurrentAccount Account account, Model model) {

        accountService.sendConfirmMail(account);

        model.addAttribute(account);

        return "accounts/email-check-info";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@CurrentAccount Account account, @Valid EmailCheckToken token, BindingResult errors
            , Model model) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "accounts/email-check-info";
        }

        account.assignRole(Role.USER);
        accountRepository.save(account);

        return "redirect:/";
    }

    @GetMapping("/{nickname}")
    public String profileView(@PathVariable String nickname, Model model) {

        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "은(는) 존재하지 않는 닉네임 입니다.");
        }

        model.addAttribute(account);

        return "accounts/profile";
    }
}
