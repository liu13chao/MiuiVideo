package com.miui.video.widget.media;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.miui.video.R;
import com.miui.video.adapter.MediaViewGridHAdapter;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.GridViewEx;

/**
 *@author tangfuling
 *
 */
public class MediaViewGridH extends MediaViewGrid {

	private Context mContext;
	private GridView mGridView;
	private MediaViewGridHAdapter mAdapter;
	
	private int NUM_COLUMNS = 2;
	
	public MediaViewGridH(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaViewGridH(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	@Override
	public void setGroup(List<BaseMediaInfo> group) {
		super.setGroup(group);
		mAdapter.setGroup(group);
	}
	
	@Override
	public void setGroup(BaseMediaInfo[] group) {
		super.setGroup(group);
		mAdapter.setGroup(group);
	}
	
	@Override
	public void setInEditMode(boolean isInEditMode) {
		super.setInEditMode(isInEditMode);
		mAdapter.setInEditMode(isInEditMode);
	}
	
	@Override
	public void setShowSubTitle(boolean showSubTitle) {
		super.setShowSubTitle(showSubTitle);
		mAdapter.setShowSubTitle(showSubTitle);
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		super.setOnItemClickListener(onItemClickListener);
		mGridView.setOnItemClickListener(onItemClickListener);
	}
	
	@Override
	public void setOnItemLongClickListener(
			OnItemLongClickListener onItemLongClickListener) {
		super.setOnItemLongClickListener(onItemLongClickListener);
		mGridView.setOnItemLongClickListener(onItemLongClickListener);
	}

	//init
	private void init() {
		mGridView = new GridViewEx(mContext);
		addView(mGridView);
		
		mAdapter = new MediaViewGridHAdapter(mContext);
		mGridView.setSelector(R.drawable.transparent);
		mGridView.setVerticalScrollBarEnabled(false);
		mGridView.setNumColumns(NUM_COLUMNS);
		mGridView.setVerticalSpacing(mContext.getResources().getDimensionPixelSize(R.dimen.video_common_interval_24));
		mGridView.setHorizontalSpacing(mContext.getResources().getDimensionPixelSize(R.dimen.video_common_interval_30));
		mGridView.setAdapter(mAdapter);
	}
}
