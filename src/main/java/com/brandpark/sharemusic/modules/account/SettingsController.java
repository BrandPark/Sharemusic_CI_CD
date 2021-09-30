package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import com.brandpark.sharemusic.modules.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordForm;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.account.validator.Validation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequestMapping("/accounts/edit")
@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final Validation validation;

    @GetMapping("/basicinfo")
    public String basicInfoForm(@CurrentAccount Account account, Model model) {

        UpdateBasicInfoForm form = modelMapper.map(account, UpdateBasicInfoForm.class);
        form.setBio(NewLineUtil.toEscape(form.getBio()));

        model.addAttribute(account);
        model.addAttribute(form);

        return "accounts/settings/basic-info";
    }

    @PostMapping("/basicinfo")
    public String basicInfoSubmit(@CurrentAccount Account account, @Valid UpdateBasicInfoForm form
            , BindingResult errors, Model model, RedirectAttributes attributes) {

        validation.validateBasicInfoForm(account, form, errors);
        if (errors.hasErrors()) {
            model.addAttribute(form);
            model.addAttribute(account);
            return "accounts/settings/basic-info";
        }

        form.setEmail(account.getEmail());
        form.setBio(NewLineUtil.toBrTag(form.getBio()));

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

        validation.validatePasswordForm(account, form, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "accounts/settings/password";
        }

        accountService.updatePassword(form, account);

        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");
        return "redirect:/accounts/edit/password";
    }

    private static abstract class NewLineUtil {
        public static String toEscape(String text) {
            if (text != null) {
                text = text.replaceAll("<br>", "\n");
            }
            return text;
        }

        public static String toBrTag(String text) {
            if (text != null) {
                text = text.replaceAll("\n", "<br>");
            }
            return text;
        }
    }
}


