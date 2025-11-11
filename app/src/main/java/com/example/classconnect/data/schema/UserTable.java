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
}
