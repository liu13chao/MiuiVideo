/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   Recommendation.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-11 
 */
package com.miui.video.type;

import android.text.TextUtils;

/**
 * @author tianli
 * 
 */
public class Recommendation extends MediaInfo {

	private static final long serialVersionUID = 3L;
	public int midtype;
	public String desc;
//	public MediaInfo mediaInfo;

	public void completeData() {
		formatContent();
	}
	
	public void formatContent() {
		if (!TextUtils.isEmpty(desc)) {
			desc = desc.trim();
			desc.replaceAll("\\r\\n", "\n");
		}
	}
}
