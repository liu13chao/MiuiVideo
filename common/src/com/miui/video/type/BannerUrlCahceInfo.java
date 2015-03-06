/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   BannerUrlInfoList.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-4-12
 */

package com.miui.video.type;

import java.io.Serializable;

/**
 *@author xuanmingliu
 *
 */

public class BannerUrlCahceInfo implements Serializable{

	private static final long serialVersionUID = 2L;
  
    public long cacheTime;
    public int mediaId;
    public ImageUrlInfo imageUrlInfo;
}