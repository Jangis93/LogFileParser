package model;


import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

public class LogEvent {

    private String id;
    private LogState state;
    private Timestamp timestamp;

    @Nullable
    private String type;
    @Nullable
    private String host;

    public LogEvent() {} // needed for object mapper to map up log lines to objects

    public LogEvent(String id, LogState state, Timestamp time, @Nullable String type, @Nullable String host) {
        this.id = id;
        this.state = state;
        this.timestamp = time;
        this.type = type;
        this.host = host;
    }

    public String getId() { return id; }

    public LogState getState() { return state; }

    public Timestamp getTimestamp() { return timestamp; }

    @Nullable
    public String getType() { return type; }

    @Nullable
    public String getHost() { return host; }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(LogState state) {
        this.state = state;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    public void setHost(@Nullable String host) {
        this.host = host;
    }
}