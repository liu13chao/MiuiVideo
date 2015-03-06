/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   CheckSyncResult.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-11-16
 */

package com.miui.video.type;

/**
 *@author xuanmingliu
 *
 */

public class CheckSyncResult {
	public boolean toSync;
	public int result;
	
	public CheckSyncResult(boolean toSync, int syncResult)
	{
		this.toSync = toSync;
		this.result = syncResult;
	}
}


