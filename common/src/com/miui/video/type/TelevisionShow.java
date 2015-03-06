package com.miui.video.type;

import java.io.Serializable;

public class TelevisionShow implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public int channelid;
	public int videostarttime;
	public int videoendtime;
	public String videoname;
	public String categoryname;
    public String channelname;
    public int epgid;
    public String cmccplayinfo;
}
