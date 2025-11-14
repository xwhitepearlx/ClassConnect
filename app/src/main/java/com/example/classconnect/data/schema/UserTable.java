package com.example.classconnect.data.schema;

public class UserTable {
    public static final String TABLE_NAME = "USERS";
    public static final String COL_ID = "UserID";
    public static final String COL_NAME = "Name";
    public static final String COL_EMAIL = "Email";
    public static final String COL_PASSWORD = "Password";
    public static final String COL_USER_TYPE = "UserType";
    public static final String COL_INTEREST = "Interest";
    public static final String COL_PICTURE = "PictureURL";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COL_PASSWORD + " TEXT NOT NULL, " +
                    COL_USER_TYPE + " TEXT NOT NULL, " +
                    COL_INTEREST + " TEXT, " +              // optional, can be NULL
                    COL_PICTURE + " TEXT" +                 // optional, can be NULL
                    ")";

    //Table 2 details
    public static final String TABLE2 = "";
    public static final String TS_COL_DATE = "Date&Time";
    public static final String TS_COL_DURATION = "Duration";
    public static final String TS_COL_MAXPART = "Maximum Paricipants";
    public static final String TS_COL_DESCRIPTION = "Description";
    public static final String TS_COL_LOCTION_MODE = "Location Mode";
    public static final String TS_COL_LOCATION_DETAIL = "Location";
    public static final String TS_COL_STATUS = "Status";

    public static final String CREATE_TABLE2 = "CREATE TABLE " + TABLE2
            + "(" + TS_COL_DATE + "DATE, "
            + TS_COL_DURATION + "INTEGER,"
            + TS_COL_MAXPART + "INTEGER,"
            + TS_COL_DESCRIPTION + "TEXT ,"
            + TS_COL_LOCTION_MODE + "TEXT,"
            + TS_COL_LOCATION_DETAIL + "TEXT,"
            + TS_COL_STATUS + "TEXT );";

    //table 3 details
    public  static final String TABLE_COURSE = "Course";
    public static final String TC_COL_COURSEID ="Course Id";
    public static final String TC_COL_COURSENAME = "Course Name";
    public static final String TC_COL_COURSE_CODE = "Course Code";

    public static final String CREATE_TABLE_COURSE = "CREATE TABLE " + TABLE_COURSE
            + "( " + TC_COL_COURSEID + "TEXT, "
            + TC_COL_COURSENAME + "TEXT,"
            + TC_COL_COURSE_CODE + "INTEGER );";
    //Table 4 details

    public static final String TABLE3 ="";




    //Table 5 details

    public static final String TABLE4 = "";



}
