package com.brandpark.sharemusic.partials.notification;

import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.QNotification;
import com.brandpark.sharemusic.modules.util.page.PagingDtoFactory;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.partials.notification.form.NotificationInfoForm;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class NotificationPartialRepository {

    private final JPAQueryFactory queryFactory;
    QAccount account = QAccount.account;
    QNotification notification = QNotification.notification;

    public PagingDto<NotificationInfoForm> findAllNotifications(Pageable pageable, NotificationType type, Long accountId) {

        QAccount subAccount = new QAccount("subAccount");

        QueryResults<NotificationInfoForm> queryResults = queryFactory.select(Projections.fields(NotificationInfoForm.class,
                        notification.id,
                        ExpressionUtils.as(
                                JPAExpressions.select(subAccount.profileImage)
                                        .from(subAccount)
                                        .where(subAccount.id.eq(notification.senderId))
                                , "senderProfileImage"),
                        ExpressionUtils.as(
                                JPAExpressions.select(subAccount.nickname)
                                        .from(subAccount)
                                        .where(subAccount.id.eq(notification.senderId))
                                , "senderNickname"),
                        notification.message,
                        notification.link,
                        notification.checked,
                        notification.createdDate,
                        notification.notificationType
                ))
                .from(notification)
                .innerJoin(account).on(account.id.eq(notification.accountId))
                .where(
                        account.id.eq(accountId),
                        whatType(type)
                )
                .orderBy(notification.checked.asc(), notification.createdDate.desc())
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
