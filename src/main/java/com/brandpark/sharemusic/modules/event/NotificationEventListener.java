package com.brandpark.sharemusic.modules.event;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Async
@RequiredArgsConstructor
@Transactional
@Component
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final AlbumRepository albumRepository;
    private final EntityManager entityManager;

    @EventListener
    public void handleFollowEvent(FollowEvent event) {

        Account follower = accountRepository.findById(event.getFollowerId())
                .orElseThrow(() -> new IllegalArgumentException("팔로우를 요청한 계정이 존재하지 않습니다."));

        Account followingTarget = accountRepository.findById(event.getFollowingTargetId())
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 계정이 존재하지 않습니다."));

        String message = String.format("%s 님이 회원님을 팔로우하기 시작했습니다."
                , follower.getNickname());


        if (followingTarget.isNotificationFollowMe()) {
            notificationRepository.save(Notification.builder()
                    .accountId(followingTarget.getId())
                    .senderId(follower.getId())
                    .message(message)
                    .link("/accounts/" + follower.getNickname())
                    .checked(false)
                    .notificationType(NotificationType.FOLLOW)
                    .build());
        }
    }

    @EventListener
    public void handleCommentEvent(CommentEvent event) {

        Account writer = accountRepository.findById(event.getWriterId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 작성자의 계정이 존재하지 않습니다."));

        Account targetAccount = accountRepository.findByAlbumId(event.getCommentTargetAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("알림 대상의 계정이 존재하지 않습니다."));

        Album targetAlbum = albumRepository.findById(event.getCommentTargetAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("댓글이 작성된 앨범이 존재하지 않습니다."));

        String message = String.format("%s 님이 앨범 \"%s\"에 댓글을 남겼습니다."
                , writer.getNickname()
                , targetAlbum.getTitle());

        if (targetAccount.isNotificationCommentOnMyAlbum()) {
            notificationRepository.save(Notification.builder()
                    .accountId(targetAccount.getId())
                    .senderId(writer.getId())
                    .message(message)
                    .link("/albums/" + targetAlbum.getId())
                    .checked(false)
                    .notificationType(NotificationType.COMMENT)
                    .build());
        }
    }

    @EventListener
    public void handleCreateAlbumEvent(CreateAlbumEvent event) {

        Account albumCreator = accountRepository.findById(event.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("앨범 작성자의 계정이 존재하지 않습니다."));

        Album createdAlbum = albumRepository.findById(event.getAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("작성된 앨범이 존재하지 않습니다."));

        List<Account> followers = accountRepository.findAllFollowersByFollowingTargetId(event.getCreatorId());

        List<Notification> notifications = new ArrayList<>();

        String message = String.format("%s 님이 새로운 앨범을 업로드 하였습니다.", albumCreator.getNickname());
        for (Account follower : followers) {
            if (follower.isNotificationAlbumCreatedByMyFollowing()) {
                notifications.add(Notification.builder()
                        .accountId(follower.getId())
                        .senderId(albumCreator.getId())
                        .message(message)
                        .link("/albums/" + createdAlbum.getId())
                        .checked(false)
                        .notificationType(NotificationType.CREATED_ALBUM_BY_FOLLOWER)
                        .build());
            }
        }

        notificationRepository.batchInsert(notifications);
        entityManager.clear();
    }
}
