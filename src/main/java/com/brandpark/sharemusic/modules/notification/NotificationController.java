package com.brandpark.sharemusic.modules.notification;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String notificationListView(@LoginAccount SessionAccount account, Model model) {

        model.addAttribute("account", account);

        return "notifications/list";
    }

}
