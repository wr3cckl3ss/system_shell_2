LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libsmtshell
LOCAL_CFLAGS := -std=c17 -Wall -Werror
LOCAL_SRC_FILES := \
  smtshell.c
LOCAL_LDLIBS := -ldl -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libsmtapi
LOCAL_CFLAGS := -std=c17 -Wall -Werror
LOCAL_SRC_FILES := \
  smtapi.c
LOCAL_LDLIBS := -ldl -llog
include $(BUILD_SHARED_LIBRARY)
