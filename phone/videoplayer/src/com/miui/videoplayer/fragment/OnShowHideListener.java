/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnShowHideListener.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-17
 */

package com.miui.videoplayer.fragment;

/**
 * @author tianli
 *
 */
public interface OnShowHideListener<T> {
	public void onShow(T t);
	public void onHide(T t);
}
