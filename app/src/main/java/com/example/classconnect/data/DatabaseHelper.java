package com.example.classconnect.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.classconnect.Course;
import com.example.classconnect.Session;
import com.example.classconnect.data.repositories.CourseData;
import com.example.classconnect.data.schema.UserTable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "ClassConnect.db";
    private static final String TAG = "DatabaseHelper";
    CourseData cd = new CourseData();

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.CREATE_TABLE);
        db.execSQL(UserTable.CREATE_COURSES_TABLE);
        db.execSQL(UserTable.CREATE_SESSION_TABLE);
        db.execSQL(UserTable.CREATE_STUDENT_COURSE_TABLE);
        db.execSQL(UserTable.CREATE_STUDENT_SESSION_TABLE);

        cd.insertDefaultCourses(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.STUDENT_SESSION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.STUDENT_COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.SESSION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.COURSES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
        onCreate(db);
    }

    public Cursor getUserByEmail(String Email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                UserTable.COL_ID, UserTable.COL_NAME, UserTable.COL_EMAIL,
                UserTable.COL_PASSWORD, UserTable.COL_USER_TYPE, UserTable.COL_INTEREST,
                UserTable.COL_PICTURE, UserTable.Col_Phone_No, UserTable.Col_Program
        };
        String selection = UserTable.COL_EMAIL + "= ?";
        String[] selectionArgs = { Email };

        return db.query(UserTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public boolean updateAllInfo(String email, String name, String phone, String program, String interest) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("PhoneNo", phone);
        cv.put("ProgramName", program);
        cv.put("Interest", interest);
        int result = db.update(UserTable.TABLE_NAME, cv, "Email=?", new String[]{email});
        return result > 0;
    }

    public boolean insertSession(int courseId, String date, String time, int duration, String location, int maxParticipant, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserTable.SESSION_COURSE_ID, courseId);
        cv.put(UserTable.SESSION_DATE, date);
        cv.put(UserTable.SESSION_START_TIME, time);
        cv.put(UserTable.SESSION_DURATION, duration);
        cv.put(UserTable.SESSION_LOCATION, location);
        cv.put(UserTable.SESSION_MAX_PART, maxParticipant);
        cv.put(UserTable.SESSION_DESCRIPTION, description);

        Log.d(TAG, "Inserting session for courseId: " + courseId);
        long result = db.insert(UserTable.SESSION_TABLE, null, cv);
        Log.d(TAG, "Insert result: " + result);
        return result != -1;
    }

    public List<Session> getSessionsForCourse(int courseId) {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + UserTable.SESSION_TABLE +
                " WHERE " + UserTable.SESSION_COURSE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_ID));
                int dbCourseId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_COURSE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_START_TIME));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_DURATION));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_LOCATION));
                int maxPart = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_MAX_PART));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DESCRIPTION));

                sessionList.add(new Session(id, dbCourseId, date, time, duration, location, maxPart, desc));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sessionList;
    }

    public boolean joinSession(String studentEmail, int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + UserTable.STUDENT_SESSION_TABLE +
                        " WHERE " + UserTable.SS_EMAIL + " = ? AND " + UserTable.SS_SESSION_ID + " = ?",
                new String[]{studentEmail, String.valueOf(sessionId)}
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(UserTable.SS_EMAIL, studentEmail);
        cv.put(UserTable.SS_SESSION_ID, sessionId);

        long result = db.insert(UserTable.STUDENT_SESSION_TABLE, null, cv);
        return result != -1;
    }

    public List<Session> getJoinedSessionsForCourse(String studentEmail, int courseId) {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT s.* FROM " + UserTable.SESSION_TABLE + " s " +
                        "INNER JOIN " + UserTable.STUDENT_SESSION_TABLE + " ss " +
                        "ON s." + UserTable.SESSION_ID + " = ss." + UserTable.SS_SESSION_ID + " " +
                        "WHERE ss." + UserTable.SS_EMAIL + " = ? AND s." + UserTable.SESSION_COURSE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{studentEmail, String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_ID));
                int dbCourseId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_COURSE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_START_TIME));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_DURATION));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_LOCATION));
                int maxPart = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_MAX_PART));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DESCRIPTION));

                sessionList.add(new Session(id, dbCourseId, date, time, duration, location, maxPart, desc));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sessionList;
    }

    public List<Session> getAvailableSessionsForCourse(String studentEmail, int courseId) {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT * FROM " + UserTable.SESSION_TABLE +
                        " WHERE " + UserTable.SESSION_COURSE_ID + " = ? " +
                        "AND " + UserTable.SESSION_ID + " NOT IN (" +
                        "SELECT " + UserTable.SS_SESSION_ID + " FROM " + UserTable.STUDENT_SESSION_TABLE +
                        " WHERE " + UserTable.SS_EMAIL + " = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId), studentEmail});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_ID));
                int dbCourseId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_COURSE_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_START_TIME));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_DURATION));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_LOCATION));
                int maxPart = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SESSION_MAX_PART));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.SESSION_DESCRIPTION));

                sessionList.add(new Session(id, dbCourseId, date, time, duration, location, maxPart, desc));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sessionList;
    }

    public int getSessionParticipantCount(int sessionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + UserTable.STUDENT_SESSION_TABLE +
                        " WHERE " + UserTable.SS_SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionId)}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<String> getSessionParticipants(int sessionId) {
        List<String> participants = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT u." + UserTable.COL_NAME + ", u." + UserTable.COL_EMAIL +
                        " FROM " + UserTable.TABLE_NAME + " u " +
                        "INNER JOIN " + UserTable.STUDENT_SESSION_TABLE + " ss " +
                        "ON u." + UserTable.COL_EMAIL + " = ss." + UserTable.SS_EMAIL + " " +
                        "WHERE ss." + UserTable.SS_SESSION_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String email = cursor.getString(1);
                participants.add(name + " (" + email + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return participants;
    }

    public boolean deleteSession(int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UserTable.STUDENT_SESSION_TABLE,
                UserTable.SS_SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionId)});
        int result = db.delete(UserTable.SESSION_TABLE,
                UserTable.SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionId)});
        return result > 0;
    }

    // NEW: Drop/Leave a session (student unenrolls)
    public boolean dropSession(String studentEmail, int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                UserTable.STUDENT_SESSION_TABLE,
                UserTable.SS_EMAIL + " = ? AND " + UserTable.SS_SESSION_ID + " = ?",
                new String[]{studentEmail, String.valueOf(sessionId)}
        );

        Log.d(TAG, "Drop session result: " + result);
        return result > 0;
    }

    public boolean changePassword(String email, String oldPass, String newPass) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Password FROM " + UserTable.TABLE_NAME + " WHERE Email=?",
                new String[]{email}
        );
        if (cursor != null && cursor.moveToFirst()) {
            String dbPassword = cursor.getString(0);
            cursor.close();
            if (!dbPassword.equals(oldPass)) {
                return false;
            }
            ContentValues cv = new ContentValues();
            cv.put("Password", newPass);
            int result = db.update(UserTable.TABLE_NAME, cv, "Email=?", new String[]{email});
            return result > 0;
        }
        return false;
    }

    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, csis, code, name FROM Courses", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String csis = cursor.getString(1);
                int code = cursor.getInt(2);
                String name = cursor.getString(3);
                courseList.add(new Course(id, csis, code, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseList;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = null;
        Cursor cursor = db.rawQuery(
                "SELECT " + UserTable.COL_USER_TYPE + " FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COL_EMAIL + " = ?",
                new String[]{email}
        );
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }

    public void insertStudentCourse(String email, int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserTable.SC_EMAIL, email);
        cv.put(UserTable.SC_COURSE_ID, courseId);
        db.insert(UserTable.STUDENT_COURSE_TABLE, null, cv);
    }

    public List<Course> getRemainingCourses(String email) {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT * FROM " + UserTable.COURSES_TABLE +
                        " WHERE " + UserTable.COL_COURSE_ID + " NOT IN (" +
                        "SELECT " + UserTable.SC_COURSE_ID +
                        " FROM " + UserTable.STUDENT_COURSE_TABLE +
                        " WHERE " + UserTable.SC_EMAIL + " = ?" +
                        ")";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_COURSE_ID));
                String csis = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_CSIS));
                int code = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_CODE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_COURSE_NAME));
                list.add(new Course(id, csis, code, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Course> getJoinedCourses(String email) {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT c.* FROM " + UserTable.COURSES_TABLE + " c " +
                        "INNER JOIN " + UserTable.STUDENT_COURSE_TABLE + " sc " +
                        "ON c." + UserTable.COL_COURSE_ID + " = sc." + UserTable.SC_COURSE_ID +
                        " WHERE sc." + UserTable.SC_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_COURSE_ID));
                String csis = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_CSIS));
                int code = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.COL_CODE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_COURSE_NAME));
                list.add(new Course(id, csis, code, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean insertCourse(String csis, int code, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_CSIS, csis);
        values.put(UserTable.COL_CODE, code);
        values.put(UserTable.COL_COURSE_NAME, name);
        long result = db.insert(UserTable.COURSES_TABLE, null, values);
        return result != -1;
    }
}