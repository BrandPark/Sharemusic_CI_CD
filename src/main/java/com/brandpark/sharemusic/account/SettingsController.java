package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.CurrentAccount;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/accounts/edit")
@RequiredArgsConstructor
@Controller
public class SettingsController {

    private final ModelMapper modelMapper;
    private final AccountService accountService;

    @GetMapping("/basicinfo")
    public String basicInfoForm(@CurrentAccount Account account, Model model) {

        UpdateBasicInfoForm form = modelMapper.map(account, UpdateBasicInfoForm.class);
        model.addAttribute(form);

        return "accounts/settings/basic-info";
    }

    @PostMapping("/basicinfo")
    public String basicInfoSubmit(@CurrentAccount Account account, @Valid UpdateBasicInfoForm form, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "accounts/settings/basic-info";
        }

        accountService.updateBasicInfo(form, account);

        return "redirect:";
    }
}


