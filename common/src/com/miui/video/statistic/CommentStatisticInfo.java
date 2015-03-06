package com.miui.video.statistic;

import org.json.JSONException;

import com.miui.video.util.DKLog;

public class CommentStatisticInfo extends StatisticInfo {

	private static final String TAG = CommentStatisticInfo.class.getName();
	private static final long serialVersionUID = 2L;

	public int mediaId;  //视频id
	public int score;  //用户评分1~5
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			jsonObject.put(StatisticTagDef.MEDIAID_TAG, mediaId);
			jsonObject.put(StatisticTagDef.COMMENT_SCORE_TAG, score);
		} catch (JSONException e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}

}
