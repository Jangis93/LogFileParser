package util;

import model.LogEventDatabaseEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractTest {

    private final Logger LOGGER = Logger.getLogger(AbstractTest.class.getName());

    private Connection connection;

    @BeforeEach
    public void beforeEach() {
        connection = ConnectionFactory.getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE LOG_EVENT");
            LOGGER.info("Truncating table LOG_EVENT successful");
        } catch (SQLException e) {
            LOGGER.warning("Failed to save entry in database due to: " + e);
        }
    }

    @AfterEach
    public void closeConnection() throws SQLException {
        connection.close();
    }

    public List<LogEventDatabaseEntry> getAll() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM LOG_EVENT");
            ps.clearParameters();
            ResultSet rs = ps.executeQuery();
            List<LogEventDatabaseEntry> events = new ArrayList<>();
            while(rs.next()) {
                LogEventDatabaseEntry entry = new LogEventDatabaseEntry(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5)
                );
                events.add(entry);
            }
            return events;
        } catch (SQLException e) {
            LOGGER.info("Failed to fetch log entries from database due to: " + e);
            return Collections.emptyList();
        }
    }
}
