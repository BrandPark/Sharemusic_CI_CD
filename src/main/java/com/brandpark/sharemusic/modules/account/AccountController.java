package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.modules.Validator;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.account.service.VerifyMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;
    private final VerifyMailService verifyMailService;
    private final AccountRepository accountRepository;
    private final Validator formValidator;

    @GetMapping("/signup")
    public String signUpForm(Model model) {

        model.addAttribute(new SignUpForm());
        return "accounts/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm form, BindingResult errors) {

        formValidator.validateSignUpForm(form, errors);
        if (errors.hasErrors()) {
            return "accounts/signup";
        }

        Account newAccount = accountService.signUp(form);

        SessionAccount sessionAccount = accountService.mapToSessionAccount(newAccount);
        verifyMailService.sendSignUpConfirmMail(sessionAccount);

        return "redirect:/send-mail-info";
    }

    @GetMapping("/{nickname}")
    public String profileView(@PathVariable String nickname, Model model) {

        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "은(는) 존재하지 않는 닉네임 입니다.");
        }

        model.addAttribute("account", account);;

        return "accounts/profile";
    }
}
