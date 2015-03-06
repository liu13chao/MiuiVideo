/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   LocalPlayHistory.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-14
 */

package com.miui.video.type;

import com.miui.video.model.LocalMediaHideInfo;
import com.miui.video.util.Util;


/**
 *@author xuanmingliu
 *
 */

public class LocalPlayHistory extends BaseLocalPlayHistory{
	private static final long serialVersionUID = 2L;
	
	public int    mediaId;      //视频id   用来把历史纪录和我们的影片数据做对应
	public int    mediaCi;      //上次剧集播放到第几集
	public String playSeconds;  //上次视频播放到的时长, 对应于play_hitory.xml中的position,单位ms
	public int    mediaSource;  //历史纪录影片的播放源（搜狐或奇艺）
	public String videoName = "";    //片名
	public int    quality;      //清晰度 （这个可能不是必须）
	public String mediaUrl = "";     //播放地址
	public String html5Page = "";    //播放影片的html5页面
	public String playParameter = "";  //第3方需要的播放参数
	public int    mediaSetType;   //单集 、多集
	public int    inBox;        //0 盒子上没有源，1盒子上有源
	public String issueDate = "";    //综艺日期
	public String imageMd5 = "";   // 封面图片md5
	public String imageUrl = ""; //封面图片url
	public String bucketName = "";  
	
	public boolean localVideoHistory;    //是否是本地视频播放
	
	public LocalPlayHistory(){
		nType = PlayHistoryType.PLAYHISTORY_TYPE_MEDIA;
		
		mediaId = -1;
		mediaCi = -1;
		playSeconds = "0";
		playDate = "";
		mediaSource = -1;
		videoName = "";
		quality = -1;
		mediaUrl = "";
		html5Page = "";
		playParameter = "";
		mediaSetType = 0;
		inBox = 0;
		localVideoHistory = false;
		issueDate = "";
	}
	
	public boolean isMultiSetType()
	{
		if( mediaSetType == 0)
			return false;
			
	    return true;
	}
	
	public boolean isLocalHide() {
		boolean isBucketHide = false;
		boolean isLocalPathHide = false;
		LocalMediaHideInfo localMediaHideInfo = LocalMediaHideInfo.getInstance();
		if(localVideoHistory) {
			if(!Util.isEmpty(bucketName)) {
				isBucketHide = localMediaHideInfo.isLocalMediaHide(bucketName);
			}
			if(!Util.isEmpty(mediaUrl)) {
				String localPath = mediaUrl;
				if(localPath != null && localPath.startsWith("file://")) {
					localPath = localPath.substring(7);
				}
				isLocalPathHide = localMediaHideInfo.isLocalMediaHide(localPath);
			}
		}
		if(isLocalPathHide || isBucketHide) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "LocalPlayHistory [mediaId=" + mediaId + ", mediaCi=" + mediaCi
				+ ", playSeconds=" + playSeconds + ", playDate=" + playDate
				+ ", mediaSource=" + mediaSource + ", videoName=" + videoName
				+ ", quality=" + quality + ", mediaUrl=" + mediaUrl
				+ ", html5Page=" + html5Page + ", playParameter="
				+ playParameter + ", mediaSetType=" + mediaSetType + ", inBox="
				+ inBox + ", localVideoHistory=" + localVideoHistory + "]";
	}
}


