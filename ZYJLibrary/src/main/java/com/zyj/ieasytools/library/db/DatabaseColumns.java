package com.zyj.ieasytools.library.db;

import android.provider.BaseColumns;

/**
 * Package the columns
 * Created by yuri.zheng on 2016/4/26.
 */
public abstract class DatabaseColumns {

    /**
     * Database file suffix
     */
    public static final String DATABASE_FILE_SUFFIX = "izyj";

    public static class EncryptColumns implements BaseColumns {

        /**
         * The table name
         */
        public static final String TABLE_NAME = "encrypt";
        /**
         * The Encrypt database's file name
         */
        public static final String DATABASE_NAME = TABLE_NAME + "." + DATABASE_FILE_SUFFIX;
        /**
         * UUID only key
         */
        public static final String _UUID = "__uuid";
        /**
         * Record category
         */
        public static final String _CATEGORY = "__category";
        /**
         * Title
         */
        public static final String _TITLE = "__title";
        /**
         * User name
         */
        public static final String _USERNAME = "__username";
        /**
         * Password string
         */
        public static final String _PASSWORD = "__pad";
        /**
         * Address
         */
        public static final String _ADDRESS = "__address";
        /**
         * Description
         */
        public static final String _DESCRIPTION = "__description";
        /**
         * E-mail
         */
        public static final String _EMAIL = "__e_mail";
        /**
         * Phone
         */
        public static final String _PHONE = "__phone";
        /**
         * Pad question 1
         */
        public static final String _QUESTION_1 = "__question_1";
        /**
         * Pad answer 1
         */
        public static final String _QUESTION_ANSWER_1 = "__question_answer_1";
        /**
         * Pad question 2
         */
        public static final String _QUESTION_2 = "__question_2";
        /**
         * Pad answer 2
         */
        public static final String _QUESTION_ANSWER_2 = "__question_answer_2";
        /**
         * Pad question 3
         */
        public static final String _QUESTION_3 = "__question_3";
        /**
         * Pad answer 3
         */
        public static final String _QUESTION_ANSWER_3 = "__question_answer_3";
        /**
         * Add time
         */
        public static final String _ADD_TIME = "__add_time";
        /**
         * Modify time
         */
        public static final String _MODIFY_TIME = "__modify_time";
        /**
         * Encryption method
         */
        public static final String _ENCRYPTION_METHOD = "__encryption_method";
        /**
         * The test password from
         */
        public static final String _ENCRYPTION_TEST_FROM = "__encryption_test_from";
        /**
         * The test password to
         */
        public static final String _ENCRYPTION_TEST_TO = "__encryption_test_to";
        /**
         * Remarks
         */
        public static final String _REMARKS = "__remarks";
        /**
         * This app's version,Compatible version of encrypt method
         */
        public static final String _Version = "__app_version";
        /**
         * Reserve field one
         */
        public static final String _Reserve_0 = "__r_reserve_0";
        /**
         * Reserve field two
         */
        public static final String _Reserve_1 = "__r_reserve_1";
        /**
         * Reserve field three
         */
        public static final String _Reserve_2 = "__r_reserve_2";
        /**
         * Reserve field four
         */
        public static final String _Reserve_3 = "__r_reserve_3";
        /**
         * Reserve field five
         */
        public static final String _Reserve_4 = "__r_reserve_4";
        /**
         * The sql for create setting table
         */
        public static final String CREATE_SETTING_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + _UUID + " TEXT, "
                + _CATEGORY + " TEXT, "
                + _TITLE + " TEXT, "
                + _USERNAME + " TEXT, "
                + _PASSWORD + " TEXT, "
                + _ADDRESS + " TEXT, "
                + _DESCRIPTION + " TEXT, "
                + _EMAIL + " TEXT, "
                + _PHONE + " TEXT, "
                + _QUESTION_1 + " TEXT, "
                + _QUESTION_ANSWER_1 + " TEXT, "
                + _QUESTION_2 + " TEXT, "
                + _QUESTION_ANSWER_2 + " TEXT, "
                + _QUESTION_3 + " TEXT, "
                + _QUESTION_ANSWER_3 + " TEXT, "
                + _ADD_TIME + " TEXT, "
                + _MODIFY_TIME + " TEXT, "
                + _ENCRYPTION_METHOD + " TEXT, "
                + _ENCRYPTION_TEST_FROM + " TEXT, "
                + _ENCRYPTION_TEST_TO + " TEXT, "
                + _REMARKS + " TEXT, "
                + _Version + " TEXT, "
                + _Reserve_0 + " TEXT, "
                + _Reserve_1 + " TEXT, "
                + _Reserve_2 + " TEXT, "
                + _Reserve_3 + " TEXT, "
                + _Reserve_4 + " TEXT )";

    }

    public static class SettingColumns implements BaseColumns {

        /**
         * The table name
         */
        public static final String TABLE_NAME = "settings";
        /**
         * The settings database's file name
         */
        public static final String DATABASE_NAME = TABLE_NAME + "." + DATABASE_FILE_SUFFIX;
        /**
         * Settings key
         */
        public static final String _KEY = "__key";
        /**
         * Settings value
         */
        public static final String _VALUE = "__value";
        /**
         * The sql for create setting table
         */
        public static final String CREATE_SETTING_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + _KEY + " TEXT, "
                + _VALUE + " TEXT )";
    }

}
