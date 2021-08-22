package com.brandpark.sharemusic.web.api;

import com.brandpark.sharemusic.service.UserService;
import com.brandpark.sharemusic.web.api.dto.UserSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/v1/users")
    public Long userSave(@RequestBody UserSaveRequestDto requestDto) {
        return userService.saveUser(requestDto);
    }
}
