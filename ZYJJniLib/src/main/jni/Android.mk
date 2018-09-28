LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := zyj_ndk_itools

LOCAL_SRC_FILES := com/zyj/jni/lib/utils/zyj_itools.c

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)