# LogFileParser 

Small program developed with Java 8 and maven and is used to parse and process certain type of log files. 

The program expects to get log files with json entries in the following format:

```
{"id": "scsmbstgra", "state":"STARTED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":1491377495212}
{"id": "scsmbstgrb", "state":"STARTED", "timestamp":1491377495213}
{"id": "scsmbstgrc", "state":"FINISHED", "timestamp":1491377495218}
{"id": "scsmbstgra", "state":"FINISHED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":1491377495217}
{"id": "scsmbstgrc", "state":"STARTED", "timestamp":1491377495210}
{"id": "scsmbstgrb", "state":"FINISHED", "timestamp":1491377495216}
```

The fields type and host are not mandatory. The program should process log eevents from the file and match the start events and finish events together, calculate the duration between them and store 
the following data into a database:
* Id
* Duration
* Type (if applicable)
* Host (if applicable)
* Alert (true if the event took longar than 4ms, otherwise false)

There is no specific order between the events meaning that a finish event could come before a start event. 

## Database
The database used was a file-based HSQLDB, see http://hsqldb.org/. 
When running the program the database will be created in the folder /db and contain among other things a log file 

## How to build
The program can be built with maven: 
* `mvn clean install` (without tests)
* `mvn clean install -DskipTests=true` (with tests)

## How to run
The program can be run with maven:  
`mvn exec:java -Dexec.mainclass=Application -Dexec.args=$YourLogFile.txt`  
A small example file can be found in `src\main\resources\logfile.txt`

## Tests
Run the test by:  
`mvn test`  
Warning! LogFileSplitterTest generates a large logfile and therefore the tests run slowly. An improvement as commented in
the code would be to use temporary files to reduce the build process when multiple or large files has been generated. 