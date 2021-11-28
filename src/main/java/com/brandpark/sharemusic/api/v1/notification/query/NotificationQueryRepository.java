package com.brandpark.sharemusic.api.v1.notification.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.notification.dto.NotificationInfoResponse;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.QNotification;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;
    private QAccount account = QAccount.account;
    private QNotification notification = QNotification.notification;

    public PageResult<NotificationInfoResponse> findAllNotifications(Pageable pageable, NotificationType type, Long accountId) {

        QueryResults<NotificationInfoResponse> queryResults = queryFactory.select(
                        Projections.fields(NotificationInfoResponse.class,
                                notification.id,
                                notification.sender.profileImage.as("senderProfileImage"),
                                notification.sender.nickname.as("senderNickname"),
                                notification.message,
                                notification.link,
                                notification.checked,
                                notification.createdDate,
                                notification.notificationType
                        ))
                .from(notification)
                .innerJoin(account).on(account.id.eq(notification.account.id))
                .where(
                        account.id.eq(accountId),
                        whatType(type)
                )
                .orderBy(notification.checked.asc(), notification.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    private BooleanExpression whatType(NotificationType type) {
        if (type == null) {
            return null;
        }

        return notification.notificationType.eq(type);
    }
}
