package com.brandpark.sharemusic.modules.account.domain;

import com.brandpark.sharemusic.modules.account.form.FriendshipDataForm;

public interface ExtendAccountRepository {
    FriendshipDataForm findFriendshipData(Long accountId);
}
