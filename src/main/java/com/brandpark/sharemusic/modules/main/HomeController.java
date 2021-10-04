package com.brandpark.sharemusic.modules.main;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController implements ErrorController {

    @GetMapping("/")
    public String viewHome(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "home";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("message", "권한이 없습니다!");
            }
        }

        return "error/error";
    }

    @Override
    public String getErrorPath() {
        // deprecated method
        return null;
    }
}
