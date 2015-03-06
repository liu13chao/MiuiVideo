package com.miui.video.type;

import java.io.Serializable;

public class ClientInfo implements Serializable {

	/**
	 * version 1: url, md5, is_download
	 */
	private static final long serialVersionUID = 1L;

	public String url;
	public String md5;
	public int is_download;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("url: ").append(url).append(", ");
		sb.append("md5: ").append(md5).append(", ");
		sb.append("is_download: ").append(is_download).append(", ");
		return sb.toString();
	}
}
