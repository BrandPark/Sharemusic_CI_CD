package com.brandpark.sharemusic.modules.account.account;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.account.form.UpdateNotificationSettingForm;
import com.brandpark.sharemusic.modules.account.account.form.UpdatePasswordForm;
import com.brandpark.sharemusic.modules.account.account.service.AccountService;
import com.brandpark.sharemusic.modules.validator.FormValidator;
import lombok.RequiredArgsConstructor;
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
    private final FormValidator formValidator;

    @GetMapping("/basicinfo")
    public String basicInfoForm(@LoginAccount SessionAccount account, Model model) {

        UpdateBasicInfoForm form = new UpdateBasicInfoForm(account);

        model.addAttribute("account", account);
        model.addAttribute(form);

        return "accounts/settings/basic-info";
    }

    @PostMapping("/basicinfo")
    public String basicInfoSubmit(@LoginAccount SessionAccount account, @Valid UpdateBasicInfoForm form
            , BindingResult errors, Model model, RedirectAttributes attributes) {

        formValidator.validateBasicInfoForm(account, form, errors);
        if (errors.hasErrors()) {
            model.addAttribute(form);
            model.addAttribute("account", account);
            return "accounts/settings/basic-info";
        }

        accountService.updateBasicInfo(form.toModuleDto(), account);

        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");
        return "redirect:/accounts/edit/basicinfo";
    }

    @GetMapping("/password")
    public String passwordForm(@LoginAccount SessionAccount account, Model model) {

        model.addAttribute("account", account);;
        model.addAttribute(new UpdatePasswordForm());

        return "accounts/settings/password";
    }

    @PostMapping("/password")
    public String passwordForm(@LoginAccount SessionAccount account, @Valid UpdatePasswordForm form, BindingResult errors
            , Model model, RedirectAttributes attributes) {

        formValidator.validatePasswordForm(account, form, errors);

        if (errors.hasErrors()) {
            model.addAttribute("account", account);;
            return "accounts/settings/password";
        }

        accountService.updatePasswordInfo(form.toModuleDto(), account);

        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");
        return "redirect:/accounts/edit/password";
    }

    @GetMapping("/notification")
    public String notificationSettingForm(@LoginAccount SessionAccount account, Model model) {

        UpdateNotificationSettingForm form = new UpdateNotificationSettingForm(account);

        model.addAttribute("account", account);
        model.addAttribute("form", form);

        return "accounts/settings/notification";
    }

    @PostMapping("/notification")
    public String notificationSettingForm(@LoginAccount SessionAccount account, Model model
            , @Valid UpdateNotificationSettingForm form, BindingResult errors, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "accounts/settings/notification";
        }

        accountService.updateNotificationSetting(form.toModuleDto(), account);
        attributes.addFlashAttribute("updateMessage", "알림 설정이 수정되었습니다.");

        return "redirect:/accounts/edit/notification";
    }
}


