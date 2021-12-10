package com.brandpark.sharemusic.modules.main;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Controller
public class HomeController implements ErrorController {

    private final FollowRepository followRepository;

    @GetMapping("/")
    public String viewHome(@LoginAccount SessionAccount account, Model model) {

        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("followingCount", followRepository.countAllByFollowerId(account.getId()));
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
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("message", "요청이 유효하지 않습니다.");
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("message", "인증이 유효하지 않습니다.");
            } else {
                model.addAttribute("message", "서버에서 요청을 처리하는데 실패했습니다.");
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
