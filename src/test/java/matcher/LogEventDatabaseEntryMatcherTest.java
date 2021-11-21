package matcher;

import model.LogEvent;
import model.LogEventDatabaseEntry;
import model.LogState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Timestamp;

public class LogEventDatabaseEntryMatcherTest {

    private LogEventEntryMatcher target;

    @BeforeEach
    void setup() {
        target = new LogEventEntryMatcher();
    }

    @Test
    public void successfulMatchBetweenEvents() {
        // Given
        Timestamp startTime = Timestamp.valueOf("2017-04-05 09:31:35");
        Timestamp finishTime = Timestamp.valueOf("2017-04-05 09:31:40");
        long duration = finishTime.getTime() - startTime.getTime();
        boolean alert = duration > 4;

        LogEvent firstEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                startTime,
                "APPLICATION_LOG",
                "12345"
        );

        LogEvent secondEvent = new LogEvent(
                "scsmbstgra",
                LogState.FINISHED,
                finishTime,
                "APPLICATION_LOG",
                "12345"
        );

        target.matchNewEvent(firstEvent);

        // When
        LogEventDatabaseEntry result = target.matchNewEvent(secondEvent);

        // Then
        Assertions.assertEquals(secondEvent.getId(), result.getId());
        Assertions.assertEquals(duration, result.getDuration());
        Assertions.assertEquals(secondEvent.getType(), result.getType());
        Assertions.assertEquals(secondEvent.getHost(), result.getHost());
        Assertions.assertEquals(alert, result.getAlert());
    }

    @Test
    public void twoEventsWithSameState() {
        // Given
        Timestamp startTime = Timestamp.valueOf("2017-04-05 09:31:35");
        Timestamp finishTime = Timestamp.valueOf("2017-04-05 09:31:36");

        LogEvent firstEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                startTime,
                "APPLICATION_LOG",
                "12345"
        );

        LogEvent secondEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                finishTime,
                "APPLICATION_LOG",
                "12345"
        );

        target.matchNewEvent(firstEvent);

        // When
        LogEventDatabaseEntry result = target.matchNewEvent(secondEvent);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    public void mismatchBetweenTimestampOnEvents() {
        // Given
        Timestamp startTime = Timestamp.valueOf("2017-04-05 09:31:35");
        Timestamp finishTime = Timestamp.valueOf("2017-04-05 09:31:40");

        LogEvent firstEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                finishTime,
                "APPLICATION_LOG",
                "12345"
        );

        LogEvent secondEvent = new LogEvent(
                "scsmbstgra",
                LogState.FINISHED,
                startTime,
                "APPLICATION_LOG",
                "12345"
        );

        target.matchNewEvent(firstEvent);

        // When
        LogEventDatabaseEntry result = target.matchNewEvent(secondEvent);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    public void eventOrderShouldNotMatter() {
        // Given
        Timestamp startTime = Timestamp.valueOf("2017-04-05 09:31:35");
        Timestamp finishTime = Timestamp.valueOf("2017-04-05 09:31:40");
        long duration = finishTime.getTime() - startTime.getTime();
        boolean alert = duration > 4;

        LogEvent finishEvent = new LogEvent(
                "scsmbstgra",
                LogState.FINISHED,
                finishTime,
                "APPLICATION_LOG",
                "12345"
        );

        LogEvent startEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                startTime,
                "APPLICATION_LOG",
                "12345"
        );

        target.matchNewEvent(finishEvent);

        // When
        LogEventDatabaseEntry result = target.matchNewEvent(startEvent);

        // Then
        Assertions.assertEquals(startEvent.getId(), result.getId());
        Assertions.assertEquals(duration, result.getDuration());
        Assertions.assertEquals(startEvent.getType(), result.getType());
        Assertions.assertEquals(startEvent.getHost(), result.getHost());
        Assertions.assertEquals(alert, result.getAlert());
    }

    @Test
    public void saveEntryWithNoAlert() {
        // Given
        Timestamp startTime = Timestamp.valueOf("2017-04-05 09:31:35.100");
        Timestamp finishTime = Timestamp.valueOf("2017-04-05 09:31:35.101");
        long duration = finishTime.getTime() - startTime.getTime();

        LogEvent firstEvent = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                startTime,
                "APPLICATION_LOG",
                "12345"
        );

        LogEvent secondEvent = new LogEvent(
                "scsmbstgra",
                LogState.FINISHED,
                finishTime,
                "APPLICATION_LOG",
                "12345"
        );

        target.matchNewEvent(firstEvent);

        // When
        LogEventDatabaseEntry result = target.matchNewEvent(secondEvent);

        // Then
        Assertions.assertEquals(secondEvent.getId(), result.getId());
        Assertions.assertEquals(duration, result.getDuration());
        Assertions.assertEquals(secondEvent.getType(), result.getType());
        Assertions.assertEquals(secondEvent.getHost(), result.getHost());
        Assertions.assertFalse(result.getAlert());
    }
}
