/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ChannelInfoList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-10 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class ChannelList implements Serializable {
	private static final long serialVersionUID = 2L;
	public Channel[] channels;
	public long cacheTime;
}
