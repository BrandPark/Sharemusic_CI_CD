package com.brandpark.sharemusic.modules.notification.domain.repoImpl;

import com.brandpark.sharemusic.modules.notification.domain.ExtendNotificationRepository;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements ExtendNotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${batchSize}")
    private int batchSize;

    @Override
    public int batchInsert(List<Notification> notifications) {

        int insertCount = 0;

        List<Notification> buff = new ArrayList<>();

        for (int i = 0; i < notifications.size(); i++) {
            buff.add(notifications.get(i));
            if (i + 1 % batchSize == 0) {
                insertCount += flushInsert(buff);
                buff.clear();
            }
        }

        if (!buff.isEmpty()) {
            insertCount += flushInsert(buff);
        }

        return insertCount;
    }

    private int flushInsert(List<Notification> buff) {

        String sql = "insert into notification(" +
                "sender_id, account_id, checked, link, message, notification_type, created_date, modified_date)" +
                " values(?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Notification n = buff.get(i);
                LocalDateTime now = LocalDateTime.now();

                ps.setLong(1, n.getSender().getId());
                ps.setLong(2, n.getAccount().getId());
                ps.setBoolean(3, n.isChecked());
                ps.setString(4, n.getLink());
                ps.setString(5, n.getMessage());
                ps.setString(6, n.getNotificationType().name());
                ps.setString(7, now.toString());
                ps.setString(8, now.toString());
            }

            @Override
            public int getBatchSize() {
                return buff.size();
            }
        });

        return buff.size();
    }
}
