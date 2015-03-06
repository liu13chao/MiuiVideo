/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   GetRankListStatisticInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-20
 */

package com.miui.video.statistic;

import org.json.JSONException;
import org.json.JSONObject;

import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public class GetRankListStatisticInfo extends StatisticInfo{
	
	private static final long serialVersionUID = 2L;

	public static final String TAG = GetRankListStatisticInfo.class.getName();
	
	public String categoryId;
	public String filterId;
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try
		{
			JSONObject topListIdJsonObject = new JSONObject();
			topListIdJsonObject.put(StatisticTagDef.CATEGORYID_TAG, categoryId);
			topListIdJsonObject.put(StatisticTagDef.FILTERID_TAG, filterId);
			jsonObject.put(StatisticTagDef.TOPLISTID_TAG, topListIdJsonObject);
		} 
		catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
			
		return jsonObject.toString();
	}
}


