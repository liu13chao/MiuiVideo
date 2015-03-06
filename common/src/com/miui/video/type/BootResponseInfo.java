/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 */

package com.miui.video.type;

import java.io.Serializable;

public class BootResponseInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	public SourceInfo[] sourceinfolist;
	public int sourceversion = -1;
	
	public SourceInfo getSourceInfo(int source) {
		if(sourceinfolist != null) {
			for(int i = 0; i < sourceinfolist.length; i++) {
				if(sourceinfolist[i] != null && sourceinfolist[i].sourceid == source) {
					return sourceinfolist[i];
				}
			}
		}
		return null;
	}
}