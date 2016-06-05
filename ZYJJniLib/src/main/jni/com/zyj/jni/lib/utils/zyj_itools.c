//
// Created by yuri.zheng on 2016/6/5.
//
#include "com_zyj_jni_ZYJJniLib.h"

JNIEXPORT jstring JNICALL Java_com_zyj_jni_ZYJJniLib_generateSettingCode
  (JNIEnv *env, jobject obj, jstring resource) {
      return (*env)->NewStringUTF(env, "I'm comes from to Native Function!");
}

