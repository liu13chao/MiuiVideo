/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  PlayHistory.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-22
 */
package com.miui.video.local;

import java.io.Serializable;

import android.text.TextUtils;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public abstract class PlayHistory extends BaseMediaInfo implements Serializable, Comparable<PlayHistory>{

	private static final long serialVersionUID = 2L;
	
	public static final int STATUS_ADDED = 0;
	public static final int STATUS_DELETED = 1;
	public static final int STATUS_SYNC = 2;
	
	protected int mStatus;
	
	public int    mediaId;             //视频id   用来把历史纪录和我们的影片数据做对应
//	public int    mediaCi;             //上次剧集播放到第几集
	public long   playPosition;        //上次视频播放到的时长, 对应于play_hitory.xml中的position,单位ms
	public long   duration;            //播放时长
//	public int    mediaSource;         //历史纪录影片的播放源（搜狐或奇艺）
//	public String videoName = "";      //片名
//	public int    quality;             //清晰度 （这个可能不是必须）
	public String mediaUrl = "";       //播放地址
//	public String html5Page = "";      //播放影片的html5页面
//	public String playParameter = "";  //第3方需要的播放参数
//	public int    mediaSetType;        //单集 、多集
	public long playDate;
//	public int    inBox;               //0 盒子上没有源，1盒子上有源
//	public String issueDate = "";      //综艺日期
//	public String imageMd5 = "";       //封面图片md5
//	public String imageUrl = "";       //封面图片url
//	public String bucketName = "";  

//	public Object getMedia() {
//		Object obj = null;
//		if(mediaId > 0) {
//			MediaInfo mediaInfo = new MediaInfo();
//			mediaInfo.mediaid = mediaId;
//			mediaInfo.medianame = videoName;
//			obj = mediaInfo;
//		} else {
//			LocalMedia localMedia = new LocalMedia();
//			localMedia.mediaPath = mediaUrl;
//			obj = localMedia;
//		}
//		return obj;
//	}
	
	public int playType;
	
	public abstract BaseMediaInfo getPlayItem();
	
	public String formatPlayDate() {
		return Util.msTime2Date(playDate);
	}

	@Override
	public String getUrl() {
		return mediaUrl;
	}
	
	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		this.mStatus = status;
	}

	public boolean isDeletedLocally() {
		return mStatus == STATUS_DELETED;
	}

	
	@Override
	public int compareTo(PlayHistory another) {
		assert(another != null);
		return -Long.valueOf(playDate).compareTo(Long.valueOf(another.playDate));
	}

	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof PlayHistory){
			PlayHistory history = (PlayHistory)o;
			if(!TextUtils.isEmpty(mediaUrl)){
				return mediaUrl.equals(history.mediaUrl);
			}
		}
//		if(o != null && o instanceof PlayHistory){
//			PlayHistory history = (PlayHistory)o;
//			if(history.mediaId > 0){
//				return history.mediaId == mediaId;
//			}else{
//				if(!TextUtils.isEmpty(history.html5Page)){
//					return history.html5Page.equals(html5Page);
//				}
//				if(!TextUtils.isEmpty(mMediaUrl)){
//					return mMediaUrl.equals(history.mMediaUrl);
//				}
//			}
//		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		if(mediaId > 0){
			return (mediaId + "").hashCode();
		}
//		if(!TextUtils.isEmpty(html5Page)){
//			html5Page.hashCode();
//		}
		if(!TextUtils.isEmpty(mediaUrl)){
			return mediaUrl.hashCode();
		}
		return super.hashCode();
	}
	
	public void updatePlayerHistory(PlayHistory history){
	    if(history != null){
	        playPosition = history.playPosition;
	        playDate = history.playDate;
	        duration = history.duration;
	    }
	}
    @Override
    public String getMediaStatus() {
        return "";
    }
    
    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public String getName() {
        BaseMediaInfo item = getPlayItem();
        if(item != null){
            return item.getName();
        }
        return "";
    }

}
