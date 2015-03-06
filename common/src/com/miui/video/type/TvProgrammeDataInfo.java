package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.util.DKLog;

public class TvProgrammeDataInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	
	private static final String TAG = TvProgrammeDataInfo.class.getName();
	
    public String category;
    public int  category_id;
    public String videoname;
    public String videoinfo;
    public int  hotindex;
    public int  episode;
    public String formatted_name;
    public int  channelid;
    public String videostarttime;
    public String videoendtime;
    
    public int  startTime;
    public int  endTime;

    public void completeData() {
    	try {
    		startTime = Integer.valueOf(videostarttime);
        	endTime = Integer.valueOf(videoendtime);
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
    }
}
