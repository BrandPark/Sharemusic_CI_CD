package com.brandpark.sharemusic.modules.follow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, ExtendFollowRepository {

    Optional<Follow> findByFollowerIdAndTargetId(Long followerId, Long targetId);
}
