package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTimeEntryRepository  implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        Date date = Date.valueOf(timeEntry.getDate());
        PreparedStatementCreator creator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) VALUES("
                        + timeEntry.getProjectId() + ", " + timeEntry.getUserId() + ", '" + date + "', " + timeEntry.getHours() + ");", PreparedStatement.RETURN_GENERATED_KEYS);
                return ps;
            }
        };
        jdbcTemplate.update(creator, keyHolder);
        timeEntry.setId(keyHolder.getKey().longValue());
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        String select = "SELECT * FROM time_entries WHERE id = " + id + ";";
        ResultSetExtractor<TimeEntry> timeEntry = rs -> {
            TimeEntry timeEntryToReturn = new TimeEntry();
            while(rs.next()){
                timeEntryToReturn.setId(rs.getLong("id"));
                timeEntryToReturn.setProjectId(rs.getLong("project_id"));
                timeEntryToReturn.setUserId(rs.getLong("user_id"));
                timeEntryToReturn.setHours(rs.getInt("hours"));
                timeEntryToReturn.setDate(rs.getDate("date").toLocalDate());
            }
            if(timeEntryToReturn.getId() == 0){
                return null;
            }
            return timeEntryToReturn;
        };
        TimeEntry timeEntryReturn = jdbcTemplate.query(select, timeEntry);
        return timeEntryReturn;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        Date date = Date.valueOf(timeEntry.getDate());
        PreparedStatementCreator creator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("UPDATE time_entries SET project_id = "
                        + timeEntry.getProjectId() + ", user_id = " + timeEntry.getUserId() + ", date = '" + date + "', hours = " + timeEntry.getHours() +
                        " WHERE id=" + id + ";");

                return ps;
            }
        };
        jdbcTemplate.update(creator);
        timeEntry.setId(id);
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        PreparedStatementCreator creator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("DELETE FROM time_entries where id= " + id + ";");
                return ps;
            }
        };
        jdbcTemplate.update(creator);
    }

    @Override
    public List<TimeEntry> list() {
        String select = "SELECT * FROM time_entries;";
        ResultSetExtractor<List<TimeEntry>> timeEntry = rs -> {
            List<TimeEntry> timeEntryList = new ArrayList<>();

            while(rs.next()){
                TimeEntry timeEntryToReturn = new TimeEntry();
                timeEntryToReturn.setId(rs.getLong("id"));
                timeEntryToReturn.setProjectId(rs.getLong("project_id"));
                timeEntryToReturn.setUserId(rs.getLong("user_id"));
                timeEntryToReturn.setHours(rs.getInt("hours"));
                timeEntryToReturn.setDate(rs.getDate("date").toLocalDate());
                timeEntryList.add(timeEntryToReturn);
            }
            return timeEntryList;
        };
        List<TimeEntry> timeEntryReturn = jdbcTemplate.query(select, timeEntry);
        return timeEntryReturn;
    }
}
