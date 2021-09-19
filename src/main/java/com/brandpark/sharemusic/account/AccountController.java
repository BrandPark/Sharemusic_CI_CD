package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.form.SignUpForm;
import com.brandpark.sharemusic.account.validator.SignUpFormValidator;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Collections;

@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final MailService mailService;

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

        Account newAccount = accountService.createAccount(form);

        sendEmailCheckMail(newAccount);

        // 5. 로그인
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                newAccount.getEmail()
                , newAccount.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(newAccount.getRole().getKey()))
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);

        // 6. 이메일 확인 화면 보여주기

        return "redirect:/";
    }

    private void sendEmailCheckMail(Account account) {
        MailMessage message = new MailMessage();
        message.setText(account.getEmailCheckToken());
        message.setTitle("ShareMusic");
        message.setTo(account.getEmail());

        mailService.send(message);
    }
}
