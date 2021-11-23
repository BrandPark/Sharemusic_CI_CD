package com.brandpark.sharemusic.modules.main;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController /*implements ErrorController*/ {

    @GetMapping("/")
    public String viewHome(@LoginAccount SessionAccount account, Model model) {

        if (account != null) {
            model.addAttribute("account", account);
        }

        return "home";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }

//    @GetMapping("/error")
//    public String error(HttpServletRequest request, Model model) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if (status != null) {
//            Integer statusCode = Integer.valueOf(status.toString());
//
//            if (statusCode == HttpStatus.FORBIDDEN.value()) {
//                model.addAttribute("message", "권한이 없습니다!");
//            }
//        }
//
//        return "error/error";
//    }

//    @Override
//    public String getErrorPath() {
//        // deprecated method
//        return null;
//    }
}
