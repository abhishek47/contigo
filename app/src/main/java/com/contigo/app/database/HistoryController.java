package com.contigo.app.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.contigo.app.History;

import java.util.ArrayList;


/**
 * Created by Trumpets on 22/10/16.
 */

public class HistoryController {


    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    private static  String[] projection = {
            CGContactsContract.CGContactEntry._ID,
            CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME,
            CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE,
            CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC,
            CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,
            CGContactsContract.CGContactEntry.COLUMN_NAME_STATE,
            CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,
            CGContactsContract.CGContactEntry.COLUMN_NAME_STATE,
     };


    public static void addHistory(SQLiteDatabase db, History History) {
        ContentValues values = new ContentValues();
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME, History.getName()); // History Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE, History.getHomePhone()); // History Phone Number
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC, History.getProfilePic()); // History Profile Pic Uri
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,History.getTimeAdded()); // History time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_STATE,History.getState()); // History time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,History.getContactId()); // History card id


        // Inserting Row
        db.insert(CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME, null, values);


    }

    // Getting single History
    public static History getHistory(SQLiteDatabase db, String name) {

        Cursor cursor = db.query(CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME, projection, CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null &&  cursor.moveToFirst()) {

            History History = new History();
            History.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
            History.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
            History.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
            History.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
            History.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
            History.setState(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_STATE)));
            History.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
            // return History
            return History;
        }

        return null;
    }


    // Getting All Historys
    public static ArrayList<History> getAllHistory(SQLiteDatabase db) {
        ArrayList<History> HistoryList = new ArrayList<History>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME + " ORDER BY " + CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History History = new History();
                History.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
                History.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
                History.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
                History.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
                History.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
                History.setState(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_STATE)));
                History.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
                // Adding History to list
                HistoryList.add(History);
            } while (cursor.moveToNext());
        }

        // return History list
        return HistoryList;
    }

    public static int getHistoryCount(SQLiteDatabase db) {
        String countQuery = "SELECT  * FROM " + CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME;

        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();

        // return count
        return c;
    }

    public static int updateHistory(SQLiteDatabase db, History History) {


        ContentValues values = new ContentValues();
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME, History.getName()); // History Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE, History.getHomePhone()); // History Phone Number
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC, History.getProfilePic()); // History Profile Pic Uri
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,History.getTimeAdded()); // History time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_STATE,History.getState()); // History time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,History.getContactId()); // History card id

        // updating row
        return db.update(CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME, values, CGContactsContract.CGContactEntry._ID + " = ?",
                new String[] { String.valueOf(History.getId()) });
    }



    public static void deleteHistory(SQLiteDatabase db, History History) {

        db.delete(CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME, CGContactsContract.CGContactEntry._ID + " = ?",
                new String[] { String.valueOf(History.getId()) });
        db.close();

    }

    public static void deleteAllHistorys(SQLiteDatabase db)
    {
        db.execSQL("DELETE FROM " + CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME);

    }
}
