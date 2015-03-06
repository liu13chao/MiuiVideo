/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  Group.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-7
 */
package com.miui.video.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianli
 *
 */
public class Group<T>{
	
	protected List<T> mItems = new ArrayList<T>();
	
	public synchronized void add(T item){
		if(item != null && !mItems.contains(item)){
			mItems.add(item);
		}
	}
	
	public synchronized void remove(T item){
		if(item != null && mItems.contains(item)){
			mItems.remove(item);
		}
	}
}
