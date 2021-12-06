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

    public Follow persistFollower(Account target, Account follower) {
        return followRepository.save(createFollowRelationship(follower, target));
    }

    public Follow persistFollowing(Account follower, Account target) {
        return followRepository.save(createFollowRelationship(follower, target));
    }

    public List<Follow> persistFollowers(Account target, List<Account> followers) {
        return followRepository.saveAll(createFollowers(target, followers));
    }

    public List<Follow> persistFollowings(Account follower, List<Account> followings) {
        return followRepository.saveAll(createFollowings(follower, followings));
    }

    private List<Follow> createFollowers(Account target, List<Account> followers) {

        List<Follow> result = new ArrayList<>();

        for (Account follower : followers) {
            result.add(createFollowRelationship(follower, target));
        }

        return result;
    }

    private List<Follow> createFollowings(Account follower, List<Account> followings) {

        List<Follow> result = new ArrayList<>();

        for (Account target : followings) {
            result.add(createFollowRelationship(follower, target));
        }

        return result;
    }

    private Follow createFollowRelationship(Account follower, Account target) {
        return Follow.builder()
                .follower(follower)
                .target(target)
                .build();
    }
}
