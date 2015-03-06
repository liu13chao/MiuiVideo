/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaConfig.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-4
 */

package com.miui.videoplayer.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;

/**
 * @author tianli
 *
 */
public class MediaConfig {
	public static final int MEDIASOURCE_UNKNOWN_TYPE_CODE = -1;
	public static final int MEDIASOURCE_IQIYI_TYPE_CODE = 1;
	public static final int MEDIASOURCE_SOHU_TYPE_CODE = 3;
	public static final int MEDIASOURCE_YOUKU_TYPE_CODE = 4;
//	public static final int MEDIASOURCE_TENCENT_TYPE_CODE = 5;
	public static final int MEDIASOURCE_NETEASE_TYPE_CODE = 6;
	public static final int MEDIASOURCE_YINYUETAI_TYPE_CODE = 7;
	public static final int MEDIASOURCE_IQIYI_PHONE_TYPE_CODE = 8;
	public static final int MEDIASOURCE_WASU_TYPE_CODE = 9;
	public static final int MEDIASOURCE_TENCENT_TYPE_CODE = 10;
	public static final int MEDIASOURCE_SINA_TYPE_CODE = 11;
//	public static final int MEDIASOURCE_SINA_TYPE_CODE = 12;
	public static final int MEDIASOURCE_PPS_TYPE_CODE = 14;
	public static final int MEDIASOURCE_FUNSHION_TYPE_CODE = 15;
	public static final int MEDIASOURCE_BESTV_TYPE_CODE = 16;
	public static final int MEDIASOURCE_LEKAN_TYPE_CODE = 17;
	public static final int MEDIASOURCE_M1905_TYPE_CODE = 18;
	public static final int MEDIASOURCE_ICNTV_TYPE_CODE = 19;
	public static final int MEDIASOURCE_YOUKU_PHONE_TYPE_CODE = 20;
	public static final int MEDIASOURCE_PPTV_TYPE_CODE = 21;
	public static final int MEDIASOURCE_TUDOU_PHONE_TYPE_CODE = 23;
	public static final int MEDIASOURCE_IFENG_PHONE_TYPE_CODE = 24;
	public static final int MEDIASOURCE_M1905_PHONE_TYPE_CODE = 25;
	public static final int MEDIASOURCE_BESTV_PHONE_TYPE_CODE = 31;
	public static final int MEDIASOURCE_LETV_PHONE_TYPE_CODE = 32;
	public static final int MEDIASOURCE_PPS_PHONE_TYPE_CODE = 33;
	public static final int MEDIASOURCE_FUNSHION_PHONE_TYPE_CODE = 34;
	public static final int MEDIASOURCE_CNLIVE_TYPE_CODE = 35;
	public static final int MEDIASOURCE_WOLE_PHONE_TYPE_CODE = 36;
	
	public static boolean needShowLogo(int source) {
		return MEDIASOURCE_SOHU_TYPE_CODE == source;
	}
	public static Drawable getLogoBySource(Context context, int source) {
		final Resources res = context.getResources();
		switch (source) {
		case MediaConfig.MEDIASOURCE_SOHU_TYPE_CODE:
			return res.getDrawable(R.drawable.icon_sohu);
		default:
			return null;
		}
	}
	public static String getResolutionName(Context context, int resolution) {
		final Resources res = context.getResources();
		switch (resolution) {
		case MediaConstantsDef.CLARITY_NORMAL:
			return res.getString(R.string.vp_standard_definition);
		case MediaConstantsDef.CLARITY_HIGH:
			return res.getString(R.string.vp_high_definition);
		case MediaConstantsDef.CLARITY_SUPPER:
			return res.getString(R.string.vp_super_definition);
		default:
			return res.getString(R.string.vp_standard_definition);
		}
	}
}
