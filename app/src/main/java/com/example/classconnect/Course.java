package com.example.classconnect;

public class Course {
    private int id;
    private String csis;
    private int code;
    private String name;

    public Course(int id, String csis, int code, String name) {
        this.id = id;
        this.csis = csis;
        this.code = code;
        this.name = name;
    }

    public int getId() { return id; }
    public String getCsis() { return csis; }
    public int getCode() { return code; }
    public String getName() { return name; }
}
