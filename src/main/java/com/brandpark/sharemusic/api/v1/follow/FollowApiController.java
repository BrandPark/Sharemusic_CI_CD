package com.brandpark.sharemusic.api.v1.follow;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowerInfoResponse;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowingInfoResponse;
import com.brandpark.sharemusic.api.v1.follow.query.FollowQueryRepository;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.api.v1.Validator;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@RestController
public class FollowApiController {

    private final AccountService accountService;
    private final FollowQueryRepository followQueryRepository;
    private final Validator validator;

    @GetMapping("/accounts/{targetAccountId}/followers")
    public PageResult<FollowerInfoResponse> findAllFollowersByPaging(@PathVariable Long targetAccountId, @PageableDefault Pageable pageable) {

        validator.validateFindAllFollowers(targetAccountId);

        return followQueryRepository.findAllFollowersByPaging(targetAccountId, pageable);
    }

    @GetMapping("/accounts/{targetAccountId}/followings")
    public PageResult<FollowingInfoResponse> findAllFollowingsByPaging(@PathVariable Long targetAccountId, @PageableDefault Pageable pageable) {

        validator.validateFindAllFollowings(targetAccountId);

        return followQueryRepository.findAllFollowingsByPaging(targetAccountId, pageable);
    }

    @PostMapping("/accounts/{targetAccountId}/follow")
    public Long doFollow(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId) {

        validator.validateFollow(loginAccount, targetAccountId);

        return accountService.doFollow(loginAccount.getId(), targetAccountId);
    }

    @PostMapping("/accounts/{targetAccountId}/unfollow")
    public Long doUnfollow(@LoginAccount SessionAccount loginAccount, @PathVariable Long targetAccountId) {

        validator.validateUnfollow(loginAccount, targetAccountId);

        return accountService.doUnfollow(loginAccount.getId(), targetAccountId);
    }
}
