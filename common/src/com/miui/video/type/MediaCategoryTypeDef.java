/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   MediaCategoryTypeDef.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.AppConfig;

/**
 *@author xuanmingliu
 *
 */

public class MediaCategoryTypeDef {
	
	//invalid
	public static final int  LOCALMEDIA_CATEGORY_UNKNOWN = -1;
	
	//play history
	public static final int  LOCALMEDIA_CATEGORY_PLAYHISTORY = 0;
	//my favorite
	public static final int  LOCALMEDIA_CATEGORY_MYFAVORITE = 1;
	//camera
	public static final int  LOCALMEDIA_CATEGORY_CAMERAVIDEO = 2;
	//other directory media
	public static final int  LOCALMEDIA_CATEGORY_OTHERDIR = 3;
	//single media
	public static final int  LOCALMEDIA_CATEGORY_SINGLEMEDIA = 4;
	//offline media
	public static final int  LOCALMEDIA_CATEGORY_OFFLINEMEDIA = 5;
	
	//browser directory media
	public static final int  LOCALMEDIA_CATEGORY_DLNA = 6;
	public static final int  LOCALMEDIA_CATEGORY_DLNA_LIST = 7;
		
	
	public static String getLocalMediaCategoryTitle(int localMediaCategoryId) {
		Resources res = DKApp.getAppContext().getResources();
		int categoryId = 0;
		switch(localMediaCategoryId)
		{
		case LOCALMEDIA_CATEGORY_PLAYHISTORY:
			categoryId = R.string.recentplay;
			break;
		case LOCALMEDIA_CATEGORY_MYFAVORITE: {
			AppConfig conf = DKApp.getSingleton(AppConfig.class);
			if( conf.isShowAll() || conf.isOnlyShowOnlineVideo())
				categoryId = R.string.myfavorite;
			else if( conf.isOnlyShowLocalVideo()) {
				categoryId = R.string.myfavorite_onlylocal;
			} else  {
				categoryId = R.string.myfavorite;
			}
		}
		break;
		case LOCALMEDIA_CATEGORY_CAMERAVIDEO:
			categoryId = R.string.mycamera;
			break;
		case LOCALMEDIA_CATEGORY_DLNA:
			categoryId = R.string.dlna_name;
			break;
		case LOCALMEDIA_CATEGORY_DLNA_LIST:
			categoryId = R.string.dlna_list_name;
			break;
		default:
			categoryId = 0;
			break;
		}
	
		if( categoryId != 0) {
			return res.getString(categoryId);
		} else
			return "";
	}
}


