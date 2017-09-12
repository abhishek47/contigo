package com.contigo.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Trumpets on 22/10/16.
 */

public class CGContactsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "CGContacts.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CGContactsContract.CGContactEntry.TABLE_NAME + " (" +
                    CGContactsContract.CGContactEntry._ID + " INTEGER PRIMARY KEY," +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS + TEXT_TYPE  + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY + " INTEGER" + " )";

    private static final String SQL_CREATE_HISTORY_ENTRIES =
            "CREATE TABLE " + CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME + " (" +
                    CGContactsContract.CGContactEntry._ID + " INTEGER PRIMARY KEY," +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID + TEXT_TYPE + " )";

    private static final String SQL_DELETE_HISTORY_ENTRIES =
            "DROP TABLE IF EXISTS " + CGContactsContract.CGContactEntry.HISTORY_TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CGContactsContract.CGContactEntry.TABLE_NAME;

    public CGContactsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_HISTORY_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_HISTORY_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}