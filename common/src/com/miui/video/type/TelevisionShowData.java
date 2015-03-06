package com.miui.video.type;

import java.io.Serializable;

public class TelevisionShowData implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String date;
	public TelevisionShow[] data; //每天的节目信息
}
