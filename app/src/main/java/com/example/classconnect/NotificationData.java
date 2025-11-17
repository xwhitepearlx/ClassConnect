package com.example.classconnect;

public class NotificationData {
    String SessionID, ScheduleTime;

    public NotificationData(String scheduleTime, String sessionID) {
        ScheduleTime = scheduleTime;
        SessionID = sessionID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public String getScheduleTime() {
        return ScheduleTime;
    }
}
