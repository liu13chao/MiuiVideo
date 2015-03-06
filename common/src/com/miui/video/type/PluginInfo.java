package com.miui.video.type;

import java.io.Serializable;

public class PluginInfo implements Serializable {

	/**
	 * version 1: url, md5
	 */
	private static final long serialVersionUID = 1L;

	public String url;
	public String md5;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("url: ").append(url).append(", ");
		sb.append("md5: ").append(md5).append(", ");
		return sb.toString();
	}
}
