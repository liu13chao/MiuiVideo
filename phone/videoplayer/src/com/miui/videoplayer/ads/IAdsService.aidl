package com.miui.videoplayer.ads;

interface IAdsService {
    String getOnlineAd(int mediaId, int ci,  int source);
    String getTvAd(int tvId, int source, String tvProgram);
     void logAdPlay(String adBean, String extraJson);
     void logAdClick(String adBean, String extraJson);
	 void logAdSkipped(String adBean,  int playTime, String extraJson);
     void logAdFinished(String adBean, String extraJson);
}
