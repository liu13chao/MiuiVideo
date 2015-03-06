package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.media.MediaView;

/**
 *@author tangfuling
 *
 */
public abstract class MediaViewGridAdapter extends BaseGroupAdapter<BaseMediaInfo> {

	private boolean mIsInEditMode = false;
	private boolean mShowSubTitle = true;
	
	public abstract MediaView createMediaView();
	
	public MediaViewGridAdapter(Context context) {
		super(context);
	}
	
	public void setInEditMode(boolean isInEditMode) {
		this.mIsInEditMode = isInEditMode;
	}
	
	public void setShowSubTitle(boolean showSubTitle) {
		this.mShowSubTitle = showSubTitle;
	}
	
	private class ViewHolder {
		MediaView mediaView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			MediaView mediaView = createMediaView();
			viewHolder = new ViewHolder();
			viewHolder.mediaView = mediaView;
			convertView = mediaView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		BaseMediaInfo baseMediaInfo = getItem(position);
		viewHolder.mediaView.setShowSubTitle(mShowSubTitle);
		viewHolder.mediaView.setInEditMode(mIsInEditMode);
		viewHolder.mediaView.setContentInfo(baseMediaInfo);
		return convertView;
	}
}
