LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
 
LOCAL_STATIC_JAVA_LIBRARIES := phonecli

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call all-java-files-under, src_pd)

LOCAL_PACKAGE_NAME := MiuiVideoPlayer
LOCAL_PRODUCT_AAPT_CONFIG := xhdpi
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_ENABLED := disabled

# We mark this out until Mtp and MediaMetadataRetriever is unhidden.
#LOCAL_SDK_VERSION := current

LOCAL_REQUIRED_MODULES :=     		\
	libxiaomimediaplayer					\
	libxiaomiplayerwrapper \

include $(BUILD_PACKAGE)
# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
