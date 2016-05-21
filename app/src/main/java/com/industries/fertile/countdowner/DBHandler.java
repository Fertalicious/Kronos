package com.industries.fertile.countdowner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 5/17/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "DATES_DB";
    // Dates table name
    private static final String TABLE_DATES = "DATES";
    // Dates Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_DATETIME = "DATETIME";
    private static final String KEY_REPEAT = "REPEAT";
    private static final String KEY_FAVORITE = "FAVORITE";
    private static final String KEY_BACKGROUND = "BACKGROUND";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATES_TABLE = "CREATE TABLE " + TABLE_DATES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATETIME + " TEXT,"
                + KEY_REPEAT + " INTEGER,"
                + KEY_FAVORITE + " INTEGER,"
                + KEY_BACKGROUND + " TEXT"
                + ")";
        db.execSQL(CREATE_DATES_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATES);
        // Creating tables again
        onCreate(db);
    }

    // Adding new date
    public void addCountdownDate(CountdownDate date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, date.getTitle());
        values.put(KEY_DATETIME, date.getDateTime());
        values.put(KEY_REPEAT, date.getRepeat());
        values.put(KEY_FAVORITE, date.getFavorite());
        values.put(KEY_BACKGROUND, date.getBackground());

        // Inserting Row
        db.insert(TABLE_DATES, null, values);
        db.close(); // Closing database connection
    }

    // Getting one shop
    public CountdownDate getCountdownDate(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DATES, new String[]{KEY_ID,
                        KEY_TITLE, KEY_DATETIME, KEY_REPEAT, KEY_FAVORITE, KEY_BACKGROUND}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        CountdownDate date = new CountdownDate(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), cursor.getString(5));
        // return date
        return date;
    }
    // Getting All Dates
    public List<CountdownDate> getAllDates() {
        List<CountdownDate> datesList = new ArrayList<CountdownDate>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_DATES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CountdownDate date = new CountdownDate();
                date.setId(Integer.parseInt(cursor.getString(0)));
                date.setTitle(cursor.getString(1));
                date.setDateTime(cursor.getString(2));
                date.setRepeat(Integer.parseInt(cursor.getString(3)));
                date.setFavorite(Integer.parseInt(cursor.getString(4)));
                date.setBackground(cursor.getString(5));

                // Adding dates to list
                datesList.add(date);
            } while (cursor.moveToNext());
        }

        // return date list
        return datesList;
    }
    // Getting dates Count
    public int getDatesCount() {
        String countQuery = "SELECT * FROM " + TABLE_DATES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating a date
    public int updateDate(CountdownDate date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, date.getTitle());
        values.put(KEY_DATETIME, date.getDateTime());
        values.put(KEY_REPEAT, date.getRepeat());
        values.put(KEY_FAVORITE, date.getFavorite());
        values.put(KEY_BACKGROUND, date.getBackground());

        // updating row
        return db.update(TABLE_DATES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(date.getId())});
    }

    // Deleting a date
    public void deleteDate(CountdownDate date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATES, KEY_ID + " = ?",
                new String[] { String.valueOf(date.getId()) });
        db.close();
    }
}
