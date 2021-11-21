package matcher;

import model.LogEventDatabaseEntry;
import model.LogEvent;
import javafx.util.Pair;
import model.LogState;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// This class will keep track on log events and try to match them together.
public class LogEventEntryMatcher {

    private final Logger LOGGER = Logger.getLogger(LogEventEntryMatcher.class.getName());

    // Saving the log state is not strictly necessary, but it is small and helps in error detection/logging
    private final Map<String, Pair<LogState, Timestamp>> logEvents = new HashMap<>();

    // After matching two events this method will return a database entry object ready to be stored in database
    public LogEventDatabaseEntry matchNewEvent(LogEvent logEvent) {
        Pair<LogState, Timestamp> previousParsedEvent = logEvents.get(logEvent.getId());

        if (previousParsedEvent != null) {

            if (eventIsValid(logEvent, previousParsedEvent)) {
                logEvents.remove(logEvent.getId());
                return createEntry(logEvent, previousParsedEvent);
            } else {
                LOGGER.warning(createInvalidStringMessage(logEvent, previousParsedEvent));
            }
        } else {
            logEvents.put(logEvent.getId(), new Pair<>(logEvent.getState(), logEvent.getTimestamp()));
        }

        return null;
    }

    // An event that are still in the map are considered to be unmatched
    public int getNumberOfUnmatchedEvents() {
        return logEvents.size();
    }

    // With more information of log events, one could increase the validation on other fields
    private boolean eventIsValid(LogEvent logEvent, Pair<LogState, Timestamp> match) {
        if (logEvent.getState() == match.getKey()) {
            return false;
        }
        if (logEvent.getState() == LogState.STARTED) {
            return logEvent.getTimestamp().before(match.getValue());
        } else {
            return logEvent.getTimestamp().after(match.getValue());
        }
    }

    private LogEventDatabaseEntry createEntry(LogEvent logEvent, Pair<LogState, Timestamp> match) {
        long duration = calculateDuration(logEvent, match);
        boolean alert = duration > 4;
        return new LogEventDatabaseEntry(logEvent, duration, alert);
    }

    private long calculateDuration(LogEvent logEvent, Pair<LogState, Timestamp> match) {
        return Math.abs(logEvent.getTimestamp().getTime() - match.getValue().getTime());
    }

    private String createInvalidStringMessage(LogEvent logEvent, Pair<LogState, Timestamp> match) {
        return "Found invalid log events: " + "Id: " + logEvent.getId() + " First event: (" + match.getKey() +
                ", " + match.getValue() + ") Second event: (" + logEvent.getState() + ", " + logEvent.getTimestamp() +")";
    }
}