package com.miui.video.statistic;

import com.miui.video.util.DKLog;

public class TvStaticInfo extends StatisticInfo {
	private static final long serialVersionUID = 2L;
	public static final String TAG = TvStaticInfo.class.getName();
	
	public int tvId = -1;  //电台id 
	public String entry = "";  //入口
	public int source = 0;
	public double adStartTime = 0;
	public double adEndTime = 0;
	public double adDuration = 0;
	public double startTime = 0;    // start time
	public double endTime = 0;    // end time
    public double loadingStartTime = 0;  // loading start time
    public double playTime = 0;  // the time of onPrepared
	public int bufferCount = 0;  // buffering times
	
	@Override
	public String formatToJson() {
		super.formatToJson();
		try {
			if(tvId > 0){
                jsonObject.put(StatisticTagDef.TV_PLAY_TIME_TAG, playTime);
                jsonObject.put(StatisticTagDef.TV_SOURCE_TAG, source);
			    jsonObject.put(StatisticTagDef.TV_ID_TAG, tvId);
			    jsonObject.put(StatisticTagDef.TV_ENTRY_TAG, entry);
			    jsonObject.put(StatisticTagDef.TV_START_TIME_TAG, startTime);
			    jsonObject.put(StatisticTagDef.TV_END_TIME_TAG, endTime);
	            jsonObject.put(StatisticTagDef.TV_AD_START_TIME_TAG, adStartTime);
	            jsonObject.put(StatisticTagDef.TV_AD_END_TIME_TAG, adEndTime);
	            jsonObject.put(StatisticTagDef.TV_AD_DURATION_TAG, adDuration);
			    if(loadingStartTime > 0 && loadingStartTime > adStartTime){
			        if(playTime > loadingStartTime){
			            jsonObject.put(StatisticTagDef.TV_LOADING_TIME_TAG, playTime - loadingStartTime);
			        }else{
			            jsonObject.put(StatisticTagDef.TV_LOADING_TIME_TAG, endTime - loadingStartTime);
			        }
			    }else{
                    jsonObject.put(StatisticTagDef.TV_LOADING_TIME_TAG, 0);
			    }
			    jsonObject.put(StatisticTagDef.TV_BUFFER_COUNT_TAG, bufferCount);
			}
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		return jsonObject.toString();
	}
}
