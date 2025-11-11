package com.example.classconnect.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.classconnect.data.DatabaseHelper;
import com.example.classconnect.data.schema.UserTable;

public class UserRepository {
    private final DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertUser(String name, String email, String password, String userType, String interest, String pictureUrl) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_NAME, name);
        values.put(UserTable.COL_EMAIL, email);
        values.put(UserTable.COL_PASSWORD, password);
        values.put(UserTable.COL_USER_TYPE, userType);
        values.put(UserTable.COL_INTEREST, interest);

        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            values.put(UserTable.COL_PICTURE, pictureUrl);
        }

        long id = db.insert(UserTable.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT 1 FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COL_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor.moveToFirst(); // true if any row is returned
        cursor.close();
        db.close();
        return exists;
    }
}
