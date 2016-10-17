package com.zyj.ieasytools.data;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public final class SettingsConstant {

    // =================================================================================================================
    // String
    // =================================================================================================================

    /**
     * View other database<br>
     * key: {@link String}
     */
    public static final String SETTINGS_VIEW_OTHER_DATABASE = "s_v_o_d";
    /**
     * Pause time<br>
     * key: {@link Long}
     */
    public static final String SETTINGS_PAUSE_TIME = "s_p_t";
    /**
     * Set the password valid time<br>
     * key: {@link Long}<br>
     */
    public static final String SETTINGS_PASSWORD_TIME_OUT = "s_p_t_o";
    /**
     * Verify state last time<br>
     * If verify faile then record the time, if verify success then set this value is -1 or blew zero， default is -1<br>
     * key: {@link Long}
     */
    public static final String SETTINGS_VERIFY_STATE_LAST_TIME = "s_v_s_l_t";
    /**
     * Get the directory path if already open once<br>
     * key: {@link String}
     */
    public static final String SETTINGS_DIRECTORY_RECORD_PATH = "s_d_p";

    /**
     * Enter password style: <br>
     * key: {@link Long}
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_IMITATE_IOS}</li>
     * <li>{@link com.zyj.ieasytools.dialog.InputEnterPasswordDialog#ENTER_PASSWORD_FINGERPRINT}</li>
     */
    public static final String SETTINGS_PASSWORD_INPUT_STYLE = "s_p_i_s";

    /**
     * The toolbar title size: save in {@link com.zyj.ieasytools.act.mainActivity.MainActivity}<br>
     * key: {@link Float}
     */
    public static final String TOOLBAR_TITLE_SIZE = "_t_s";

    /**
     * Save the enter password <br>
     * {@link String}
     */
    public static final String SETTINGS_SAVE_ENTER_PASSWORD = "s_s_e_p";

    // =================================================================================================================
    // Long
    // =================================================================================================================

    /**
     * The password time out default time:default ten minutes<br>
     * {@link Long}
     */
    public static final Long SETTINGS_PASSWORD_TIME_OUT_DEFAULT_VALUE = new Long(1000 * 60 * 10);


}
