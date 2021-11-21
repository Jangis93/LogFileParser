package parser;

import model.LogEvent;
import model.LogState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.sql.Timestamp;

public class ParserTest {

    @Test
    public void shouldSuccessfullyParseFile() throws FileNotFoundException {

        // Given
        LogEvent expected = new LogEvent(
                "scsmbstgra",
                LogState.STARTED,
                Timestamp.valueOf("2017-04-05 09:31:35.212"),
                "APPLICATION_LOG",
                "12345"
        );

        String filePath = "src/test/resources/logfile-happy-case.txt";
        Parser target = new Parser(filePath);

        // When
        LogEvent actual = target.parseNextEvent();

        // Then
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getState(), actual.getState());
        Assertions.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        Assertions.assertEquals(expected.getType(), actual.getType());
        Assertions.assertEquals(expected.getHost(), actual.getHost());
    }

    @Test
    public void shouldSuccessfullyParseEmptyFile() throws FileNotFoundException {

        // Given
        String filePath = "src/test/resources/logfile-empty.txt";

        Parser target = new Parser(filePath);

        // When
        LogEvent actual = target.parseNextEvent();

        // Then
        Assertions.assertNull(actual);
    }

    @Test
    public void shouldFailToParseInvalidObjectFormat() throws FileNotFoundException {
        // Given
        String filePath = "src/test/resources/logfile-invalid-format.txt";
        Parser target = new Parser(filePath);

        // When
        LogEvent actual = target.parseNextEvent();

        // Then
        Assertions.assertNull(actual);
    }

    @Test
    public void shouldThrowExceptionDueToInvalidPath() {
        // Given
        String filePath = "not-valid-path/logfile.txt";

        // When
        Assertions.assertThrows(
                FileNotFoundException.class,
                () -> new Parser(filePath)
        );
    }
}
