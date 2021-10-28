package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.follow.Follow;
import com.brandpark.sharemusic.modules.follow.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AccountApiController {

    private final AccountRepository accountRepository;
    private final FollowRepository followRepository;
    private final AccountQueryRepository accountQueryRepository;
    private final DtoValidator dtoValidator;

    @PostMapping("/accounts/{targetId}/follow")
    public Long doFollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        Account follower = accountRepository.findById(account.getId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        if (followRepository.isFollowing(account.getId(), targetId)) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION);
        }

        return followRepository.save(Follow.builder()
                .follower(follower)
                .target(target)
                .build()).getId();
    }

    @PostMapping("/accounts/{targetId}/unfollow")
    public Long doUnfollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION);
        }

        Follow follow = followRepository.findByFollowerIdAndTargetId(account.getId(), targetId)
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION));

        followRepository.delete(follow);

        return follow.getId();
    }

    @GetMapping("/accounts/{targetId}/followers")
    public PagingDto<FollowerInfoDto> findAllFollowersByPaging(@PathVariable Long targetId, @PageableDefault(size = 6) Pageable pageable) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION);
        }
        return accountQueryRepository.findAllFollowersByPaging(targetId, pageable);
    }

    @GetMapping("/accounts/{targetId}/followings")
    public PagingDto<FollowingInfoDto> findAllFollowingsByPaging(@PathVariable Long targetId, @PageableDefault(size = 6) Pageable pageable) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION);
        }

        return accountQueryRepository.findAllFollowingsByPaging(targetId, pageable);
    }
}
