package com.miui.video.type;

import java.io.Serializable;

public class UsersNickNameInfoList implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String result;
	public String description;
	public UserNickNameInfo[] list;
	public int code;
}
