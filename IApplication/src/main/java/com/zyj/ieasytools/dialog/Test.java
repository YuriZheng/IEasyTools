package com.zyj.ieasytools.dialog;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.zyj.ieasytools.R;
import com.zyj.ieasytools.library.views.gesture.CustomLockView;
import com.zyj.ieasytools.library.utils.ZYJUtils;

/**
 * Author: Yuri.zheng<br>
 * Date: 8/21/16<br>
 * Email: 497393102@qq.com<br>
 */
public class Test {

    private final Class TAG = getClass();

    private CustomLockView mLockView;

    private void test(Context mContext) {
//        mLockView = (CustomLockView) v.findViewById(R.id.lock_view);
        mLockView.setLocusRoundOriginal(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_unselected));
        mLockView.setLocusRoundClick(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected));
        mLockView.setLocusArrow(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrown));
        mLockView.setLocusRoundError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_selected_error));
        mLockView.setLocusArrowError(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.gesture_trianglebrownerror));

        mLockView.setCirclePadd(100);

        mLockView.setEncryptPassword(new CustomLockView.EncryptPassword() {
            @Override
            public String encrypt(String resourceString) {
//                return mEncrypt.encrypt(resourceString, 1);
                return resourceString;
            }

            @Override
            public String decrypt(String encryptString) {
//                return mEncrypt.decrypt(encryptString, 1);
                return encryptString;
            }
        });

        mLockView.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
            @Override
            public void onComplete(int[] indexs) {
                if (mLockView.getLockViewStyle() == CustomLockView.LOCK_STATUS.LOCK_VERIFY) {
                    ZYJUtils.logD(TAG, "验证通过");
                }
            }

            @Override
            public void onError(int errorMessage) {
                ZYJUtils.logD(TAG, "Error: " + errorMessage);
            }

            @Override
            public void onCompleteSetting(int[] indexs) {
                ZYJUtils.logD(TAG, "onCompleteSetting: " + indexs.length);
            }

            @Override
            public void onSelecting(int[] selectPoints) {
                StringBuilder sb = new StringBuilder();
                for (int index : selectPoints) {
                    sb.append(index + ", ");
                }
                ZYJUtils.logD(TAG, "onSelecting: " + sb.toString());
            }
        });
        mLockView.resetPassword();
    }

    private void test1(Context mContext) {
        android.support.v4.hardware.fingerprint.FingerprintManagerCompat compat = android.support.v4.hardware.fingerprint.FingerprintManagerCompat.from(mContext);
        // 获取是否支持指纹
        boolean exit = compat.isHardwareDetected();
        // 获取是否至少有一个指纹
        compat.hasEnrolledFingerprints();

        android.hardware.fingerprint.FingerprintManager manager = (android.hardware.fingerprint.FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            boolean exit = manager.isHardwareDetected();
//            ZYJUtils.logD(TAG, "Exit: " + exit);
//            if (exit) {
//                ZYJUtils.logD(TAG, "至少一个：" + manager.hasEnrolledFingerprints());
//            }
        }
    }
}
