/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   BannerRecommendationList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-9-17 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 *
 */
public class BannerList implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String[] searchKeyWords;

	public Banner[] banners;
	public TelevisionInfo[] tvInfos;
}
