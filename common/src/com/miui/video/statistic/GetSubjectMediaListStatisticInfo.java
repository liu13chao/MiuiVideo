/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   GetSubjectMediaListStatisticInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-20
 */

package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public class GetSubjectMediaListStatisticInfo extends StatisticInfo{
  
	private static final long serialVersionUID = 2L;

	public static final String TAG = GetSubjectMediaListStatisticInfo.class.getName();
	
	public String specialListId;
	public int position;
	//获取专题信息的来源
	public String sourcePath;
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try
		{
			jsonObject.put(StatisticTagDef.SOURCEPATH_TAG, sourcePath);
			jsonObject.put(StatisticTagDef.SPECIALLISTID_TAG, specialListId);
			jsonObject.put(StatisticTagDef.SUBJECT_POSITION_TAG, position);
		}
		catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		return jsonObject.toString();
	}

	
}


