/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PlayHistory.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-12
 */

package com.miui.video.type;

import java.io.Serializable;

/**
 *@author xuanmingliu
 *
 */

public class PlayHistory implements Serializable {
    
    private static final long serialVersionUID = 2L;
    public MediaInfo   mediaInfo;    //只关心其中的视频id
	
	public int     mediaCi;       //视频第几集
	public int     mediaSource;   //视频源,上传必填
	public String  videoName;   //视频名字
	public float   percent;     //百分比 ,上传必填
	public float   curMoviePercent;   //当前剧集百分比
	public String  playDate;    //播放时间 ,上传必填
	public int     scoreCount;
	public float   score;   //评分
	public int     setNow;   //当前有效集数
	public int     duration = -1;   //上传可填 默认-1
	public int     mediaType;  //当前 media的类型
	public int     totalSet;   //总集数
	public long    playSeconds;   //播放时间，从开始的播放的秒算起
	public int     quality = -1;   //上传时填写，默认-1 影片清晰度
	public int     errorCode = -1;  //上传时填写，默认-1 影片播放失败原因
}