/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LivePlayInfo.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年9月30日
 */
package com.miui.video.live;

/**
 * @author tianli
 *
 */
public class LivePlayInfo {
    
    private int mTvId;
    private String mTvPrograme;
    private String mTvChannelName;
    private int mSource;
    
    public int getSource() {
        return mSource;
    }
    
    public void setSource(int source) {
        this.mSource = source;
    }
    public int getTvId() {
        return mTvId;
    }
    public void setTvId(int tvId) {
        this.mTvId = tvId;
    }
    
    public String getTvChannelName() {
        return mTvChannelName;
    }
    
    public void setTvChannelName(String channelName) {
        this.mTvChannelName = channelName;
    }
    public String getTvPrograme() {
        return mTvPrograme;
    }
    public void setTvPrograme(String tvPrograme) {
        this.mTvPrograme = tvPrograme;
    }
    
    
}
