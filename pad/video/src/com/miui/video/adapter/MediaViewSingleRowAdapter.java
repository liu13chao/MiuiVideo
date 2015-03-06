package com.miui.video.adapter;

import java.util.List;
import com.miui.video.widget.media.MediaViewRow;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class MediaViewSingleRowAdapter extends BaseGroupAdapter<Object> {
	
	private int nameViewColor = -1;
	private int statusViewColor = -1;
	
	private int posterW = -1;
	private int posterH = -1;
	
	//flags
	private boolean showText = true;
	private boolean inEditMode = false;
	
	private OnMediaClickListener mOnMediaClickListener;
	private OnMediaLongClickListener mOnMediaLongClickListener;
	
	private List<Object> mSelectedMedias;
	
	public MediaViewSingleRowAdapter(Context context) {
		super(context);
	}
	
	public void setShowText(boolean showText) {
		this.showText = showText;
	}
	
	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
	}
	
	public void setGroup(List<Object> list, List<Object> selectedMedias) {
		this.mSelectedMedias = selectedMedias;
		super.setGroup(list);
	}
	
	public void setGroup(Object[] array, List<Object> selectedMedias) {
		this.mSelectedMedias = selectedMedias;
		super.setGroup(array);
	}

	public OnMediaClickListener getOnMediaClickListener() {
		return mOnMediaClickListener;
	}

	public void setOnMediaClickListener(
			OnMediaClickListener onMediaClickListener) {
		this.mOnMediaClickListener = onMediaClickListener;
	}
	
	public void setOnMediaLongClickListener(
			OnMediaLongClickListener onMediaLongClickListener) {
		this.mOnMediaLongClickListener = onMediaLongClickListener;
	}
	
	public void setInfoViewColor(int nameViewColor, int statusViewColor) {
		this.nameViewColor = nameViewColor;
		this.statusViewColor = statusViewColor;
	}
	
	public void setPosterSize(int posterW, int posterH) {
		this.posterW = posterW;
		this.posterH = posterH;
	}
	
	@Override
	public int getCount() {
		if(mGroup.size() > 0) {
			return 1;
		}
		return 0;
	}

	private class ViewHolder {
		MediaViewRow mediaViewRow;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			MediaViewRow mediaViewRow = new MediaViewRow(mContext);
			viewHolder.mediaViewRow = mediaViewRow;
			convertView = mediaViewRow;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mediaViewRow.setMediaViewContents(mGroup.toArray(), mSelectedMedias, inEditMode);
		viewHolder.mediaViewRow.setOnMediaClickListener(mOnMediaClickListener);
		viewHolder.mediaViewRow.setOnMediaLongClickListener(mOnMediaLongClickListener);
		viewHolder.mediaViewRow.setShowText(showText);
//		viewHolder.mediaViewRow.setInEditMode(inEditMode);
		
		if(nameViewColor != - 1 && statusViewColor != -1) {
			viewHolder.mediaViewRow.setInfoViewColor(nameViewColor, statusViewColor);
		}
		if(posterW != -1 && posterH != -1) {
			viewHolder.mediaViewRow.setMediaViewSize(posterW, posterH);
		}
		return convertView;
	}
}
