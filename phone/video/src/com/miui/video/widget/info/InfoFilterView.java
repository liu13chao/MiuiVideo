package com.miui.video.widget.info;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.Channel;

public class InfoFilterView extends LinearLayout {

	private Context mContext;
	
	private int mNumColumns;
	private Channel[] mItems;
	private List<List<Channel>> mDivideItems = new ArrayList<List<Channel>>();
	private List<TextView> mItemViews = new ArrayList<TextView>();
	private int mCurChannelId;
	
	private int mItemHeight;
	private int mTextSize;
	private int textColorNormal;
	private int textColrSelected;
	
	private List<OnFilterViewClickListener> mListeners = new ArrayList<OnFilterViewClickListener>();
	
	public InfoFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public InfoFilterView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void addListener(OnFilterViewClickListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(OnFilterViewClickListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	public void setCurChannelId(int channelId) {
		this.mCurChannelId = channelId;
		refreshItemViewColor();
	}
	
	public void setNumColumns(int numColumns) {
		this.mNumColumns = numColumns;
		divideItems();
		buildGridView();
	}
	
	public void setItems(Channel[] items) {
		this.mItems = items;
		divideItems();
		buildGridView();
	}
	
	//init
	private void init() {
		mTextSize = (int) mContext.getResources().getDimension(R.dimen.font_size_36);
		mItemHeight = (int) mContext.getResources().getDimension(R.dimen.info_filter_view_item_height);
		textColorNormal = mContext.getResources().getColor(R.color.p_50_black);
		textColrSelected = mContext.getResources().getColor(R.color.black);
	}

	//packaged method
	private void divideItems() {
		mDivideItems.clear();
		if(mItems != null && mNumColumns > 0) {
			int rows = (int) Math.ceil(mItems.length / (float)mNumColumns);
			for(int i = 0; i < rows; i++) {
				List<Channel> row = new ArrayList<Channel>();
				for(int j = 0; j < mNumColumns; j++) {
					int curIndex = i * mNumColumns + j;
					if(curIndex < mItems.length) {
						row.add(mItems[curIndex]);
					}
				}
				mDivideItems.add(row);
			}
		}
	}
	
	private void refreshItemViewColor() {
		for(int i = 0; i < mItemViews.size(); i++) {
			TextView textView = mItemViews.get(i);
			if(textView != null) {
				textView.setTextColor(textColorNormal);
				Object tag = textView.getTag();
				if(tag instanceof Integer) {
					int tagInt = (Integer) tag;
					if(tagInt == mCurChannelId) {
						textView.setTextColor(textColrSelected);
					}
				}
			}
		}
	}
	
	private void buildGridView() {
		if(mDivideItems.size() == 0) {
			return;
		}
		
		this.setOrientation(VERTICAL);
		mItemViews.clear();
		removeAllViews();
		addDividerViewH(this);
		for(int i = 0; i < mDivideItems.size(); i++) {
			List<Channel> items = mDivideItems.get(i);
			ViewGroup row = buildGridViewRow(items);
			if(row != null) {
				ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
				addView(row, params);
				addDividerViewH(this);
			}
		}
		refreshItemViewColor();
	}
	
	private ViewGroup buildGridViewRow(List<Channel> items) {
		if(items == null || items.size() == 0) {
			return null;
		}
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		addDividerViewV(linearLayout);
		for(int i = 0; i < mNumColumns; i++) {
			TextView textView = createTextView();
			if(i < items.size()) {
				Channel channel = items.get(i);
				if(channel != null) {
					textView.setText(channel.name);
					textView.setTag(channel.id);
				}
			}
			LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			params.weight = 1;
			linearLayout.addView(textView, params);
			mItemViews.add(textView);
			addDividerViewV(linearLayout);
		}
		return linearLayout;
	}
	
	private void addDividerViewH(ViewGroup parent) {
		if(parent == null) {
			return;
		}
		View dividerView = new View(mContext);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				mContext.getResources().getDimensionPixelSize(R.dimen.video_common_divider_height));
		dividerView.setBackgroundResource(R.drawable.com_10_black);
		parent.addView(dividerView, params);
	}
	
	private void addDividerViewV(ViewGroup parent) {
		if(parent == null) {
			return;
		}
		View dividerView = new View(mContext);
		ViewGroup.LayoutParams params = 
				new ViewGroup.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.video_common_divider_height), 
				ViewGroup.LayoutParams.MATCH_PARENT);
		dividerView.setBackgroundResource(R.drawable.com_10_black);
		parent.addView(dividerView, params);
	}
	
	private TextView createTextView() {
		TextView textView = new TextView(mContext);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundResource(R.drawable.transparent);
		textView.setOnClickListener(mOnClickListener);
		return textView;
	}
	
	private void notifyFilterViewClick(int channelId) {
		for(int i = 0; i < mListeners.size(); i++) {
			OnFilterViewClickListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onFilterViewClick(channelId);
			}
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			if(tag instanceof Integer) {
				int tagInt = (Integer) tag;
				mCurChannelId = tagInt;
				refreshItemViewColor();
				
				notifyFilterViewClick(mCurChannelId);
			}
		}
	};
	
	//self def class
	public interface OnFilterViewClickListener {
		public void onFilterViewClick(int channelId);
	}
}
