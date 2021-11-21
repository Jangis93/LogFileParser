package model;


import org.jetbrains.annotations.Nullable;

// Class to create database objects
public class LogEventDatabaseEntry {
    private String id;
    @Nullable
    private String type;
    @Nullable
    private String host;
    private final long duration;
    private final boolean alert;

    public LogEventDatabaseEntry(LogEvent logEvent, long duration, boolean alert) {
        this.id = logEvent.getId();
        this.type = logEvent.getType();
        this.host = logEvent.getHost();
        this.duration = duration;
        this.alert = alert;
    }

    public LogEventDatabaseEntry(String id, long duration, @Nullable String type, @Nullable String host, boolean alert) {
        this.id = id;
        this.type = type;
        this.host = host;
        this.duration = duration;
        this.alert = alert;
    }

    public long getDuration() {
        return duration;
    }

    public boolean getAlert() {
        return alert;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }
}
