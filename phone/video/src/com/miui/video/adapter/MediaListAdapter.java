package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.media.MediaViewGrid;

/**
 *@author tangfuling
 *
 */
public abstract class MediaListAdapter extends BaseAdapter {

	public abstract int getNumColumns();
	public abstract int getItemViewRes();
	
	private Context mContext;
	private boolean mIsInEditMode;
	
	private List<List<? extends BaseMediaInfo>> mDivideList 
		= new ArrayList<List<? extends BaseMediaInfo>>();
	
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;
	
	public MediaListAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setInEditMode(boolean isInEditMode) {
		this.mIsInEditMode = isInEditMode;
		notifyDataSetChanged();
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		mOnItemLongClickListener = onItemLongClickListener;
	}
	
	public void setGroup(List<? extends BaseMediaInfo> list) {
		mDivideList.clear();
		if(list != null) {
			int listSize = list.size();
			int numColums = getNumColumns();
			int rows = (int) Math.ceil(listSize / (float)numColums);
			for(int i = 0; i < rows; i++) {
				int start = i * numColums;
				int end = (i + 1) * numColums;
				if(end > listSize) {
					end = (int) listSize;
				}
				List<? extends BaseMediaInfo> curRow = list.subList(start, end);
				mDivideList.add(curRow);
			}
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mDivideList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mDivideList.get(position);
	}
	
	private class ViewHolder {
		MediaViewGrid mediaViewGrid;
		View bottomMargin;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			View view = View.inflate(mContext, getItemViewRes(), null);
			viewHolder = new ViewHolder();
			viewHolder.mediaViewGrid = (MediaViewGrid) view.findViewById(R.id.media_list_item_grid);
			viewHolder.bottomMargin = view.findViewById(R.id.media_list_item_bottom_margin);
			viewHolder.mediaViewGrid.setOnItemClickListener(mOnItemClickListener);
			viewHolder.mediaViewGrid.setOnItemLongClickListener(mOnItemLongClickListener);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		@SuppressWarnings("unchecked")
		List<BaseMediaInfo> list = (List<BaseMediaInfo>) getItem(position);
		viewHolder.mediaViewGrid.setInEditMode(mIsInEditMode);
		viewHolder.mediaViewGrid.setGroup(list);
		int count = getCount();
		if(position == count - 1) {
			viewHolder.bottomMargin.setVisibility(View.VISIBLE);
		} else {
			viewHolder.bottomMargin.setVisibility(View.GONE);
		}
		if(count == 1) {
			convertView.setBackgroundResource(R.drawable.com_item_bg_full);
		} else {
			if(position == 0) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_up);
			} else if(position == count - 1) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_down);
			} else {
				convertView.setBackgroundResource(R.drawable.com_item_bg_mid);
			}
		}
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
