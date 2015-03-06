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

public class OpenMediaStatisticInfo extends StatisticInfo{
	private static final long serialVersionUID = 2L;

	public static final String TAG = OpenMediaStatisticInfo.class.getName();
	public int comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_PLAY;         //用户数据类型
	public int mediaId = 0;             //视频id
	public int ci = 1;                  //视频当前集
	public String sourcePath;           //页面进入来源
	public int mediaSourceType;         //搜狐、奇艺
	public int videoType;         //长视频、短视频
	public boolean isFree = true;       //是否免费
	public String mediaSetType;            //单集，多集连载，多集完结
	
	public String lastEnterSource;      //进入详情页的sourcepath
	public String categoryId;           //分类：电视剧，电影
	public String filterId;               //港台，大陆，欧洲，美国，日韩
	public UUID uuid;
	public int playmode = UploadPositionDef.FROM_HTML5;
	public boolean isAds;
	public long timestamp;
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.COM_USER_DATA_TYPE_TAG, comUserDataType);
			jsonObject.put(StatisticTagDef.VIDEOID_TAG, mediaId);
			jsonObject.put(StatisticTagDef.MEDIACI_TAG, ci);
			jsonObject.put(StatisticTagDef.MEDIASETTYPE_TAG, mediaSetType);
			jsonObject.put(StatisticTagDef.ISFREE_TAG, MediaIsFreeDef.getMediaIsFree(isFree));
			jsonObject.put(StatisticTagDef.MEDIASOURCE_TAG, mediaSourceType);
			jsonObject.put(StatisticTagDef.FROM_TAG, sourcePath);
			jsonObject.put(StatisticTagDef.PLAYMODE_TAG, playmode);
			jsonObject.put(StatisticTagDef.VIDEOTYPE_TAG, videoType);
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


