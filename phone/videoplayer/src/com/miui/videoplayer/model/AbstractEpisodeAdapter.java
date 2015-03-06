/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AbstractEpisodeAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-9
 */

package com.miui.videoplayer.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * @author tianli
 *
 */
public abstract class AbstractEpisodeAdapter extends BaseAdapter{

	protected Context mContext;
	
	protected BaseUri mUri;
	
	public AbstractEpisodeAdapter(Context context){
		mContext = context;
	}
	
	public void setPlayingUri(BaseUri uri){
		mUri = uri;
	}
	
	public BaseUri getPlayingUri(){
		return mUri;
	}
	
	private List<Episode> mGroup = new ArrayList<Episode>();
	
	public void setGroup(List<Episode> list){
		if(list != null){
			mGroup = list;
		}
	}
	@Override
	public int getCount() {
		return mGroup.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroup.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public abstract int getMediaStyle();
}
