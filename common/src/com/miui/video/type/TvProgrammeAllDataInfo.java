package com.miui.video.type;

import java.io.Serializable;

public class TvProgrammeAllDataInfo implements Serializable {
	private static final long serialVersionUID = 3L;
	
    public String category;
    public int  categoryId;
    public String videoName;
    public String videoInfo;
    public int  hotIndex;
    public int  episode;
    public String formattedName;
    public int  channelId;
    public int  startTime;
    public int  endTime;
    
    //节目所在电台相关信息
    public char headLetter;
    public int  backgroundColor;
    public String mediaName;
    public String videoIdentiying;
    public String posterurl;  //海报url
    public String md5; //海报md5

}
