package com.miui.video.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;
import com.miui.video.widget.media.MediaViewRow;

/**
 * @author tangfuling
 * 
 */

public class MediaViewListAdapter extends BaseGroupAdapter<Object> {
	
	private int mSizePerRow;
	
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
	
	public MediaViewListAdapter(Context context) {
		super(context);
	}
	
	public void setShowText(boolean showText) {
		this.showText = showText;
	}
	
	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
		refresh();
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
		refreshSizePerRow();
		int rows = (int) Math.ceil(mGroup.size() / (float) mSizePerRow);
		return rows;
	}

	private class ViewHolder {
		MediaViewRow mediaViewRow;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(mContext, R.layout.media_view_list_item, null);
			viewHolder.mediaViewRow = (MediaViewRow) view.findViewById(R.id.media_view_list_item_row);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Object[] mediaContentInfos = null;
		if(mSizePerRow > 0) {
			mediaContentInfos = new Object[mSizePerRow];
			for(int i = 0; i < mSizePerRow; i++) {
				int currentIndex = mSizePerRow * position + i;
				if(currentIndex < mGroup.size()) {
					mediaContentInfos[i] = mGroup.get(currentIndex);
				}
			}
		}

		viewHolder.mediaViewRow.setMediaViewContents(mediaContentInfos, mSelectedMedias, inEditMode);
		viewHolder.mediaViewRow.setOnMediaClickListener(mOnMediaClickListener);
		viewHolder.mediaViewRow.setOnMediaLongClickListener(mOnMediaLongClickListener);
		viewHolder.mediaViewRow.setShowText(showText);
		
		if(nameViewColor != - 1 && statusViewColor != -1) {
			viewHolder.mediaViewRow.setInfoViewColor(nameViewColor, statusViewColor);
		}
		if(posterW != -1 && posterH != -1) {
			viewHolder.mediaViewRow.setMediaViewSize(posterW, posterH);
		}
		return convertView;
	}
	
	private void refreshSizePerRow() {
		Object obj = null;
		if(mGroup.size() > 0) {
			obj = mGroup.get(0);
		}
		mSizePerRow = MediaViewHelper.getSizePerRow(obj);
	}
}
