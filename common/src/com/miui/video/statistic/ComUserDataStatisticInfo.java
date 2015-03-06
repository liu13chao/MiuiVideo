package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

public class ComUserDataStatisticInfo extends StatisticInfo {
	private static final String TAG = ComUserDataStatisticInfo.class.getName();
	private static final long serialVersionUID = 2L;
	
	public int comUserDataType;                //用户数据类型
	
	//搜索结果
	public boolean searchResultHit;            //搜索结果命中
	public String searchKey;                   //搜索关键字
	public String searchKeySource;    		   //关键字来源 ,取值为direct时，searchKeyPosition为0
	public int    searchKeyPosition;           //关键字位置
	
	//直播
	public String categoryId;                  //分类，如直播
	
	//离线
	public int mediaId = -1;                   //视频id
	public int ci = -1;                        //视频当前集
	public int mediaSource = -1;			   //视频源
	public String sourcePath = "";			   //入口,详情页面，下载选择页面
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.COM_USER_DATA_TYPE_TAG, comUserDataType);
			if(comUserDataType == ComUserDataTypeValueDef.COM_USER_DATA_TYPE_SEARCH_RESULT) {
				jsonObject.put(StatisticTagDef.SEARCHKEY_TAG, searchKey);
				jsonObject.put(StatisticTagDef.SEARCH_RESULT_HIT_TAG, searchResultHit);
				jsonObject.put(StatisticTagDef.COM_USER_DATA_TYPE_TAG, comUserDataType);
				jsonObject.put(StatisticTagDef.FROM_TAG, searchKeySource);
				jsonObject.put(StatisticTagDef.SEARCHKEY_POSITION_TAG, searchKeyPosition);
			} else if(comUserDataType == ComUserDataTypeValueDef.COM_USER_DATA_TYPE_LIVE) {
				jsonObject.put(StatisticTagDef.CATEGORYID_TAG, categoryId);
			} else if(comUserDataType == ComUserDataTypeValueDef.COM_USER_DATA_TYPE_OFFLINE) {
				jsonObject.put(StatisticTagDef.MEDIAID_TAG, mediaId);
				jsonObject.put(StatisticTagDef.MEDIACI_TAG, ci);
				jsonObject.put(StatisticTagDef.MEDIASOURCE_TAG, mediaSource);
				jsonObject.put(StatisticTagDef.FROM_TAG, sourcePath);
			} else if(comUserDataType == ComUserDataTypeValueDef.COM_USER_DATA_TYPE_RECOMAND_MIUI) {
				jsonObject.put(StatisticTagDef.MEDIAID_TAG, mediaId);
				jsonObject.put(StatisticTagDef.CATEGORYID_TAG, categoryId);
			}
		} catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}
}
