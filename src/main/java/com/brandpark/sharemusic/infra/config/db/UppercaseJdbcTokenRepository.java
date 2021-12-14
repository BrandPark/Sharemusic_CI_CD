package com.brandpark.sharemusic.infra.config.db;

import org.springframework.core.log.LogMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/** Copy And Modified from JdbcTokenRepositoryImpl */
public class UppercaseJdbcTokenRepository extends JdbcDaoSupport implements PersistentTokenRepository {

    /** Default SQL for creating the database table to store the tokens */
    public static final String CREATE_TABLE_SQL = "CREATE TABLE PERSISTENT_LOGINS (USERNAME VARCHAR(64) NOT NULL, SERIES VARCHAR(64) PRIMARY KEY, "
            + "TOKEN VARCHAR(64) NOT NULL, LAST_USED TIMESTAMP NOT NULL)";

    /** The default SQL used by the <tt>getTokenBySeries</tt> query */
    public static final String DEF_TOKEN_BY_SERIES_SQL = "SELECT USERNAME,SERIES,TOKEN,LAST_USED FROM PERSISTENT_LOGINS WHERE SERIES = ?";

    /** The default SQL used by <tt>createNewToken</tt> */
    public static final String DEF_INSERT_TOKEN_SQL = "INSERT INTO PERSISTENT_LOGINS (USERNAME, SERIES, TOKEN, LAST_USED) VALUES(?,?,?,?)";

    /** The default SQL used by <tt>updateToken</tt> */
    public static final String DEF_UPDATE_TOKEN_SQL = "UPDATE PERSISTENT_LOGINS SET TOKEN = ?, LAST_USED = ? WHERE SERIES = ?";

    /** The default SQL used by <tt>removeUserTokens</tt> */
    public static final String DEF_REMOVE_USER_TOKENS_SQL = "DELETE FROM PERSISTENT_LOGINS WHERE USERNAME = ?";

    private String tokensBySeriesSql = DEF_TOKEN_BY_SERIES_SQL;

    private String insertTokenSql = DEF_INSERT_TOKEN_SQL;

    private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;

    private String removeUserTokensSql = DEF_REMOVE_USER_TOKENS_SQL;

    private boolean createTableOnStartup;

    @Override
    protected void initDao() {
        if (this.createTableOnStartup) {
            getJdbcTemplate().execute(CREATE_TABLE_SQL);
        }
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        getJdbcTemplate().update(this.insertTokenSql, token.getUsername(), token.getSeries(), token.getTokenValue(),
                token.getDate());
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        getJdbcTemplate().update(this.updateTokenSql, tokenValue, lastUsed, series);
    }

    /**
     * Loads the token data for the supplied series identifier.
     *
     * If an error occurs, it will be reported and null will be returned (since the result
     * should just be a failed persistent login).
     * @param seriesId
     * @return the token matching the series, or null if no match found or an exception
     * occurred.
     */
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return getJdbcTemplate().queryForObject(this.tokensBySeriesSql, this::createRememberMeToken, seriesId);
        }
        catch (EmptyResultDataAccessException ex) {
            this.logger.debug(LogMessage.format("Querying token for series '%s' returned no results.", seriesId), ex);
        }
        catch (IncorrectResultSizeDataAccessException ex) {
            this.logger.error(LogMessage.format(
                    "Querying token for series '%s' returned more than one value. Series" + " should be unique",
                    seriesId));
        }
        catch (DataAccessException ex) {
            this.logger.error("Failed to load token for series " + seriesId, ex);
        }
        return null;
    }

    private PersistentRememberMeToken createRememberMeToken(ResultSet rs, int rowNum) throws SQLException {
        return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
    }

    @Override
    public void removeUserTokens(String username) {
        getJdbcTemplate().update(this.removeUserTokensSql, username);
    }

    /**
     * Intended for convenience in debugging. Will create the persistent_tokens database
     * table when the class is initialized during the initDao method.
     * @param createTableOnStartup set to true to execute the
     */
    public void setCreateTableOnStartup(boolean createTableOnStartup) {
        this.createTableOnStartup = createTableOnStartup;
    }
}
