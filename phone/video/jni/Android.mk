LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := file_scanner

LOCAL_SRC_FILES := FileScanner.cpp
                   
LOCAL_LDLIBS :=  -llog

LOCAL_CPPFLAGS := -D_DEBUG

include $(BUILD_SHARED_LIBRARY)

