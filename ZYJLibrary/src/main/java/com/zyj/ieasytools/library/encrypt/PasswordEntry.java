package com.zyj.ieasytools.library.encrypt;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The password entry
 *
 * @author yuri.zheng 2016/04/25
 */
public final class PasswordEntry implements Parcelable {

    /**
     * Web group，default
     */
    public static final int CATEGORY_WEB = 0;
    /**
     * Email group
     */
    public static final int CATEGORY_EMAIL = 1;
    /**
     * Wallet group
     */
    public static final int CATEGORY_WALLET = 2;
    /**
     * Application group
     */
    public static final int CATEGORY_APP = 3;
    /**
     * Game group
     */
    public static final int CATEGORY_GAME = 4;
    /**
     * Other group
     */
    public static final int CATEGORY_OTHER = 5;

    private String uuid; // No change

    /**
     * Category<br>
     * {@link #CATEGORY_WEB}<br>
     * {@link #CATEGORY_EMAIL}<br>
     * {@link #CATEGORY_WALLET}<br>
     * {@link #CATEGORY_GAME}<br>
     * {@link #CATEGORY_OTHER}<br>
     * Other Customize group
     */
    public int p_category = CATEGORY_WEB;

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
     * The add time
     */
    private long p_add_time = -1;// No change

    /**
     * The last modify time
     */
    public long p_modify_time = -1;

    /**
     * The encrypt method
     */
    private String p_encryption_method = BaseEncrypt.ENCRYPT_AES;// No change

    /**
     * The public key
     */
    private String p_encryption_public_key = "";// No change

    /**
     * The private key
     */
    private String p_encryption_private_key = "";// No change

    /**
     * The remark
     */
    public String p_remarks;

    /**
     * This app's version,Compatible version of encrypt method
     */
    public final int p_version = Build.VERSION.SDK_INT;

    /**
     * @param uuid        key
     * @param addTime     this record add time,if addTime is -1, say this is a new entry
     * @param method      encryption method
     * @param private_key private key,the see password
     */
    public PasswordEntry(String uuid, long addTime, String method, String private_key) {
        this(uuid, addTime, method, "", private_key);
    }

    /**
     * @param uuid        key
     * @param addTime     this record add time,if addTime is -1, say this is a new entry
     * @param method      encryption method
     * @param public_key  public key,Temporarily not used
     * @param private_key private key,the see password
     * @deprecated Temporarily not used to public
     */
    public PasswordEntry(String uuid, long addTime, String method, String public_key, String private_key) {
        this.uuid = uuid;
        if (p_add_time <= 0) {
            p_add_time = System.currentTimeMillis();
        } else {
            p_add_time = addTime;
        }
        this.p_encryption_public_key = public_key;
        this.p_encryption_private_key = private_key;
        this.p_encryption_method = method;
    }

    private PasswordEntry(Parcel in) {
        uuid = in.readString();
        p_category = in.readInt();
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
        p_encryption_public_key = in.readString();
        p_encryption_private_key = in.readString();
        p_remarks = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeInt(p_category);
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
        dest.writeString(p_encryption_public_key);
        dest.writeString(p_encryption_private_key);
        dest.writeString(p_remarks);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PasswordEntry) {
            PasswordEntry entry = (PasswordEntry) o;
            // uuid、password、username
            return entry.uuid.equals(this.uuid) && entry.p_password.equals(p_password) && entry.p_username.equals(p_username);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Title: " + p_title
                + "UUID: " + uuid
                + "Username: " + p_username
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
     * {@link #p_encryption_public_key}
     */
    public String getEncryptionPublicKey() {
        return p_encryption_public_key;
    }

    /**
     * {@link #p_encryption_private_key}
     */
    public String getEncryptionPrivateKey() {
        return p_encryption_private_key;
    }

    /**
     * {@link #p_encryption_method}
     */
    public String getEncryptionMethod() {
        return p_encryption_method;
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
