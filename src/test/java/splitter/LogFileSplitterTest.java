package splitter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogFileSplitterTest {

    private LogFileSplitter target;

    @BeforeEach
    void setup() {
        target = new LogFileSplitter();
    }

    @Test
    public void generateTemporaryFiles() {
        // Given
        String largeFilePath = generateLogFile(100_000_000);

        // When
        List<String> result = target.splitFileIfNecessary(largeFilePath);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.size() > 1);
    }

    @Test
    public void smallFileShouldNotGenerateIntermediateFiles() {
        // Given
        String smallLogFile = generateLogFile(3);

        // When
        List<String> result = target.splitFileIfNecessary(smallLogFile);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(smallLogFile, result.get(0));
    }

    private String generateLogFile(int numOfRows) {
        String filePath = "src/test/resources/generated";
        String fileName = "logfile_"  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"))+ ".txt";

        File newFile = new File(filePath, fileName);
        try {
            newFile.createNewFile();
            BufferedWriter buffer = new BufferedWriter(new FileWriter(filePath + "/"+ fileName, true));

            for (int i = 0; i <= numOfRows; i++) {
                String startEvent = "{\"id\": \"scsmbstgra" + i + "\" , \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
                String finishEvent = "{\"id\": \"scsmbstgra" + i + "\" , \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495217}";

                buffer.append(startEvent);
                buffer.newLine();
                buffer.append(finishEvent);
                buffer.newLine();
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath + "/" + fileName;
    }

    /*
    Improvements:
    Add test cases:
    1. events with same id is put in the same file
    2. files will get an even distribution of log events
    3. files names are correctly generated to ensure we don't end up with multiple files with same name (would not be a problem with temp files)
    4. verify that files is not larger than allowed
     */

}
