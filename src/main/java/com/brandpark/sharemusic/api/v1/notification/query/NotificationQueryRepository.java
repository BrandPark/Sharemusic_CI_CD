package com.brandpark.sharemusic.api.v1.notification.query;

import com.brandpark.sharemusic.api.PagingDtoFactory;
import com.brandpark.sharemusic.api.v1.notification.query.dto.NotificationInfo;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
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
    QAccount account = QAccount.account;
    QNotification notification = QNotification.notification;

    public PagingDto<NotificationInfo> findAllNotifications(Pageable pageable, NotificationType type, Long accountId) {

        QueryResults<NotificationInfo> queryResults = queryFactory.select(
                        Projections.fields(NotificationInfo.class,
                                notification.id,
                                account.profileImage.as("senderProfileImage"),
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
    }

    private BooleanExpression whatType(NotificationType type) {
        if (type == null) {
            return null;
        }

        return notification.notificationType.eq(type);
    }
}
