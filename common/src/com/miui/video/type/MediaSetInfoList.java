/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaSetInfoList.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-11-30
 */

package com.miui.video.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.miui.video.api.def.MediaConstantsDef;

/**
 *@author xuanmingliu
 *
 */

public class MediaSetInfoList implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public int style;
	public MediaSetInfo[]  videos;
	
	public List<MediaSetInfo> getAvailableCiList() {
		List<MediaSetInfo> mediaAvailableCiList = new ArrayList<MediaSetInfo>();
		if( videos == null)
			return mediaAvailableCiList;
		
		int count = videos.length;
		for(int i = 0; i < count; i++) {
			mediaAvailableCiList.add(videos[i]);
		}	
		return mediaAvailableCiList;
	}
	
	public int getPlayLength(int ci) {
		if (videos != null) {
			for (MediaSetInfo info : videos) {
				if (info != null && info.ci == ci) {
					return info.playlength;
				}
			}
		}
		return 0;
	}
	
	public boolean isVariety() {
		if(style == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
			return true;
		}
		return false;
	}
}