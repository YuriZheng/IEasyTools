package com.zyj.ieasytools.library.gesture;

/**
 * 点位置
 * Created by apple on 4/11/15.
 */
public class GesturePoint {
//    public static int STATE_NORMAL = 0; // 未选中
//    public static int STATE_CHECK = 1; // 选中 e
//    public static int STATE_CHECK_ERROR = 2; // 已经选中,但是输错误

    public float x;
    public float y;
    public CircleState state = CircleState.STATE_NORMAL;
    public int index = 0;// 下标

    /**
     * The Circle state
     */
    public enum CircleState {
        /**
         * Normal state
         */
        STATE_NORMAL,
        /**
         * Check state
         */
        STATE_CHECK,
        /**
         * Error state
         */
        STATE_CHECK_ERROR
    }

    public GesturePoint() {
    }

    public GesturePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
