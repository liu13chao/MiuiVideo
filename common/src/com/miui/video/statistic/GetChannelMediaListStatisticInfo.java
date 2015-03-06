/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   GetChannelMediaListStatisticInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-20
 */

package com.miui.video.statistic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public class GetChannelMediaListStatisticInfo extends StatisticInfo{
	
	private static final long serialVersionUID = 2L;

	public static final String TAG = GetChannelMediaListStatisticInfo.class.getName();
	
	public String categoryId;
	public int startPosition;
	public int listType;
	
	public String [] filterTypes;
	public String [] filterValues;
	
	public void setFilter(String [] filterTypes, String [] filterValues)
	{
		if(filterTypes == null || filterValues == null
				|| filterTypes.length == 0 || filterValues.length == 0
				|| filterTypes.length != filterValues.length)
			return;

		this.filterTypes = filterTypes;
		this.filterValues = filterValues;
	}
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try
		{
			jsonObject.put(StatisticTagDef.CATEGORYID_TAG, categoryId);
			jsonObject.put(StatisticTagDef.CHANNEL_MEDIAINFOLIST_START_TAG, startPosition);
			jsonObject.put(StatisticTagDef.CHANNEL_MEDIAINFOLIST_TYPE_TAG,
					 ChannelMediaInfoListTypeDef.getMediaInfoListType(listType));
			
			if( listType == ChannelMediaInfoListTypeDef.LIST_HOT_TYPE_CODE
					|| listType == ChannelMediaInfoListTypeDef.LIST_NEWEST_TYPE_CODE)
			{
				jsonObject.put(StatisticTagDef.CHANNEL_MEDIAINFOLIST_FILTER_TAG, getChannelMediaListFilter());
			}
		} 
		catch (JSONException e) {
			DKLog.e(TAG, " formatToJson " + e.getLocalizedMessage());
		}
		
		return jsonObject.toString();
	}
	
	private JSONArray getChannelMediaListFilter()
	{
		JSONArray   filter = new JSONArray();
		JSONObject  filterElement = null;
		if(filterTypes != null && filterValues != null) {
			int count = filterTypes.length;
			for( int i = 0; i < count; i++)
			{
				try
				{
					filterElement = new JSONObject();
					filterElement.put(filterTypes[i], filterValues[i]);
					filter.put(i, filterElement);
				} 
				catch (JSONException e) {
					DKLog.e(TAG, " getChannelMediaListFilter " + e.getLocalizedMessage());
				}
			}	
		}
		return filter;
	}

}


