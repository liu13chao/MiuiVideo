/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   LocalMyFavoriteItemInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-30
 */

package com.miui.video.type;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 *@author xuanmingliu
 *
 */

public class LocalMyFavoriteItemInfo implements Comparable<LocalMyFavoriteItemInfo>{

    public static class MyFavoriteFlags
    {
    	public static final int FLAG_ONLY_MYFAVORITE = 0;
    	public static final int FLAG_WAITFORNEWSET_MYFAVORITE = 1;
    }
    
    public static final String TAG = LocalMyFavoriteItemInfo.class.getSimpleName();
	
	public MediaInfo  mediaInfo;
	
	public int localCount;
	public String localVideoPath;
	public String localAlbum;
	public int localFlag = -1;
	public int localId = -1;
	
	public static final int  MASK_LOCALFLAG_SET = 0x0001;
	public static final int  MASK_LOCALFLAG_VIDEO = 0x0002;
	
	public static final int  TYPE_MYFAVORITE_SET = 0x0001;
	public static final int  TYPE_MYFAVORITE_VIDEO = 0x0002;
	
	public static final int  TYPE_LOCALFLAG_ONLYSET = 0x0001;
	public static final int  TYPE_LOCALFLAG_ONLYVIDEO = 0x0002;
	public static final int  TYPE_LOCALFLAG_SETVIDEO = 0x0003;

	public String  addDate;
	public int flag;    //0  myfavorite  1 for new set
	public int level;   //watermark level
	
	public boolean localVideo;
	public boolean syncedToNetwork;
	public boolean localDeleted;
	
	public List<LocalMyFavoriteItemInfo>  localVideoList = null;
	
	public String getMyFavoriteItemName()
	{
		if( mediaInfo != null)
			return mediaInfo.medianame;
		
		int startIndex = localVideoPath.lastIndexOf(File.separator) + 1;
		int endIndex = localVideoPath.lastIndexOf('.');
		if( endIndex == -1)
			endIndex = localVideoPath.length() - 1;
		String mediaName = localVideoPath.substring(startIndex, endIndex);
		return mediaName;
	}
	
	public boolean isWaitForNewSet()
	{
		if( flag == MyFavoriteFlags.FLAG_WAITFORNEWSET_MYFAVORITE)
			return true;
		
		return false;
	}

	@Override
	public int compareTo(LocalMyFavoriteItemInfo another) {
		int result = this.addDate.compareTo(another.addDate);
		return -result;
	}
	
	public static class UserParamTag {
		public static final String LEVEL_TAG = "level";
	}
	
	public String formatUserParam() {
		String userParam = "";
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put(UserParamTag.LEVEL_TAG, level);
		} catch (JSONException e) {
			jsonObj = null;
			DKLog.e(TAG, "" + e);
		}
		
		if(jsonObj != null)
			userParam = jsonObj.toString();
		
		return userParam;
	}
	
	public boolean parseUserParam(String userParam) {
		if(Util.isEmpty(userParam)) {
			level = 0;
			return false;
		}
		
		boolean success = true;
		try {
			JSONObject jsonObj = new JSONObject(userParam);
			level = jsonObj.getInt(UserParamTag.LEVEL_TAG);
		} catch (JSONException e) {
			level = 0;
			DKLog.e(TAG, "" + e);
		}
		
		return success;
	}

} 


