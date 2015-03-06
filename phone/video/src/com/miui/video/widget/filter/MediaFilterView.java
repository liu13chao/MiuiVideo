package com.miui.video.widget.filter;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;
import com.miui.video.widget.ListViewEx;

public class MediaFilterView extends LinearLayout {

	private Context mContext;
	
	private ListViewEx mListView;
	private MediaFilterAdapter mAdapter;
	
	private List<CategoryDetailInfo> mCategoryDetailInfos;
	private String mCurCategoryName;
	
	private OnFilterViewClickListener mListener;
	
	public MediaFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MediaFilterView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setCategoryDetailInfos(List<CategoryDetailInfo> categoryDetailInfos, 
			CategoryDetailInfo curCategoryDetailInfo) {
		this.mCategoryDetailInfos = categoryDetailInfos;
		if(curCategoryDetailInfo != null) {
			mCurCategoryName = curCategoryDetailInfo.categoryName;
		}
		refresh();
	}
	
	public void setOnFilterViewClickListener(OnFilterViewClickListener listener) {
		this.mListener = listener;
	}
	
	//init
	private void init() {
		setOrientation(VERTICAL);
		addDividerView();
		
		mListView = new ListViewEx(mContext);
		mAdapter = new MediaFilterAdapter(mContext);
		mListView.setSelector(R.drawable.com_list_selector_dialog_bg);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		addView(mListView);
	}
	
	//packaged method
	private void refresh() {
		mAdapter.setCurCategoryName(mCurCategoryName);
		mAdapter.setGroup(mCategoryDetailInfos);
	}
	
	private void addDividerView() {
		View dividerView = new View(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
				mContext.getResources().getDimensionPixelSize(R.dimen.video_common_divider_height));
		dividerView.setBackgroundResource(R.drawable.com_10_black);
		addView(dividerView, params);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(mListener != null) {
				CategoryDetailInfo categoryDetailInfo = mAdapter.getItem(position);
				mListener.onFilterViewClick(categoryDetailInfo);
			}
		}
	};
	
	//self def class
	public interface OnFilterViewClickListener {
		public void onFilterViewClick(CategoryDetailInfo categoryDetailInfo);
	}
}
