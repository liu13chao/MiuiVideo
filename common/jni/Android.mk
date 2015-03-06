LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := NativeApi

LOCAL_SRC_FILES := NativeApi.cpp \
                   JniUtil.cpp
                   
LOCAL_LDLIBS :=  -llog -lz
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libdk_client.a"
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libjson.a"
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libcurl.a"
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libcares.a"
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libssl.a"
LOCAL_LDLIBS += "D:\work\project\phone\me\MiuiVideo_V5_new\MiuiVideo_V5_REDMI2\jni\libs\libcrypto.a"


LOCAL_CPPFLAGS := -D_DEBUG

include $(BUILD_SHARED_LIBRARY)

