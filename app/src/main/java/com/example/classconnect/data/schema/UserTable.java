package com.example.classconnect.data.schema;

public class UserTable {
    public static final String TABLE_NAME = "USERS";
    public static final String COL_ID = "UserID";
    public static final String COL_NAME = "Name";
    public static final String COL_CUSTOM_ID = "CustomID";
    public static final String COL_EMAIL = "Email";
    public static final String COL_PASSWORD = "Password";
    public static final String COL_USER_TYPE = "UserType";
    public static final String COL_INTEREST = "Interest";
    public static final String COL_PICTURE = "PictureURL";

    public static final String Col_Program = "ProgramName";
    public static final String Col_Phone_No = "PhoneNo";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CUSTOM_ID + " TEXT NOT NULL UNIQUE, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COL_PASSWORD + " TEXT NOT NULL, " +
                    COL_USER_TYPE + " TEXT NOT NULL, " +
                    COL_INTEREST + " TEXT, " +
                    COL_PICTURE + " TEXT, " +
                    Col_Phone_No + " TEXT DEFAULT '(000)000-0000', " +
                    Col_Program + " TEXT DEFAULT 'NOT SELECTED'" +
                    ");";


    //Table 2 - COURSES

    public static final String COURSES_TABLE = "Courses";
    public static final String COL_COURSE_ID = "id";
    public static final String COL_CSIS = "csis";
    public static final String COL_CODE = "code";
    public static final String COL_COURSE_NAME = "name";

    public static final String CREATE_COURSES_TABLE =
            "CREATE TABLE " + COURSES_TABLE + " (" +
                    COL_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CSIS + " TEXT, " +
                    COL_CODE + " INTEGER, " +
                    COL_COURSE_NAME + " TEXT);";


    //Table 3 - SESSION TABLE

    public static final String SESSION_TABLE = "SessionTable";

    public static final String SESSION_ID = "SessionID";
    public static final String SESSION_COURSE_ID = "CourseID";
    public static final String SESSION_DATE = "Date";
    public static final String SESSION_START_TIME = "StartTime";
    public static final String SESSION_DURATION = "Duration";
    public static final String SESSION_LOCATION = "Location";
    public static final String SESSION_MAX_PART = "MaxParticipant";
    public static final String SESSION_DESCRIPTION = "Description";

    public static final String CREATE_SESSION_TABLE =
            "CREATE TABLE " + SESSION_TABLE + " (" +
                    SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SESSION_COURSE_ID + " INTEGER NOT NULL, " +
                    SESSION_DATE + " TEXT NOT NULL, " +
                    SESSION_START_TIME + " TEXT NOT NULL, " +
                    SESSION_DURATION + " INTEGER NOT NULL, " +
                    SESSION_LOCATION + " TEXT NOT NULL, " +
                    SESSION_MAX_PART + " INTEGER NOT NULL, " +
                    SESSION_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY(" + SESSION_COURSE_ID + ") REFERENCES " +
                    COURSES_TABLE + "(" + COL_COURSE_ID + ") ON DELETE CASCADE" +
                    ");";


    //Table 4 - STUDENT_COURSE

    public static final String STUDENT_COURSE_TABLE = "StudentCourse";

    public static final String SC_ID = "ID";
    public static final String SC_EMAIL = "StudentEmail";
    public static final String SC_COURSE_ID = "CourseID";

    public static final String CREATE_STUDENT_COURSE_TABLE =
            "CREATE TABLE " + STUDENT_COURSE_TABLE + " (" +
                    SC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SC_EMAIL + " TEXT NOT NULL, " +
                    SC_COURSE_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + SC_EMAIL + ") REFERENCES " + TABLE_NAME + "(" + COL_EMAIL + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + SC_COURSE_ID + ") REFERENCES " + COURSES_TABLE + "(" + COL_COURSE_ID + ") ON DELETE CASCADE" +
                    ");";

    //Table 5 - STUDENT_SESSION

    public static final String STUDENT_SESSION_TABLE = "StudentSession";

    public static final String SS_ID = "ID";
    public static final String SS_EMAIL = "StudentEmail";
    public static final String SS_SESSION_ID = "SessionID";

    public static final String CREATE_STUDENT_SESSION_TABLE =
            "CREATE TABLE " + STUDENT_SESSION_TABLE + " (" +
                    SS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SS_EMAIL + " TEXT NOT NULL, " +
                    SS_SESSION_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + SS_EMAIL + ") REFERENCES " + TABLE_NAME + "(" + COL_EMAIL + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + SS_SESSION_ID + ") REFERENCES " + SESSION_TABLE + "(" + SESSION_ID + ") ON DELETE CASCADE" +
                    ");";

}