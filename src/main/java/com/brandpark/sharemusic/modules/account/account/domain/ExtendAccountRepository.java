package com.brandpark.sharemusic.modules.account.account.domain;

import com.brandpark.sharemusic.modules.account.account.form.FriendshipDataForm;

public interface ExtendAccountRepository {
    FriendshipDataForm findFriendshipData(Long accountId);
}
