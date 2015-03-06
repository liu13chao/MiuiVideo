/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   UrlInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-22
 */

package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.util.StringUtils;

import android.text.TextUtils;

/**
 *@author xuanmingliu
 *
 */

public class ImageUrlInfo  implements Serializable{
	private static final long serialVersionUID = 2L;
	
	//image content md5
	public String md5; 
	//image url
	public String url;
	
	public String webpurl;

	public ImageUrlInfo(){
	}
	
	public ImageUrlInfo(String url, String md5, String webp){
        this.url = url;
        this.md5 = md5;
        this.webpurl = webp;
    }
	
	public String getImageUrl(){
	    if(TextUtils.isEmpty(webpurl)){
	        return StringUtils.avoidNull(url);
	    }
	    return webpurl;
	}
	
}
