/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaSetType.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-20
 */

package com.miui.video.statistic;

import android.content.Context;
import com.miui.video.type.MediaInfo;

/**
 *@author xuanmingliu
 *
 */

public class MediaSetTypeDef {
	
	public static final int MEDIA_SETTYPE_UNKNOWN_CODE = -1; 
	public static final int MEDIA_SETTYPE_SINGLE_CODE = 0;                //单集
	public static final int MEDIA_SETTYPE_MULTISETING_CODE = 1;           //多集连载中
	public static final int MEDIA_SETTYPE_MULTISETED_CODE = 2;            //多集已完结
	
	public static final String MEDIA_SETTYPE_UNKNOWN = "unknown";
	public static final String MEDIA_SETTYPE_SINGLE = "single";
	public static final String MEDIA_SETTYPE_MULTISETING = "multiseting";
	public static final String MEDIA_SETTYPE_MULTISETED = "multiseted";
	
	//单集，多集连载中、多集已完结   0 1 2
	public static String getMediaSetType(Context context, MediaInfo mediaInfo) {
		int mediaSetType = 0;
		if(mediaInfo != null) {
			if(mediaInfo.isMultiSetType()) {
				if (mediaInfo.setnow == mediaInfo.setcount) {		
					mediaSetType = MediaSetTypeDef.MEDIA_SETTYPE_MULTISETED_CODE;   //多集完结
				} else {			
					mediaSetType = MediaSetTypeDef.MEDIA_SETTYPE_MULTISETING_CODE;   //多集连载
				}
			} else {
				if(mediaInfo.playlength > 0) {
					mediaSetType = MediaSetTypeDef.MEDIA_SETTYPE_SINGLE_CODE;   //单集
				}
			}
		}

		switch(mediaSetType) {
		case MEDIA_SETTYPE_SINGLE_CODE:
			return MEDIA_SETTYPE_SINGLE;
		case MEDIA_SETTYPE_MULTISETING_CODE:
			return MEDIA_SETTYPE_MULTISETING;
		case MEDIA_SETTYPE_MULTISETED_CODE:
			return MEDIA_SETTYPE_MULTISETED;
		default:
			return MEDIA_SETTYPE_UNKNOWN;
		}
	}
}


