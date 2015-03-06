/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaUrlInfoList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-31 
 */
package com.miui.video.type;

/**
 * @author tianli
 *
 */
public class MediaUrlInfoList {
	public static final int RESOLUTION_NORMAL = 0;
	public static final int RESOLUTION_HIGH = 1;
	public static final int RESOLUTION_SUPER = 2;
	public static final int RESOLUTION_COUNT = 3;

	public String videoName;
	public MediaUrlInfo[] urlNormal;
	public MediaUrlInfo[] urlHigh;
	public MediaUrlInfo[] urlSuper;
}
