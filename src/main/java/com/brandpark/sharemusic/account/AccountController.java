package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.form.SignUpForm;
import com.brandpark.sharemusic.account.validator.SignUpFormValidator;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
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

        Account newAccount = accountService.processCreateAccount(form);

        accountService.login(newAccount);

        // 6. 이메일 확인 화면 보여주기

        return "redirect:/";
    }
}
