/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  BaseMediaInfo.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-26
 */
package com.miui.video.type;

import java.io.Serializable;



/**
 * @author tianli
 *
 */
public abstract class BaseMediaInfo implements Serializable{
	
    private static final long serialVersionUID = 1L;

    public boolean mIsSelected;
	
    public String getMediaStatus(){
        return "";
    }
    
    public String getSubtitle(){
        return "";
    }
    
	public String getName(){
	    return "";
	}
	
	public String getDesc(){
	    return "";
	}
	
	public String getDescSouth() {
		return "";
	}
	
	public String getDescSouthEast() {
		return "";
	}
	
	public String getUrl() {
		return "";
	}
	
	public ImageUrlInfo getPosterInfo() {
		return null;
	}
}
