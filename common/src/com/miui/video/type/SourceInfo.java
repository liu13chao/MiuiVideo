/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 */

package com.miui.video.type;

import java.io.Serializable;

public class SourceInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	public int sourceid;
	public String name;
	public String posterurl;
	public String md5;
	public String selectedposterurl;
	public String selectedmd5;
	public ClientInfo client_info;
	public PluginInfo plugin_info;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("client_info: ").append(client_info).append(",");
		sb.append("plugin_info: ").append(plugin_info).append(",");
		return sb.toString();
	}
}