package com.zyj.ieasytools.library.db;

import android.provider.BaseColumns;

/**
 * Created by yuri.zheng on 2016/4/26.
 */
public abstract class DatabaseColumns {

    public static class PasswordColumns implements BaseColumns {
        /**
         * UUID only key
         */
        public static final String P_UUID = "_p_uuid";
        /**
         * Record category
         */
        public static final String P_CATEGORY = "_p_category";
        /**
         * Title
         */
        public static final String P_TITLE = "_p_title";
        /**
         * User name
         */
        public static final String P_USERNAME = "_p_username";
        /**
         * Password string
         */
        public static final String P_PASSWORD = "_p_pad";
        /**
         * Address
         */
        public static final String P_ADDRESS = "_p_address";
        /**
         * Description
         */
        public static final String P_DESCRIPTION = "_p_description";
        /**
         * E-mail
         */
        public static final String P_EMAIL = "_p_e_mail";
        /**
         * Phone
         */
        public static final String P_PHONE = "_p_phone";
        /**
         * Pad question 1
         */
        public static final String P_QUESTION_1 = "_p_question_1";
        /**
         * Pad answer 1
         */
        public static final String P_QUESTION_ANSWER_1 = "_p_question_answer_1";
        /**
         * Pad question 2
         */
        public static final String P_QUESTION_2 = "_p_question_2";
        /**
         * Pad answer 2
         */
        public static final String P_QUESTION_ANSWER_2 = "_p_question_answer_2";
        /**
         * Pad question 3
         */
        public static final String P_QUESTION_3 = "_p_question_3";
        /**
         * Pad answer 3
         */
        public static final String P_QUESTION_ANSWER_3 = "_p_question_answer_3";
        /**
         * Add time
         */
        public static final String P_ADD_TIME = "_p_add_time";
        /**
         * Modify time
         */
        public static final String P_MODIFY_TIME = "_p_modify_time";
        /**
         * Encryption method
         */
        public static final String P_ENCRYPTION_METHOD = "_p_encryption_method";
        /**
         * Encryption public key <h1>not used
         */
        public static final String P_ENCRYPTION_PUBLIC_KEY = "_p_encryption_public_key";
        /**
         * Encryption private key <h1>check entry password
         */
        public static final String P_ENCRYPTION_PRIVATE_KEY = "_p_encryption_private_key";
        /**
         * Remarks
         */
        public static final String P_REMARKS = "_p_remarks";
        /**
         * This app's version,Compatible version of encrypt method
         */
        public static final String P_Version = "_p_app_version";
        /**
         * Reserve field one
         */
        public static final String P_Reserve_0 = "_p_r_reserve_0";
        /**
         * Reserve field two
         */
        public static final String P_Reserve_1 = "_p_r_reserve_1";
        /**
         * Reserve field three
         */
        public static final String P_Reserve_2 = "_p_r_reserve_2";
        /**
         * Reserve field four
         */
        public static final String P_Reserve_3 = "_p_r_reserve_3";
        /**
         * Reserve field five
         */
        public static final String P_Reserve_4 = "_p_r_reserve_4";

    }

    public static class SettingColumns implements BaseColumns {
        /**
         * Settings key
         */
        public static final String S_KEY = "_s_key";
        /**
         * Settings value
         */
        public static final String S_VALUE = "_s_value";
    }

}
