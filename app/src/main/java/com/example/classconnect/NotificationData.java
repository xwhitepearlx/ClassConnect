package com.example.classconnect;

public class NotificationData {
    private String sessionID;
    private String scheduleTime;
    private String message;
    private String timestamp;

    public NotificationData(String sessionID, String scheduleTime, String message, String timestamp) {
        this.sessionID = sessionID;
        this.scheduleTime = scheduleTime;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}