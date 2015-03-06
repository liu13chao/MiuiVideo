/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  ChannelInfo.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-8-9
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class Category implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String name;
	public int channeltype;  // 0 - long , 1 - information
	public int totalcount;
	public int id;
	public ImageUrlInfo poster;
	public ImageUrlInfo icon;
	public String[] namelist;
}
