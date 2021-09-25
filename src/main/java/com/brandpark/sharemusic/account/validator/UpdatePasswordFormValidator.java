package com.brandpark.sharemusic.account.validator;

import com.brandpark.sharemusic.account.dto.UpdatePasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UpdatePasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdatePasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UpdatePasswordForm form = (UpdatePasswordForm) target;
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.confirmPassword", "패스워드가 일치하지 않습니다.");
        }
    }
}
