package dao;

import model.LogEventDatabaseEntry;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LogEventDaoImpl implements LogEventDao {

    private final Logger LOGGER = Logger.getLogger(LogEventDaoImpl.class.getName());

    private final Connection connection;

    public LogEventDaoImpl() {
        connection = ConnectionFactory.getConnection();
    }

    @Override
    public int save(LogEventDatabaseEntry logEventDatabaseEntry) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO LOG_EVENT VALUES(?, ?, ?, ?, ?)");
            ps.setString(1, logEventDatabaseEntry.getId());
            ps.setLong(2, logEventDatabaseEntry.getDuration());
            ps.setString(3, logEventDatabaseEntry.getType());
            ps.setString(4, logEventDatabaseEntry.getHost());
            ps.setBoolean(5, logEventDatabaseEntry.getAlert());
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warning("Failed to save entry in database due to: " + e);
            return 0;
        }
    }
}
