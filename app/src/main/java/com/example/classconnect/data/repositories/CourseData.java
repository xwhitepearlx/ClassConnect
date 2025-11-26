package com.example.classconnect.data.repositories;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.classconnect.data.schema.UserTable;

public class CourseData {
    public static final String[][] COURSES = {
            {"CSIS", "1175", "Introduction to Programming I"},
            {"CSIS", "1280", "Multimedia Web Development"},
            {"CSIS", "2175", "Object-Oriented Software Development"},
            {"CSIS", "2270", "Systems Analysis & Design"},
            {"CSIS", "2280", "Operating Systems"},
            {"CSIS", "2271", "Virtualization and Computer Networking"},
            {"CSIS", "2300", "Database I"},
            {"CSIS", "3175", "Introduction to Mobile Application Development"},
            {"CSIS", "3275", "Software Engineering"},
            {"CSIS", "3155", "IT Security Fundamentals"},
            {"CSIS", "3160", "Evidence Imaging"},
            {"CSIS", "3280", "Backend Web Development"},
            {"CSIS", "3380", "Full Stack Development"},
            {"CSIS", "3456", "Data Structures and Algorithms"},
            {"CSIS", "4175", "Mobile Application Development II"},
            {"CSIS", "4270", "Cloud Infrastructure"},
            {"CSIS", "4280", "Special Topics in Emerging Technology"},
            {"CSIS", "4495", "Applied Research Project"},
            {"CSIS", "3560", "Scripting for CyberSecurity"},
            {"CSIS", "4460", "Mobile CyberSecurity"}
    };

    public void insertDefaultCourses(SQLiteDatabase db) {
        for (String[] course : CourseData.COURSES) {
            ContentValues cv = new ContentValues();
            cv.put(UserTable.COL_CSIS, course[0]);
            cv.put(UserTable.COL_CODE, Integer.parseInt(course[1]));
            cv.put(UserTable.COL_COURSE_NAME, course[2]);
            db.insert(UserTable.COURSES_TABLE, null, cv);
        }
    }
}
