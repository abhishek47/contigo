package com.contigo.app.database;

import android.provider.BaseColumns;

/**
 * Created by Trumpets on 22/10/16.
 */
public final class CGContactsContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CGContactsContract() {}

    /* Inner class that defines the table contents */
    public static class CGContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "cg_contract";
        public static final String HISTORY_TABLE_NAME = "cg_history_contract";
        public static final String COLUMN_NAME_FULLNAME = "fullname";
        public static final String COLUMN_NAME_HOME_PHONE = "home_phone";
        public static final String COLUMN_NAME_HOME_MAIL = "home_mail";
        public static final String COLUMN_NAME_HOME_ADDRESS = "home_address";
        public static final String COLUMN_NAME_WORK_PHONE = "work_phone";
        public static final String COLUMN_NAME_WORK_MAIL = "work_mail";
        public static final String COLUMN_NAME_WORK_ADDRESS = "work_address";
        public static final String COLUMN_NAME_PROFILE_PIC = "profile_pic";
        public static final String COLUMN_NAME_VISITING_CARD= "visiting_card";
        public static final String COLUMN_NAME_COMPANY_NAME= "company_name";
        public static final String COLUMN_NAME_WORK_AS= "work_as";
        public static final String COLUMN_NAME_IS_PRIMARY= "is_primary";
        public static final String COLUMN_NAME_TIME_ADDED= "time_added";
        public static final String COLUMN_NAME_CONTACT_ID= "contact_id";
        public static final String COLUMN_NAME_STATE = "contact_state";


    }
}

