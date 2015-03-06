/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaReview.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-22 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class MediaReview implements Serializable {

	private static final long serialVersionUID = 2L;

	public int score;  //评分
	public int choice; // 1 表示精选
	public String userid;  //用户id
	public String filmreview;  //评论内容
	public String createtime;  //评论创建时间
}
