package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

public class BannerListStatisticInfo extends StatisticInfo {
	
	private static final String TAG = BannerListStatisticInfo.class.getName();
	private static final long serialVersionUID = 2L;
	
	public String cateogry;  //分类
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.BANNER_CATEGORY_TAG, cateogry);
		} catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}
}
