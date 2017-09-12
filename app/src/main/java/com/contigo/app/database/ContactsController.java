package com.contigo.app.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.contigo.app.Contact;

import java.util.ArrayList;


/**
 * Created by Trumpets on 22/10/16.
 */

public class ContactsController {


    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    private static  String[] projection = {
            CGContactsContract.CGContactEntry._ID,
            CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME,
            CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE,
            CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL,
            CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS,
            CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME,
            CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS,
            CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE,
            CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL,
            CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS,
            CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC,
            CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD,
            CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,
            CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,
            CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY
    };


    public static long addContact(SQLiteDatabase db, Contact contact) {
        ContentValues values = new ContentValues();
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME, contact.getName()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE, contact.getHomePhone()); // Contact Phone Number
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL, contact.getHomeEmail()); // Contact Home Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS, contact.getHomeAddress()); // Contact Home Address
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME, contact.getCompanyName()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS, contact.getWorkAs()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE, contact.getWorkPhone()); // Contact Work Phone
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL, contact.getWorkEmail()); // Contact Work Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS, contact.getWorkAddress()); // Contact Work Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC, contact.getProfilePic()); // Contact Profile Pic Uri
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD,contact.getVisitingCard()); // Contact visiting card
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,contact.getTimeAdded()); // Contact time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,contact.getContactId()); // Contact card id
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY,contact.isPrimary()); // is self profile


        // Inserting Row
        long id = db.insert(CGContactsContract.CGContactEntry.TABLE_NAME, null, values);

        return id;


    }

    // Getting single contact
    public static Contact getContact(SQLiteDatabase db, String name) {

        Cursor cursor = db.query(CGContactsContract.CGContactEntry.TABLE_NAME, projection, CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null &&  cursor.moveToFirst()) {

            Contact contact = new Contact();
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
            contact.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
            contact.setHomeEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL)));
            contact.setHomeAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS)));
            contact.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME)));
            contact.setWorkAs(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS)));
            contact.setWorkPhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE)));
            contact.setWorkEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL)));
            contact.setWorkAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS)));
            contact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
            contact.setVisitingCard(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD)));
            contact.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
            contact.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
            contact.setIsPrimary(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY)));
            // return contact
            return contact;
        }

        return null;
    }

    // Getting single contact
    public static Contact getContactById(SQLiteDatabase db, String cId) {


        Cursor cursor = db.query(CGContactsContract.CGContactEntry.TABLE_NAME, projection, CGContactsContract.CGContactEntry._ID + "=?",
                new String[] { cId }, null, null, null, null);
        if (cursor != null &&  cursor.moveToFirst()) {

            Contact contact = new Contact();
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
            contact.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
            contact.setHomeEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL)));
            contact.setHomeAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS)));
            contact.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME)));
            contact.setWorkAs(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS)));
            contact.setWorkPhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE)));
            contact.setWorkEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL)));
            contact.setWorkAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS)));
            contact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
            contact.setVisitingCard(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD)));
            contact.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
            contact.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
            contact.setIsPrimary(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY)));
            // return contact
            return contact;
        }

        return null;
    }

    // Getting single contact
    public static Contact getProfileContact(SQLiteDatabase db) {

        Cursor cursor = db.query(CGContactsContract.CGContactEntry.TABLE_NAME, projection, CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY + "=?",
                new String[] { "1" }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {


            Contact contact = new Contact();
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
            contact.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
            contact.setHomeEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL)));
            contact.setHomeAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS)));
            contact.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME)));
            contact.setWorkAs(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS)));
            contact.setWorkPhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE)));
            contact.setWorkEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL)));
            contact.setWorkAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS)));
            contact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
            contact.setVisitingCard(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD)));
            contact.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
            contact.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
            contact.setIsPrimary(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY)));
            // return contact
            return contact;
        }

        return null;
    }

    // Getting All Contacts
    public static ArrayList<Contact> getAllContacts(SQLiteDatabase db) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + CGContactsContract.CGContactEntry.TABLE_NAME + " WHERE " + CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY + " = 0 ORDER BY " + CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry._ID)));
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME)));
                contact.setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE)));
                contact.setHomeEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL)));
                contact.setHomeAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS)));
                contact.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME)));
                contact.setWorkAs(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS)));
                contact.setWorkPhone(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE)));
                contact.setWorkEmail(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL)));
                contact.setWorkAddress(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS)));
                contact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC)));
                contact.setVisitingCard(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD)));
                contact.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED)));
                contact.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID)));
                contact.setIsPrimary(cursor.getInt(cursor.getColumnIndexOrThrow(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public static int getContactsCount(SQLiteDatabase db) {
        String countQuery = "SELECT  * FROM " + CGContactsContract.CGContactEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();

        // return count
        return c;
    }

    public static int updateContact(SQLiteDatabase db, Contact contact) {


        ContentValues values = new ContentValues();
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_FULLNAME, contact.getName()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_PHONE, contact.getHomePhone()); // Contact Phone Number
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_MAIL, contact.getHomeEmail()); // Contact Home Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_HOME_ADDRESS, contact.getHomeAddress()); // Contact Home Address
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_COMPANY_NAME, contact.getCompanyName()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_AS, contact.getWorkAs()); // Contact Name
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_PHONE, contact.getWorkPhone()); // Contact Work Phone
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_MAIL, contact.getWorkEmail()); // Contact Work Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_WORK_ADDRESS, contact.getWorkAddress()); // Contact Work Mail
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_PROFILE_PIC, contact.getProfilePic()); // Contact Profile Pic Uri
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_VISITING_CARD,contact.getVisitingCard()); // Contact visiting card
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_TIME_ADDED,contact.getTimeAdded()); // Contact time added
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_CONTACT_ID,contact.getContactId()); // Contact card id
        values.put(CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY,contact.isPrimary()); // Contact visiting card

        // updating row
        return db.update(CGContactsContract.CGContactEntry.TABLE_NAME, values, CGContactsContract.CGContactEntry._ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }



    public static void deleteContact(SQLiteDatabase db, Contact contact) {

        db.delete(CGContactsContract.CGContactEntry.TABLE_NAME, CGContactsContract.CGContactEntry._ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();

    }

    public static void deleteAllContacts(SQLiteDatabase db)
    {
        db.execSQL("DELETE FROM " + CGContactsContract.CGContactEntry.TABLE_NAME + " WHERE " + CGContactsContract.CGContactEntry.COLUMN_NAME_IS_PRIMARY + " = 0");

    }
}
