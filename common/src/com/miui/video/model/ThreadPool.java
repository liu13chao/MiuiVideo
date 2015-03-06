/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ThreadPool.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-28
 */

package com.miui.video.model;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tianli
 *
 */
public class ThreadPool {
	
	public static ThreadPoolExecutor newThreadPool(int size){
		return new ThreadPoolExecutor(0, size, 30, TimeUnit.SECONDS, 
	    		new LinkedBlockingDeque<Runnable>());
	}
}
