/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   OpenMediaStatisticInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-18
 */

package com.miui.video.statistic;


import java.util.UUID;

import com.miui.video.util.DKLog;

/**
 *@author xuanmingliu
 *
 */

public class LivePlayStatisticInfo extends StatisticInfo{
	private static final long serialVersionUID = 2L;

	public static final String TAG = LivePlayStatisticInfo.class.getName();
	public int comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_START_LIVE;         //用户数据类型
	public UUID uuid;
	public boolean isAds;
	public long timestamp;
	public int source;
	public String tvid;
	public String videoidentifying;
	public int channelid;
	public String channelname;
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.COM_USER_DATA_TYPE_TAG, comUserDataType);
			jsonObject.put(StatisticTagDef.TV_CHANNELNAME_TAG, channelname);
			jsonObject.put(StatisticTagDef.TV_CHANNELID_TAG, channelid);
			jsonObject.put(StatisticTagDef.TV_VIDEOIDENTIFYING_TAG, videoidentifying);
			jsonObject.put(StatisticTagDef.TV_ID_TAG, tvid);
			jsonObject.put(StatisticTagDef.TV_SOURCE_TAG, source);
			jsonObject.put(StatisticTagDef.UUID_TAG, uuid);
			jsonObject.put(StatisticTagDef.TIMESTAMP_TAG, timestamp);
			jsonObject.put(StatisticTagDef.ISADS_TAG, isAds);
		} 
		catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}
}


