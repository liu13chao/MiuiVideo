package com.miui.video.statistic;

import java.util.ArrayList;

public class TvStaticInfoList extends StatisticInfo {
	
	private static final long serialVersionUID = 2L;
	private ArrayList<TvStaticInfo> tvStaticInfos = new ArrayList<TvStaticInfo>();
	
	public void addTvStaticInfo(TvStaticInfo tvStaticInfo) {
		if(tvStaticInfo == null) {
			return;
		}
		tvStaticInfos.add(tvStaticInfo);
	}

	@Override
	public String formatToJson() {
		StringBuilder jsonSb = new StringBuilder();
		for(int i = 0; i < tvStaticInfos.size(); i++) {
			jsonSb.append(tvStaticInfos.get(i).formatToJson());
		}
		return jsonSb.toString();
	}

}
