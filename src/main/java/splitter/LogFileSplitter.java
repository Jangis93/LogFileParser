package splitter;

import model.LogEvent;
import parser.Parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


// Some files are large enough to overflow memory. This class will split such files into smaller ones.
// For correctness, it is vital that all log pairs are sorted into the same files. This is ensured by
// determining file based on log-id hash.
public class LogFileSplitter {

    private final Logger LOGGER = Logger.getLogger(LogFileSplitter.class.getName());

    // Returns the list of filenames given pathToFile was split into
    public List<String> splitFileIfNecessary(String pathToFile) {

        // this number was chosen arbitrarily, an obvious improvement would be to decide this dynamically based on free heap memory
        long maxFileSize = 6000000000L;

        File originalFile = new File(pathToFile);
        boolean shouldSplitFile = originalFile.length() > maxFileSize;
        if (shouldSplitFile) {
            int numberOfTempFiles = (int) Math.ceil((double) originalFile.length() / maxFileSize);
            List<String> tmpFileNames = generateFileNames(numberOfTempFiles);

            LOGGER.info(String.format("Splitting %s into %d files", pathToFile, numberOfTempFiles));
            splitFile(pathToFile, tmpFileNames);
            return tmpFileNames;
        } else {
            LOGGER.info(String.format("File %s small enough to not required any splitting", pathToFile));
            return Collections.singletonList(pathToFile);
        }
    }

    private List<String> generateFileNames(int numberOfFiles) {
        String basePath = "src/main/resources/temporary/";

        List<String> filePaths = new ArrayList<>();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
        for (int i = 0; i < numberOfFiles; i++) {
            String newFileName = basePath + "temporary_" + i + "_" + currentTime + ".txt";
            filePaths.add(newFileName);
        }
        return filePaths;
    }

    private void splitFile(String originalFileName, List<String> fileNames) {
        List<BufferedWriter> writers = createFiles(fileNames);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(originalFileName));

            String line;
            while ((line = reader.readLine()) != null) {
                LogEvent event = Parser.fromJsonToObj(line, LogEvent.class);
                int fileIndex = getFileIndexByHashcode(event.getId(), fileNames.size());
                BufferedWriter writer = writers.get(fileIndex);
                writer.append(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.warning("Error encountered while splitting file: " + e);
        } finally {
            cleanUp(writers);
        }
    }

    // Use the property of uniform distribution of hashcode to divide the events into even files and
    // ensure that events with same ids are put in the same file
    private int getFileIndexByHashcode(String id, int numberOfFiles) {
        return Math.floorMod(id.hashCode(), numberOfFiles);
    }

    private List<BufferedWriter> createFiles(List<String> fileNames) {

        List<BufferedWriter> fileWriters = new ArrayList<>();

        for(String fileName: fileNames) {
            File newFile = new File(fileName);
            BufferedWriter buffer = null;
            try {
                newFile.createNewFile(); // improvement: create temporary files instead since we don't care about these intermediate files
                buffer = new BufferedWriter(new FileWriter(fileName, true));
            } catch (IOException e) {
                LOGGER.warning("Something went wrong when creating file: " + e);
            }
            fileWriters.add(buffer);
        }

        return fileWriters;
    }

    private void cleanUp(List<BufferedWriter> writers) {
        for (BufferedWriter writer: writers) {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.warning("Error encountered when trying to to free up system resources: " + e);
            }
        }
    }
}
