package com.brandpark.sharemusic.modules.album.domain.repoImpl;

import com.brandpark.sharemusic.modules.album.domain.ExtendTrackRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TrackRepositoryImpl implements ExtendTrackRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${batchSize}")
    private final int batchSize;

    @Override
    public int batchInsert(List<Track> tracks) {
        int insertCount = 0;

        List<Track> subTracks = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            subTracks.add(tracks.get(i));
            if ((i + 1) % batchSize == 0) {
                insertCount += batchInsertImpl(subTracks);
            }
        }
        if (!subTracks.isEmpty()) {
            insertCount += batchInsertImpl(subTracks);
        }

        return insertCount;
    }

    @Override
    public int batchUpdate(List<Track> tracks) {
        int updateCount = 0;

        List<Track> subTracks = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            subTracks.add(tracks.get(i));
            if ((i + 1) % batchSize == 0) {
                updateCount += batchUpdateImpl(subTracks);
            }
        }
        if (!subTracks.isEmpty()) {
            updateCount += batchUpdateImpl(subTracks);
        }

        return updateCount;
    }in mysql

    private int batchUpdateImpl(List<Track> subTracks) {

        int batchedSize = subTracks.size();

        jdbcTemplate.batchUpdate("UPDATE TRACK SET NAME=?")
        return 0;
    }

    private int batchInsertImpl(List<Track> subTracks) {

        int batchedSize = subTracks.size();

        jdbcTemplate.batchUpdate("INSERT INTO TRACK('NAME', 'ARTIST') VALUES(?, ?)"
                , new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, subTracks.get(i).getName());
                        ps.setString(2, subTracks.get(i).getArtist());
                    }

                    @Override
                    public int getBatchSize() {
                        return batchedSize;
                    }
                });

        subTracks.clear();

        return batchedSize;
    }
}
