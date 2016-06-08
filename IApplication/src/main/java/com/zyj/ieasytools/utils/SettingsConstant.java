package com.zyj.ieasytools.utils;

/**
 * Created by yuri.zheng on 2016/5/17.
 */
public final class SettingsConstant {

    /**
     * View other database<br>
     * {@link String}
     */
    public static final String SETTINGS_VIEW_OTHER_DATABASE = "settings_view_other_database";
    /**
     * Pause time<br>
     * {@link Long}
     */
    public static final String SETTINGS_PAUSE_TIME = "settings_pause_time";
    /**
     * Set the password valid time<br>
     * {@link Long}
     */
    public static final String SETTINGS_PASSWORD_TIME_OUT = "settings_password_time_out";
    /**
     * Verify state last time<br>
     * If verify faile then record the time, if verify success then set this value is -1 or blew zero， default is -1<br>
     * {@link Long}
     */
    public static final String SETTINGS_VERIFY_STATE_LAST_TIME = "settings_verify_state_last_time";

    /**
     * Enter password style: <br>
     * {@link Long}
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_GESTURE}</li>
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_IMITATE_IOS}</li>
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_FINGERPRINT}</li>
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_INPUT}</li>
     */
    public static final String SETTINGS_PASSWORD_INPUT_STYLE = "settings_password_input_style";

    /**
     * Save the enter password <br>
     * {@link String}
     */
    public static final String SETTINGS_SAVE_ENTER_PASSWORD = "settings_save_enter_password";


}
