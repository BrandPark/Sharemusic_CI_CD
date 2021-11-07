package com.brandpark.sharemusic.modules.event;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Component
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final AlbumRepository albumRepository;
    private final FollowRepository followRepository;

    @EventListener
    public void handleFollowEvent(FollowEvent event) {

        String message = String.format("%s 님이 회원님을 팔로우하기 시작했습니다."
                , event.getFollower().getNickname());

        notificationRepository.save(Notification.builder()
                .account(event.getFollowingTarget())
                .sender(event.getFollower())
                .message(message)
                .link("/accounts/" + event.getFollower().getNickname())
                .checked(false)
                .notificationType(NotificationType.FOLLOW)
                .build());
    }

    @EventListener
    public void handleCommentEvent(CommentEvent event) {

        Account writer = accountRepository.findById(event.getWriterId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Account targetAccount = accountRepository.findByAlbumId(event.getCommentTargetAlbumId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Album targetAlbum = albumRepository.findById(event.getCommentTargetAlbumId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ALBUM_EXCEPTION));

        String message = String.format("%s 님이 앨범 \"%s\"에 댓글을 남겼습니다."
                , targetAccount.getNickname()
                , targetAlbum.getTitle());

        notificationRepository.save(Notification.builder()
                .account(targetAccount)
                .sender(writer)
                .message(message)
                .link("/albums/" + targetAlbum.getId())
                .checked(false)
                .notificationType(NotificationType.FOLLOW)
                .build());
    }

    @EventListener
    public void handleCreateAlbumEvent(CreateAlbumEvent event) {

        Account albumCreator = accountRepository.findById(event.getCreatorId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Album createdAlbum = albumRepository.findById(event.getAlbumId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ALBUM_EXCEPTION));

        List<Account> followers = accountRepository.findAllFollowersByFollowingTargetId(event.getCreatorId());

        List<Notification> notifications = new ArrayList<>();

        String message = String.format("%s 님이 새로운 앨범을 업로드 하였습니다.", albumCreator.getNickname());
        for (Account follower : followers) {
            notifications.add(Notification.builder()
                    .account(follower)
                    .sender(albumCreator)
                    .message(message)
                    .link("/albums/" + createdAlbum.getId())
                    .checked(false)
                    .notificationType(NotificationType.FOLLOW)
                    .build());
        }

        notificationRepository.saveAll(notifications);
    }
}
