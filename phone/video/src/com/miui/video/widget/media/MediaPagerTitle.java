package com.miui.video.widget.media;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;

public class MediaPagerTitle extends LinearLayout {

	
	public static final String TAG = MediaPagerTitle.class.getName();
	
	private Context context;
	
	private List<String> titles = new ArrayList<String>();
	private List<TextView> titleViews = new ArrayList<TextView>();
	private List<View> dividerViews = new ArrayList<View>();
	
	private int textSize;
	private int textColorNormal;
	private int textColrSelected;
	
	private int textWidth;
	private int textHeight;
	
	private int curPage;
	
	private int maxPage = 3;
	
	private OnTitleSelectedListener listener;
	
	public MediaPagerTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public MediaPagerTitle(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	protected void setOnTitleSelectedListener(OnTitleSelectedListener listener) {
		this.listener = listener;
	}
	
	protected void setTitle(List<String> titles) {
		this.titles.clear();
		if(titles != null) {
			this.titles.addAll(titles);
		}
		buildPagerTitle();
	}
	
	protected void setCurPage(int curPage) {
		if(this.curPage == curPage) {
			return;
		}
		
		this.curPage = curPage;
		refreshCurPage();
	}
	
	//init
	private void init() {
		textSize = context.getResources().getDimensionPixelSize(R.dimen.font_size_42);
		textColorNormal = context.getResources().getColor(R.color.p_50_black);
		textColrSelected = context.getResources().getColor(R.color.orange);
		textWidth = context.getResources().getDimensionPixelSize(R.dimen.media_pager_title_text_width);
		textHeight = context.getResources().getDimensionPixelSize(R.dimen.media_pager_title_text_height);
	}
	
	//packaged method
	private void buildPagerTitle() {
		titleViews.clear();
		dividerViews.clear();
		removeAllViews();
		for(int i = 0; i < titles.size(); i++) {
			LayoutParams params = new LayoutParams(textWidth, textHeight);
			String str = titles.get(i);
			TextView textView = createTextView();
			textView.setGravity(Gravity.CENTER);
			textView.setText(str);
			textView.setTag(i);
			titleViews.add(textView);
			addView(textView, params);
			if(i != maxPage - 1) {
				int dividerWidth = context.getResources().getDimensionPixelSize(R.dimen.video_common_divider_width);
				LayoutParams dividerParams = new LayoutParams(dividerWidth, LayoutParams.MATCH_PARENT);
				View dividerView = new View(context);
				dividerView.setBackgroundResource(R.drawable.com_10_black);
				dividerView.setTag(i);
				dividerViews.add(dividerView);
				addView(dividerView, dividerParams);
			}
		}
		refreshCurPage();
	}
	
	private TextView createTextView() {
		TextView textView = new TextView(context);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		textView.setOnClickListener(mOnClickListener);
		return textView;
	}
	
	private void refreshCurPage() {
		for(int i = 0; i < titleViews.size(); i++) {
			TextView textView = titleViews.get(i);
			Object tag = textView.getTag();
			int position = (Integer) tag;
			if(curPage == position) {
				textView.setTextColor(textColrSelected);
			} else {
				textView.setTextColor(textColorNormal);
			}
		}
		
		for(int i = 0; i < dividerViews.size(); i++) {
			View view = dividerViews.get(i);
			Object tag = view.getTag();
			int position = (Integer) tag;
			if(position == curPage || position == curPage - 1) {
				view.setVisibility(View.INVISIBLE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void notifyTitleSelected(int position) {
		if(listener != null) {
			listener.onTitleSelected(position);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			int position = (Integer) tag;
			setCurPage(position);
			notifyTitleSelected(position);
		}
	};
	
	//self def class
	public interface OnTitleSelectedListener {
		public void onTitleSelected(int position);
	}
}
