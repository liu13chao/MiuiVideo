package com.miui.video.type;

import java.io.Serializable;

public class TvChannelInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	
    public String szHeadLetter;
    public int  nHotIndex;
    public int  nChannelId;
    public ImageUrlInfo poster;
    public int  nBackgroundColor;
    public String szChannelName;
    public String szVideoId;
}
