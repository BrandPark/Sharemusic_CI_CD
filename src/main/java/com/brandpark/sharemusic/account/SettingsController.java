package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.CurrentAccount;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.account.validator.UpdateBasicInfoFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final UpdateBasicInfoFormValidator updateBasicInfoFormValidator;

    @InitBinder("UpdateBasicInfoForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(updateBasicInfoFormValidator);
    }

    @GetMapping("/basicinfo")
    public String basicInfoForm(@CurrentAccount Account account, Model model) {

        UpdateBasicInfoForm form = modelMapper.map(account, UpdateBasicInfoForm.class);

        String bio = form.getBio();
        bio = bio.replaceAll("<br>", "\n");
        form.setBio(bio);

        model.addAttribute(form);

        return "accounts/settings/basic-info";
    }

    @PostMapping("/basicinfo")
    public String basicInfoSubmit(@CurrentAccount Account account, @Valid UpdateBasicInfoForm form
            , BindingResult errors, Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            return "accounts/settings/basic-info";
        }

        accountService.updateBasicInfo(form, account);
        attributes.addFlashAttribute("updateMessage", "프로필이 수정되었습니다.");

        return "redirect:/accounts/edit/basicinfo";
    }
}


