import dao.LogEventDao;
import dao.LogEventDaoImpl;
import matcher.LogEventEntryMatcher;
import model.LogEventDatabaseEntry;
import parser.Parser;
import model.LogEvent;
import splitter.LogFileSplitter;
import util.ConnectionFactory;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;

public class Application {

    private final static Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {

        String path = null;
        if (args.length == 0) {
            System.out.println("User did not specify a path to file");
            System.exit(0);
        } else {
            path = args[0];
        }
        LOGGER.info("User input: " + path);

        LogFileSplitter logFileSplitter = new LogFileSplitter();
        List<String> fileNames = logFileSplitter.splitFileIfNecessary(path);

        for (String fileName: fileNames) {
            int matchCounter = 0, lineCounter = 0;
            Parser fileParser = null;

            try {
                fileParser = new Parser(fileName);
            } catch (FileNotFoundException e) {
                System.out.println("Could not find file for the given path: " + path);
                System.exit(0);
            }
            LogEventEntryMatcher entryMatcher = new LogEventEntryMatcher();
            LogEventDao logEventDao = new LogEventDaoImpl();

            LogEvent next;
            while ((next = fileParser.parseNextEvent() )!= null) {
                lineCounter++;
                LogEventDatabaseEntry logEntryMatch = entryMatcher.matchNewEvent(next);
                boolean foundMatchingEntry = logEntryMatch != null;

                if (foundMatchingEntry) {
                    logEventDao.save(logEntryMatch);
                    matchCounter++;
                }
            }

            ConnectionFactory.closeConnection();

            int numberOfUnmatchedEvents = entryMatcher.getNumberOfUnmatchedEvents();
            String infoMessage = String.format("Processed %d lines and found %d matches and %d unmatched events in file %s%n", lineCounter, matchCounter, numberOfUnmatchedEvents, fileName);
            System.out.print(infoMessage);
        }
    }
}