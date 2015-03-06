/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  MediaViewType.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-26
 */
package com.miui.video.controller;

import android.content.res.Resources;
import android.widget.ImageView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.thumbnail.ThumbnailHelper;
import com.miui.video.thumbnail.ThumbnailManager;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.Util;

/**
 *@author tangfuling
 *
 */
public class MediaViewHelper {
	
	public static final int MEDIA_CLASSIFY_TYPE_V = 0;
	public static final int MEDIA_CLASSIFY_TYPE_H = 1;
	public static final int MEDIA_CLASSIFY_TYPE_TV = 2;
	
	public static String getMediaStatus(MediaInfo mediaInfo) {
		StringBuilder status = new StringBuilder();
		Resources res = DKApp.getAppContext().getResources();
		if(mediaInfo.isMultiSetType()) {
			if(!Util.isEmpty(mediaInfo.lastissuedate)){
				String[] array = mediaInfo.lastissuedate.split("-");
				if(array.length >= 3){
				    status.append(mediaInfo.lastissuedate);
				}		
			} else if (mediaInfo.setnow == mediaInfo.setcount) {
				String str = res.getString(R.string.count_ji_quan);
				str = String.format(str, mediaInfo.setnow);
				status.append(str);
			} else {
				String str = "";
				str = res.getString(R.string.update_to_count_ji);
		    	str = String.format(str, mediaInfo.setnow);
			    status.append(str);
			}
		} else {
			if (mediaInfo.playlength > 0) {
				String hourStr = res.getString(R.string.minute);
				String minuteStr = res.getString(R.string.second);
				status.append(mediaInfo.playlength / 60);
				int second = mediaInfo.playlength % 60;
				status.append(hourStr);
				if (second == 0) {
					status.append("00");
				} else {
					if (second < 10)
						status.append("0");
					status.append(second);
				}
				status.append(minuteStr);
			}
		}
		return status.toString();
	}
	
	public static void setPoster(ImageView poster, ImageUrlInfo urlInfo, int defaultPoster){
	    if(poster != null && urlInfo != null){
	        if(!ImageManager.isUrlDone(urlInfo, poster)) {
	            poster.setImageResource(defaultPoster);
	            ImageManager.getInstance().fetchImage(ImageManager.createTask(urlInfo, null), poster);
	        }
	    }
	}
	
	public static void setThumbnail(ImageView poster, BaseMediaInfo mediaInfo, int defaultPoster){
        if(poster == null || mediaInfo == null){
            return;
        }
        if(!DKApp.getSingleton(ThumbnailManager.class).fetchThumbnail(ThumbnailHelper.
                generateThumbnailTaskInfo(mediaInfo), poster)){
            poster.setImageResource(defaultPoster);
        }
	}
}
