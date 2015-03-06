/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ChannelRecomendationList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-21 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class ChannelRecommendationList implements Serializable {
	private static final long serialVersionUID = 2L;

	public ChannelRecommendation[] channelRecommendations;
	public long cacheTime;
}
