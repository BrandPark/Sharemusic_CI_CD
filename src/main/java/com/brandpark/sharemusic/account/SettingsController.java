package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.CurrentAccount;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.account.dto.UpdatePasswordForm;
import com.brandpark.sharemusic.account.validator.UpdatePasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequestMapping("/accounts/edit")
@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @InitBinder("updatePasswordForm")
    public void initPasswordBinder(WebDataBinder binder) {
        binder.addValidators(new UpdatePasswordFormValidator());
    }

    @GetMapping("/basicinfo")
    public String basicInfoForm(@CurrentAccount Account account, Model model) {

        UpdateBasicInfoForm form = modelMapper.map(account, UpdateBasicInfoForm.class);

        String bio = form.getBio();
        if (bio != null) {
            bio = bio.replaceAll("<br>", "\n");
            form.setBio(bio);
        }

        model.addAttribute(account);
        model.addAttribute(form);

        return "accounts/settings/basic-info";
    }

    @PostMapping("/basicinfo")
    public String basicInfoSubmit(@CurrentAccount Account account, @Valid UpdateBasicInfoForm form
            , BindingResult errors, Model model, RedirectAttributes attributes) {

        form.setEmail(account.getEmail());

        // 같은 닉네임이 있는지 확인
        if (!account.getNickname().equals(form.getNickname())) {

            Account duplicatedNicknameAccount = accountRepository.findByNickname(form.getNickname());
            if (duplicatedNicknameAccount != null) {
                errors.rejectValue("nickname", "error.nickname", "이미 존재하는 닉네임입니다.");
            }
        }

        if (errors.hasErrors()) {
            model.addAttribute(form);
            model.addAttribute(account);
            return "accounts/settings/basic-info";
        }

        // bio의 개행 처리
        String bio = form.getBio();
        if (bio != null) {
            bio = bio.replaceAll("\n", "<br>");
            form.setBio(bio);
        }

        accountService.updateBasicInfo(form, account);

        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");
        return "redirect:/accounts/edit/basicinfo";
    }

    @GetMapping("/password")
    public String passwordForm(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new UpdatePasswordForm());

        return "accounts/settings/password";
    }

    @PostMapping("/password")
    public String passwordForm(@CurrentAccount Account account, @Valid UpdatePasswordForm form, BindingResult errors
            , Model model, RedirectAttributes attributes) {

        if (!passwordEncoder.matches(form.getCurrentPassword(), account.getPassword())) {
            errors.rejectValue("currentPassword", "error.currentPassword"
                    , "현재 비밀번호가 일치하지 않습니다.");
        }

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "accounts/settings/password";
        }

        accountService.updatePassword(form, account);

        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");
        return "redirect:/accounts/edit/password";
    }
}


