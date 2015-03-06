/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   RecommendationList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-11 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class RecommendationList implements Serializable {
	private static final long serialVersionUID = 2L;

	public long cacheTime;
	public Recommendation[] recommendations;
}
