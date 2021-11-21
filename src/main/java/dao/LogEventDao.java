package dao;

import model.LogEventDatabaseEntry;

public interface LogEventDao {

    int save(LogEventDatabaseEntry logEventDatabaseEntry);
}
