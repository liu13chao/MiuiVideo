/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   SyncResultInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-31
 */

package com.miui.video.type;

import java.io.Serializable;

/**
 *@author xuanmingliu
 *
 */

public class SyncResultInfo implements Serializable{
	private static final long serialVersionUID = 2L;
	
	public boolean bAnonymousAccount = true;
	
	public long lastSyncTime;
	public boolean  lastSyncSuccess;
	public String xiaomiAccountName;
}


