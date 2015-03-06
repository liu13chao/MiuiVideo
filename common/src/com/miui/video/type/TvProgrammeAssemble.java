package com.miui.video.type;

import java.io.Serializable;

public class TvProgrammeAssemble implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public String category;
	public int categoryid;
	public TvProgrammeCategory[] subcategory;
	public TvProgrammesAndDate[] programmes;
	public TelevisionInfo[] channelinfos;
}
