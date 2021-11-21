package dao;

import util.AbstractTest;
import model.LogEvent;
import model.LogEventDatabaseEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static model.LogState.STARTED;

public class LogEventDaoTest extends AbstractTest {

    private LogEventDaoImpl logEventDao;

    @BeforeEach
    void setUp() {
        logEventDao = new LogEventDaoImpl();
    }

    @Test
    public void storeLogEvent() {
        // Given
        LogEvent logEvent = new LogEvent(
                "id1",
                STARTED,
                Timestamp.valueOf(LocalDateTime.now()),
                "TYPE",
                "HOST"
        );

        Timestamp.valueOf(LocalDateTime.now());

        LogEventDatabaseEntry entry = new LogEventDatabaseEntry(logEvent, 5, true);

        // When
        int rows = logEventDao.save(entry);

        // Then
        List<LogEventDatabaseEntry> entries = getAll();
        LogEventDatabaseEntry savedEntry = entries.get(0);

        Assertions.assertEquals(1, rows);
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals(entry.getId(), savedEntry.getId());
        Assertions.assertEquals(entry.getDuration(), savedEntry.getDuration());
        Assertions.assertEquals(entry.getType(), savedEntry.getType());
        Assertions.assertEquals(entry.getHost(), savedEntry.getHost());
        Assertions.assertEquals(entry.getAlert(), savedEntry.getAlert());
    }
}
