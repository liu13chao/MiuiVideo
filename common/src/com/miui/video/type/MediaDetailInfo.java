/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaDetailInfo.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-19 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class MediaDetailInfo extends MediaInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String desc; // 影片描述
	public String smallposterurl;  //小海报url
	public String smallpostermd5;  //小海报md5
	
	public AppRecommandInfo[] recommend_miui; 

	public ImageUrlInfo getCoverUrlInfo(){
	    return new ImageUrlInfo(smallposterurl, smallpostermd5, null);
	}
}
