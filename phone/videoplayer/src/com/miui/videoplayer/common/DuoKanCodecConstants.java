package com.miui.videoplayer.common;

import android.util.Log;

public class DuoKanCodecConstants {
	public static final String TAG = "DuoKanCodecConstants";
	
	public static final boolean IS_DUOKAN_CODEC_PHONE;
	public static boolean sUseDuokanCodec = true;
	public static final boolean USE_DIRAC_SOUND;
	
	static {
		Log.i(TAG, "static block");
		final boolean loaded = loadDuokanCodec();
		if (loaded) {
			if (DuoKanConstants.FOR_MI_APPSTORE) {
				IS_DUOKAN_CODEC_PHONE = true;
				sUseDuokanCodec = true;
			} else {
				if (useOriginCodec()) {
		        	IS_DUOKAN_CODEC_PHONE = false;
		        	sUseDuokanCodec = false;
				} else {
					IS_DUOKAN_CODEC_PHONE = true;
					sUseDuokanCodec = true;
				}
			}
		} else {
        	IS_DUOKAN_CODEC_PHONE = false;
        	sUseDuokanCodec = false;
		}
        
		final boolean found = findDiracSound();
        if (found) {
			USE_DIRAC_SOUND = true;
		} else {
			USE_DIRAC_SOUND = false;
		}
    }
	
	public static void init() {
	}
	
	private static boolean useOriginCodec() {
		return DuoKanConstants.IS_CM_CUSTOMIZATION && (DuoKanConstants.IS_H2S || DuoKanConstants.IS_H3 || DuoKanConstants.IS_H3_LTE || DuoKanConstants.IS_X4_LTE);
	}
	
	private static boolean loadDuokanCodec() {
		Log.i(TAG, "for mi appstore: " + DuoKanConstants.FOR_MI_APPSTORE);
       	if (!DuoKanConstants.FOR_MI_APPSTORE) {
    		try {
    			Log.i(TAG, "load duokan codec from system");
        		System.loadLibrary("xiaomimediaplayer");
        		System.loadLibrary("xiaomiplayerwrapper");
        		return true;
			} catch (Throwable t) {
				Log.e(TAG, "load duokan codec from system error: " + t);
			}
    	} else {
			try {
				Log.i(TAG, "load duokan codec from data");
				System.load(DuoKanConstants.PATH_DATA_LIB + "libxvx.so");
				System.load(DuoKanConstants.PATH_DATA_LIB + "libffmpeg_duokan.so");
				System.load(DuoKanConstants.PATH_DATA_LIBS + "libxiaomimediaplayer.so");
				System.load(DuoKanConstants.PATH_DATA_LIBS + "libxiaomiplayerwrapper.so");
				return true;
			} catch (Throwable t) {
				Log.e(TAG, "load duokan codec from data error: " + t);
				try {
					Log.i(TAG, "load duokan codec from system");
					System.loadLibrary("xvx");
					System.loadLibrary("ffmpeg_duokan");
					System.loadLibrary("xiaomimediaplayer");
					System.loadLibrary("xiaomiplayerwrapper");
					return true;
				} catch (Throwable t2) {
					Log.e(TAG, "load duokan codec from system error: " + t2);
				}
			}
		}
       	return false;
	}
	
	private static boolean findDiracSound() {
		try {
			Log.i(TAG, "find dirac sound");
			Class.forName("android.media.audiofx.DiracSound");
			return true;
		} catch (Throwable t) {
			Log.e(TAG, "find dirac sound error: " + t);
		}
		return false;
	}
	
}
