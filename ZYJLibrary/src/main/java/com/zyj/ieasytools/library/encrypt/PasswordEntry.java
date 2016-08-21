package com.zyj.ieasytools.library.encrypt;

import android.os.Parcel;
import android.os.Parcelable;

import com.zyj.ieasytools.library.utils.ZYJDatabaseUtils;
import com.zyj.ieasytools.library.utils.ZYJVersion;

/**
 * The password entry
 *
 * @author yuri.zheng 2016/04/25
 */
public final class PasswordEntry implements Parcelable {

    // TODO: 2016/8/18  6个不加密、6个不能修改，
    // TODO: 2016/8/18 因为显示简介时，需要根据不同的查看密码来获取真实信息，所以这里规定“UUID”、“类别”、“标题”、“用户”、“描述”和“备注”六个字段不进行任何形式的加密

    /**
     * Web group，default
     */
    public static final String CATEGORY_WEB = "web";
    /**
     * Email group
     */
    public static final String CATEGORY_EMAIL = "email";
    /**
     * Wallet group
     */
    public static final String CATEGORY_WALLET = "wallet";
    /**
     * Application group
     */
    public static final String CATEGORY_APP = "app";
    /**
     * Game group
     */
    public static final String CATEGORY_GAME = "game";
    /**
     * Other group
     */
    public static final String CATEGORY_OTHER = "other";

    /**
     * <h1>No change</h1>
     * UUID,Uniquely identifies
     */
    private String uuid;

    /**
     * Category<br>
     * {@link #CATEGORY_WEB}<br>
     * {@link #CATEGORY_EMAIL}<br>
     * {@link #CATEGORY_WALLET}<br>
     * {@link #CATEGORY_GAME}<br>
     * {@link #CATEGORY_OTHER}<br>
     * Other Customize group
     */
    public String p_category = CATEGORY_WEB;

    /**
     * Save title
     */
    public String p_title;

    /**
     * Save username
     */
    public String p_username;

    /**
     * Save password
     */
    public String p_password;

    /**
     * Save address
     */
    public String p_address;

    /**
     * Save description
     */
    public String p_description;

    /**
     * Save email
     */
    public String p_email;

    /**
     * Save phone
     */
    public String p_phone;

    /**
     * Save question1
     */
    public String p_q_1;

    /**
     * Save answer1
     */
    public String p_q_a_1;

    /**
     * Save question2
     */
    public String p_q_2;

    /**
     * Save answer2
     */
    public String p_q_a_2;

    /**
     * Save question3
     */
    public String p_q_3;

    /**
     * Save answer3
     */
    public String p_q_a_3;

    /**
     * <h1>No change</h1>
     * The add time, not encrypt
     */
    private long p_add_time = -1;

    /**
     * The last modify time, not encrypt
     */
    public long p_modify_time = -1;

    /**
     * <h1>No change</h1>
     * The encrypt method, not encrypt
     */
    private String p_encryption_method = BaseEncrypt.ENCRYPT_AES;

    /**
     * <h1>No change</h1>
     * Compared field, to judg the password, not encrypt
     */
    private String p_test_from;

    /**
     * <h1>No change</h1>
     * The string after encrypt, not encrypt
     */
    private String p_test_to;

    /**
     * The remark
     */
    public String p_remarks;

    /**
     * <h1>No change</h1>
     * This app's version,Compatible version of encrypt method, not encrypt<br>
     * {@link android.os.Build.VERSION#SDK_INT}
     */
    public int p_version;

    /**
     * Instance from database
     *
     * @param uuid    key
     * @param addTime this record add time,if addTime is -1, say this is a new entry
     * @param method  encryption method
     * @param from    check the passwrod resource
     * @param to      check the password's string after compared
     * @param version encrypted app's version
     */
    public PasswordEntry(String uuid, long addTime, String method, String from, String to, int version) {
        this.uuid = uuid;
        if (addTime <= 0) {
            p_add_time = System.currentTimeMillis();
        } else {
            p_add_time = addTime;
        }
        p_version = version;
        p_encryption_method = method;
        p_test_from = from;
        p_test_to = to;
    }

