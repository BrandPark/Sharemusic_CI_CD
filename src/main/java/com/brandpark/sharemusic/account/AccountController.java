package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "accounts/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            System.out.println("회원가입 실패");
            return "accounts/signup";
        }

        // TODO: 회원가입 로직
        // 1. 닉네임이 있는지, 이메일이 있는지 확인(valid)
        // 2. Account 생성
        // 3. 토큰, AccountRole 생성
        // 4. 이메일 전송
        // 5. 로그인
        // 6. 이메일 확인 화면 보여주기

        return "redirect:/";
    }
}
