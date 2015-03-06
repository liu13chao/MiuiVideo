/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 */

package com.miui.video.type;

import java.io.Serializable;

public class AppStoreApkInfo implements Serializable {
	/**
	 * version 2: add client_info and plugin_info
	 */
	private static final long serialVersionUID = 2L;

	public int canupgrade;
	public String apkurl;
	public long apkversion;
	public String apkintro;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("apkurl: ").append(apkurl).append(",");
		sb.append("apkversion: ").append(apkversion).append(",");
		return sb.toString();
	}
}