    /**
     * New from code
     *
     * @param uuid     key
     * @param password the entry's password
     * @param method   encryption method
     */
    public PasswordEntry(String uuid, String password, String method) {
        String[] test = ZYJDatabaseUtils.generateTestTo(method, password, ZYJVersion.getCurrentVersion());
        this.uuid = uuid;
        p_add_time = System.currentTimeMillis();
        p_modify_time = p_add_time;
        p_version = ZYJVersion.getCurrentVersion();
        p_encryption_method = method;
        p_test_from = test[0];
        p_test_to = test[1];
    }

    private PasswordEntry(Parcel in) {
        uuid = in.readString();
        p_category = in.readString();
        p_username = in.readString();
        p_password = in.readString();
        p_address = in.readString();
        p_description = in.readString();
        p_email = in.readString();
        p_phone = in.readString();
        p_q_1 = in.readString();
        p_q_a_1 = in.readString();
        p_q_2 = in.readString();
        p_q_a_2 = in.readString();
        p_q_3 = in.readString();
        p_q_a_3 = in.readString();
        p_title = in.readString();
        p_add_time = in.readLong();
        p_modify_time = in.readLong();
        p_encryption_method = in.readString();
        p_remarks = in.readString();
        p_test_from = in.readString();
        p_test_to = in.readString();
        p_version = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(p_category);
        dest.writeString(p_username);
        dest.writeString(p_password);
        dest.writeString(p_address);
        dest.writeString(p_description);
        dest.writeString(p_email);
        dest.writeString(p_phone);
        dest.writeString(p_q_1);
        dest.writeString(p_q_a_1);
        dest.writeString(p_q_2);
        dest.writeString(p_q_a_2);
        dest.writeString(p_q_3);
        dest.writeString(p_q_a_3);
        dest.writeString(p_title);
        dest.writeLong(p_add_time);
        dest.writeLong(p_modify_time);
        dest.writeString(p_encryption_method);
        dest.writeString(p_remarks);
        dest.writeString(p_test_from);
        dest.writeString(p_test_to);
        dest.writeInt(p_version);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PasswordEntry) {
            PasswordEntry entry = (PasswordEntry) o;
            // uuid、password、username
            return entry.uuid.equals(this.uuid) && entry.p_username.equals(p_username);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Title: " + p_title
                + ", UUID: " + uuid
                + ", Username: " + p_username
                + ", Add time: " + p_add_time
                + ", Encrypt way: " + p_encryption_method;
    }

    @Override
    public int hashCode() {
        return getHashCodeFromUUID(uuid.substring(uuid.length() - 9));
    }

    private int getHashCodeFromUUID(String uuidSuffix) {
        int length = uuidSuffix.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(getHashCodeString(uuidSuffix.charAt(i)));
        }
        int code = 0;
        try {
            code = Integer.parseInt(p_category + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getHashCodeString(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch + "";
        }
        return ch % 10 + "";
    }

    /**
     * {@link #uuid}
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * {@link #p_add_time}
     */
    public long getAddTime() {
        return p_add_time;
    }

    /**
     * {@link #p_encryption_method}
     */
    public String getEncryptionMethod() {
        return p_encryption_method;
    }

    /**
     * {@link #p_test_from}
     */
    public String getTestFrom() {
        return p_test_from;
    }

    /**
     * {@link #p_test_to}
     */
    public String getTestTo() {
        return p_test_to;
    }

    /**
     * {@link #p_version}
     */
    public int getEncryptVersion() {
        return p_version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PasswordEntry> CREATOR = new Creator<PasswordEntry>() {

        @Override
        public PasswordEntry createFromParcel(Parcel source) {
            return new PasswordEntry(source);
        }

        @Override
        public PasswordEntry[] newArray(int size) {
            return new PasswordEntry[size];
        }

    };

}
