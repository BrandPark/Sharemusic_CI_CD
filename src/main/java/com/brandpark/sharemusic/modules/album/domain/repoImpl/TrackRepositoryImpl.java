package com.brandpark.sharemusic.modules.album.domain.repoImpl;

import com.brandpark.sharemusic.modules.album.domain.ExtendTrackRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
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
public class TrackRepositoryImpl implements ExtendTrackRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${batchSize}")
    private int batchSize;

    @Override
    public int batchInsert(List<Track> tracks, Long albumId) {

        int insertCount = 0;

        List<Track> buff = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            buff.add(tracks.get(i));
            if ((i + 1) % batchSize == 0) {
                insertCount += flushInsert(buff, albumId);
                buff.clear();
            }
        }
        if (!buff.isEmpty()) {
            insertCount += flushInsert(buff, albumId);
        }

        return insertCount;
    }

    @Override
    public int batchUpdate(List<Track> tracks) {
        int updateCount = 0;

        List<Track> buff = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            buff.add(tracks.get(i));
            if ((i + 1) % batchSize == 0) {
                updateCount += flushUpdate(buff);
                buff.clear();
            }
        }
        if (!buff.isEmpty()) {
            updateCount += flushUpdate(buff);
        }

         return updateCount;
    }

    @Override
    public int batchDelete(List<Track> tracks) {
        int removeCount = 0;

        List<Track> buff = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            buff.add(tracks.get(i));
            if ((i + 1) % batchSize == 0) {
                removeCount += flushDelete(buff);
                buff.clear();
            }
        }
        if (!buff.isEmpty()) {
            removeCount += flushDelete(buff);
        }
        return removeCount;
    }

    private int flushDelete(List<Track> buff) {
        int buffSize = buff.size();

        jdbcTemplate.batchUpdate("DELETE FROM TRACK WHERE TRACK_ID=?"
                , new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, buff.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return buffSize;
                    }
                });

        return buffSize;
    }

    private int flushUpdate(List<Track> buff) {

        int buffSize = buff.size();

        jdbcTemplate.batchUpdate("UPDATE TRACK SET TRACK_NAME=?, ARTIST=? WHERE TRACK_ID=?"
                , new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, buff.get(i).getName());
                        ps.setString(2, buff.get(i).getArtist());
                        ps.setLong(3, buff.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return buffSize;
                    }
                });

        return buffSize;
    }

    private int flushInsert(List<Track> buff, Long albumId) {

        int buffSize = buff.size();

        jdbcTemplate.batchUpdate("INSERT INTO TRACK(TRACK_NAME, ARTIST, ALBUM_ID, CREATED_DATE, MODIFIED_DATE) VALUES(?, ?, ?, ?, ?)"
                , new BatchPreparedStatementSetter() {

                    LocalDateTime now = LocalDateTime.now();

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, buff.get(i).getName());
                        ps.setString(2, buff.get(i).getArtist());
                        ps.setLong(3, albumId);
                        ps.setString(4, now.toString());
                        ps.setString(5, now.toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return buffSize;
                    }
                });

        return buffSize;
    }
}
