LOCAL_PATH := $(call my-dir)

SF_SRC_FILES := \
	benchmark.cpp main.cpp movegen.cpp pawns.cpp thread.cpp uci.cpp psqt.cpp \
	bitbase.cpp endgame.cpp material.cpp movepick.cpp position.cpp timeman.cpp ucioption.cpp \
	bitboard.cpp evaluate.cpp misc.cpp search.cpp tt.cpp syzygy/tbprobe.cpp jnitest.cpp


include $(CLEAR_VARS)
LOCAL_MODULE    := stockfish
LOCAL_SRC_FILES := $(SF_SRC_FILES)
LOCAL_CFLAGS    := --std=c++11 \
	-I $(LOCAL_PATH)/sysport/ -I -DNDEBUG -Wall
include $(BUILD_SHARED_LIBRARY)
