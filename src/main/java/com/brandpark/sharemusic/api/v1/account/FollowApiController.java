package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import com.brandpark.sharemusic.modules.follow.domain.Follow;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.brandpark.sharemusic.api.v1.exception.Error.NOT_FOUND_ACCOUNT_EXCEPTION;

@RequiredArgsConstructor
@RestController
public class FollowApiController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final FollowRepository followRepository;
    private final AccountQueryRepository accountQueryRepository;

    @PostMapping("/accounts/{targetId}/follow")
    public Long doFollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        Account follower = accountRepository.findById(account.getId())
                .orElseThrow(() -> new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION));

        Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION));

        if (followRepository.isFollowing(account.getId(), targetId) || account.getId().equals(targetId)) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION);
        }

        return accountService.doFollow(follower, target);
    }

    @PostMapping("/accounts/{targetId}/unfollow")
    public Long doUnfollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION);
        }

        Follow follow = followRepository.findByFollowerIdAndTargetId(account.getId(), targetId)
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION));

        followRepository.delete(follow);

        return follow.getId();
    }

    @GetMapping("/accounts/{targetId}/followers")
    public PagingDto<FollowerInfoDto> findAllFollowersByPaging(@PathVariable Long targetId, @PageableDefault(size = 6) Pageable pageable) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION);
        }
        return accountQueryRepository.findAllFollowersByPaging(targetId, pageable);
    }

    @GetMapping("/accounts/{targetId}/followings")
    public PagingDto<FollowingInfoDto> findAllFollowingsByPaging(@PathVariable Long targetId, @PageableDefault(size = 6) Pageable pageable) {

        if (!accountRepository.existsById(targetId)) {
            throw new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION);
        }

        return accountQueryRepository.findAllFollowingsByPaging(targetId, pageable);
    }
}
