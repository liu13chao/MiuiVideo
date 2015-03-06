/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   SpecialSubject.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-22 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class SpecialSubject extends BaseMediaInfo implements Serializable {

	private static final long serialVersionUID = 2L;

	public int count;
	public String name;
	public String desc;
	public int id;  //channel id
	public String posterurl;
	public String md5;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDesc() {
		return desc;
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
		return new ImageUrlInfo(posterurl, md5, null);
	}

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
        return "";
    }
}
