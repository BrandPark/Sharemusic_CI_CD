package com.brandpark.sharemusic.api.v1;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OldApiValidator {

    public void validateNotification(Long loginId, Long notificationAccountId) {
        if (!loginId.equals(notificationAccountId)) {
            throw new ApiException(Error.FORBIDDEN_EXCEPTION, "해당 알림에 대한 권한이 없습니다.");
        }
    }

}
