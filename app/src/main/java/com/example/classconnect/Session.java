package com.example.classconnect;

public class Session {
    private int id;
    private int courseId;
    private String date;
    private String startTime;
    private int duration;
    private String location;
    private int maxParticipant;
    private String description;

    public Session(int id, int courseId, String date, String startTime, int duration,
                   String location, int maxParticipant, String description) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.location = location;
        this.maxParticipant = maxParticipant;
        this.description = description;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public int getDuration() { return duration; }
    public String getLocation() { return location; }
    public int getMaxParticipant() { return maxParticipant; }
    public String getDescription() { return description; }

    public void setId(int id) { this.id = id; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setLocation(String location) { this.location = location; }
    public void setMaxParticipant(int maxParticipant) { this.maxParticipant = maxParticipant; }
    public void setDescription(String description) { this.description = description; }
}