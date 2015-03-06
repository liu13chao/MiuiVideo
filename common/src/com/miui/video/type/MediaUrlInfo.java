/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaUrlInfo.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-31 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 *
 */
public class MediaUrlInfo implements Serializable {

	private static final long serialVersionUID = 2L;
	public String mediaUrl;
	public int clarity;
	public int mediaSource;
	public int startOffset;
	public int endOffset;
	public int isHtml;
	public boolean sdkdisable;
	public String sdkinfo2;
	
	public boolean isHtml() {
		return isHtml == 1;
	}
	
}
