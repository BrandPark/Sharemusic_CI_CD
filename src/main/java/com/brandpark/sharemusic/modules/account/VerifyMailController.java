package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.modules.FormValidator;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.form.VerificationEmailToken;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.account.service.VerifyMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class VerifyMailController {

    private final FormValidator formValidator;
    private final VerifyMailService verifyMailService;
    private final AccountService accountService;

    @PostMapping("/resend-verify-mail")
    public String resendVerifyMail(@LoginAccount SessionAccount account) {

        verifyMailService.sendSignUpConfirmMail(account);
        return "redirect:/send-mail-info";
    }

    @GetMapping("/send-mail-info")
    public String sendMailInfo(@LoginAccount SessionAccount account, Model model) {

        model.addAttribute("account", account);;

        return "accounts/mails/send-mail-info";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@LoginAccount SessionAccount account, @Valid VerificationEmailToken verificationEmail, BindingResult errors
            , Model model, RedirectAttributes attributes) {

        formValidator.validateVerificationEmailToken(verificationEmail.getToken(), verificationEmail.getEmail(), errors);
        if (errors.hasErrors()) {
            model.addAttribute("account", account);;
            return "accounts/mails/verify-email-result";
        }

        accountService.succeedVerifyEmailCheckToken(account);

        attributes.addFlashAttribute("successMessage", "계정 인증이 완료되었습니다.");
        return "redirect:/verify-email-result";
    }

    @GetMapping("/verify-email-result")
    public String verifyEmailResult(@LoginAccount SessionAccount account, Model model) {

        model.addAttribute("account", account);;
        model.addAttribute(new VerificationEmailToken());
        return "accounts/mails/verify-email-result";
    }
}
