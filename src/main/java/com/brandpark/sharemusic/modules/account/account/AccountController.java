package com.brandpark.sharemusic.modules.account.account;

import com.brandpark.sharemusic.modules.account.account.form.FriendshipDataForm;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.validator.FormValidator;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.account.service.AccountService;
import com.brandpark.sharemusic.modules.account.account.service.VerifyMailService;
import com.brandpark.sharemusic.modules.account.follow.domain.FollowRepository;
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
    private final FollowRepository followRepository;
    private final FormValidator formValidator;

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

        SessionAccount newAccount = accountService.signUp(form.toModuleDto());

        verifyMailService.sendSignUpConfirmMail(newAccount);

        return "redirect:/send-mail-info";
    }

    @GetMapping("/{nickname}")
    public String viewProfile(@LoginAccount SessionAccount account, @PathVariable String nickname, Model model) {

        formValidator.validateViewProfile(nickname);
        if (account != null) {
            model.addAttribute("account", account);
        }

        Account profileAccount = accountRepository.findByNickname(nickname);
        model.addAttribute("targetAccount", profileAccount);

        boolean isOwner = account != null && nickname.equals(account.getNickname());
        model.addAttribute("isOwner", isOwner);

        boolean isFollowing = account != null && followRepository.isFollowing(account.getId(), profileAccount.getId());
        model.addAttribute("isFollowing", isFollowing);

        FriendshipDataForm friendshipData = accountRepository.findFriendshipData(profileAccount.getId());
        model.addAttribute("friendshipData", friendshipData);

        return "accounts/profile";
    }
}
