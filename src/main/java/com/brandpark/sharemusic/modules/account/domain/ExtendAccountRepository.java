package com.brandpark.sharemusic.modules.account.domain;

import java.util.Optional;

public interface ExtendAccountRepository {
    Optional<Account> findByAlbumId(Long albumId);
}
