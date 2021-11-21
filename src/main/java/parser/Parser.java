package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.LogEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

// This class will enable reading from file.
// It takes care of converting the log events string values to LogEvent objects.
public class Parser {

    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    private final BufferedReader reader;

    private static ObjectMapper objMapper;

    private static ObjectMapper getMapper() {
        if (objMapper == null) {
            objMapper = new ObjectMapper();
        }
        return objMapper;
    }

    public Parser(String path) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(path));
    }

    public static <K> K fromJsonToObj(String json, Class<K> kClass) throws IOException {
        return getMapper().readValue(json, kClass);
    }

    public LogEvent parseNextEvent() {
        String nextLine;
        try {
           nextLine = reader.readLine();
        } catch (IOException e) {
            LOGGER.warning("Failed to read line from file due to: " + e.getMessage());
            return null;
        }
        if (nextLine != null) {
            try {
                return fromJsonToObj(nextLine, LogEvent.class);
            } catch (IOException e) {
                LOGGER.warning("Failed to convert line to object due to: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
}
