/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   SearchButtonTriggerPathInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-18
 */

package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public class SearchStatisticInfo extends StatisticInfo{

	private static final long serialVersionUID = 2L;

	public static final String TAG = SearchStatisticInfo.class.getName();
	
	public String searchKey;          //搜索关键字
	public String searchKeySource;    //关键字来源 ,取值为direct时，searchKeyPosition为0
	public int    searchKeyPosition;  //关键字位置
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try
		{
			jsonObject.put(StatisticTagDef.SEARCHKEY_TAG, searchKey);
			jsonObject.put(StatisticTagDef.FROM_TAG, searchKeySource);
			jsonObject.put(StatisticTagDef.SEARCHKEY_POSITION_TAG, searchKeyPosition);
		} 
		catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		return jsonObject.toString();
	}
	
}


