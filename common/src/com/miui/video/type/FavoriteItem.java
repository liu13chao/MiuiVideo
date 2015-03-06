/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MyFavoriteItemInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-29
 */

package com.miui.video.type;

import java.io.Serializable;

/**
 *@author xuanmingliu
 *
 */

public class FavoriteItem implements Serializable {
    
	private static final long serialVersionUID = 2L;
	
	public MediaInfo mediaInfo;
	public long utime;  //create time
	
}