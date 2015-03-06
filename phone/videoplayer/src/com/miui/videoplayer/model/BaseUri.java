/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseUri.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-30
 */

package com.miui.videoplayer.model;

import android.net.Uri;

/**
 * @author tianli
 *
 */
public abstract class BaseUri {
	public abstract Uri getUri();
	public abstract String getTitle();
	public abstract int getCi();
}
