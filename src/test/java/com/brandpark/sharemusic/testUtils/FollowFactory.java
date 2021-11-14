package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.follow.domain.Follow;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class FollowFactory {

    private final FollowRepository followRepository;

    public List<Follow> createFollowRelationship(Account target, List<Account> followers) {

        List<Follow> result = new ArrayList<>();

        for (Account follower : followers) {
            result.add(Follow.builder()
                    .follower(follower)
                    .target(target)
                    .build());
        }

        return result;
    }
}
