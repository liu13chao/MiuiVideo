package com.miui.videoplayer.framework;

import android.os.Build;
import android.util.Log;

public class DuoKanCodecConstants {
	public static final boolean IS_X1  = "mione".equals(Build.DEVICE)          || "mione_plus".equals(Build.DEVICE);
	public static final boolean IS_X2  = "aries".equals(Build.DEVICE);
	public static final boolean IS_X2A = miui.os.Build.IS_MI2A;
	public static final boolean IS_X3  = miui.os.Build.IS_MITHREE              || "cancro".equals(Build.DEVICE);
	public static final boolean IS_H2  = "HM2013023".equals(Build.DEVICE)      || "HM2013022".equals(Build.DEVICE);
    public static final boolean IS_H2A = "armani".equals(Build.DEVICE);
	public static final boolean IS_H2S = "HM2014011".equals(Build.DEVICE)      || "HM2014012".equals(Build.DEVICE);
	public static final boolean IS_H3  = "lcsh92_wet_tdd".equals(Build.DEVICE) || "lcsh92_wet_jb9".equals(Build.DEVICE);
	
	public static final boolean IS_X6 = "mocha".equals(Build.DEVICE);
	public static final boolean IS_N7 = "flo".equals(Build.DEVICE);

	public static boolean IS_DUOKAN_CODEC_PHONE = IS_X1 || IS_X2 || IS_X2A || IS_X3 || IS_H2 || IS_H2A || IS_H2S || IS_H3
			|| IS_X6 || IS_N7;
	public static boolean sUseDiracSound  = IS_DUOKAN_CODEC_PHONE;
	public static boolean sUseDuokanCodec = IS_DUOKAN_CODEC_PHONE;
	
	static {
		if (sUseDuokanCodec) {
			try {
				System.loadLibrary("xiaomimediaplayer");
			} catch (UnsatisfiedLinkError e) {
				Log.e("DuoKanCodecConstants", "Can not load duokan codec, use origin codec");
				IS_DUOKAN_CODEC_PHONE = false;
				sUseDuokanCodec = IS_DUOKAN_CODEC_PHONE;
			}

			try {
				Class DiracSound = Class.forName("android.media.audiofx.DiracSound");
			} catch (Exception e) {
				sUseDiracSound  = false;
				e.printStackTrace();
			}
		}
	}
}
