/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   RankInfo.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-26 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class RankInfo implements Serializable {
	private static final long serialVersionUID = 2L;

	public int id;    //频道id
	public String name;  //频道名称
	public int count;  //影片总数
	public MediaInfo[] data;

}
