/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   TaskFractory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-9-2
 */

package com.miui.videoplayer.playservice;

import com.miui.videoplayer.model.MediaConfig;

/**
 * @author tianli
 *
 */
public class TaskFractory {
	
	public static PlayTask createTask(int source){
		if(source == MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE){
			return new PPTVPlayTask();
		}
		return null;
	}
}
