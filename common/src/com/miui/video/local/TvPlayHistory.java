/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  TvPlayHistory.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-25
 */
package com.miui.video.local;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;

/**
 * @author tianli
 *
 */
public class TvPlayHistory extends PlayHistory {

	private static final long serialVersionUID = 2L;
	
	public int mTvId;            //电视台id
	public int mTvBgColor;       //电视台背景颜色
	public String mTvPlayId;     //电视台播放id
	public String mTvName;       //电视台名字
	public String mChannelPoster;
	
	@Override
	public String getName() {
		return mTvName;
	}
	
	@Override
	public String getDesc() {
		return mTvName;
	}
	
	@Override
	public String getDescSouth() {
		return "";
	}
	@Override
	public String getDescSouthEast() {
		return "";
	}
	
	@Override
	public String getUrl() {
		return mChannelPoster;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TvPlayHistory){
			return mTvId == ((TvPlayHistory)o).mTvId;
		}
		return false;
	}
	
	@Override
	public BaseMediaInfo getPlayItem() {
		return null;
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
		return new ImageUrlInfo(mChannelPoster, "", null);
	}

    @Override
    public String getSubtitle() {
        // TODO Auto-generated method stub
        return null;
    }
}
