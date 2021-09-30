package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import com.brandpark.sharemusic.modules.account.dto.VerificationEmailToken;
import com.brandpark.sharemusic.modules.account.service.VerifyMailService;
import com.brandpark.sharemusic.modules.account.validator.Validation;
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

    private final Validation validation;
    private final VerifyMailService verifyMailService;

    @PostMapping("/resend-verify-mail")
    public String resendVerifyMail(@CurrentAccount Account account) {

        verifyMailService.sendSignUpConfirmMail(account);
        return "redirect:/send-mail-info";
    }

    @GetMapping("/send-mail-info")
    public String sendMailInfo(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);

        return "accounts/mails/send-mail-info";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@CurrentAccount Account account, @Valid VerificationEmailToken verificationEmail, BindingResult errors
            , Model model, RedirectAttributes attributes) {

        validation.validateVerificationEmailToken(verificationEmail.getToken(), verificationEmail.getEmail(), errors);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "accounts/mails/verify-email-result";
        }

        verifyMailService.succeedVerifyEmailCheckToken(account);

        attributes.addFlashAttribute("successMessage", "계정 인증이 완료되었습니다.");
        return "redirect:/verify-email-result";
    }

    @GetMapping("/verify-email-result")
    public String verifyEmailResult(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new VerificationEmailToken());
        return "accounts/mails/verify-email-result";
    }
}
