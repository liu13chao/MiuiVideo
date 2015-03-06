package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

public class MyFavoriteStatisticInfo extends StatisticInfo {

	private static final String TAG = MyFavoriteStatisticInfo.class.getName();
	private static final long serialVersionUID = 2L;
	
	public int mediaId;  //视频id
	public int action;  //1收藏, -1取消收藏
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.MEDIAID_TAG, mediaId);
			jsonObject.put(StatisticTagDef.MY_FAVORITE_ACTION_TAG, action);
		} catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}

}
