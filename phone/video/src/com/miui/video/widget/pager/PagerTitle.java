/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PagerTitle.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-17 
 */
package com.miui.video.widget.pager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;


/**
 * @author tianli
 * 
 */

public class PagerTitle extends HorizontalScrollView {
	
	public static final String TAG = PagerTitle.class.getName();
	
	private Context mContext;
	
	private CharSequence[] mTitles;
	private List<TextView> mTitleViews = new ArrayList<TextView>();
	private LinearLayout mTitleContainer;
	
	private int mTextBgResId;
	private int mTextSize;
	private int mTextColorNormal;
	private int mTextColorSelected;
	private boolean mTextBold = false;
//	private int mTitlePadding;
	
//	private int mTitlePaddingLeft;
//	private int mTitlePadding;
	
	private OnTitleSelectedListener mListener;
	
//	private int textPadding;
//	private int titleIntervalH;
	
	private int mCurPage;
	
	public PagerTitle(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	protected void setOnTitleSelectedListener(OnTitleSelectedListener listener) {
		this.mListener = listener;
	}
	
	protected void setPagerTitleTextBold(boolean textBold) {
		this.mTextBold = textBold;
		refreshTextBold();
	}
	
	public void setTitlePadding(int padding){
	    mTitleContainer.setPadding(padding, 0, padding, 0);
	}
	
	protected void setTitle(CharSequence[] titles) {
		this.mTitles = titles;
		buildTitles();
	}
	
	private void buildTitles() {
	    if(mTitles == null || mTitles.length == 0) {
	        return;
	    }
	    mTitleViews.clear();
	    mTitleContainer.removeAllViews();
	    int textWidth = 0;
	    for(int i = 0; i < mTitles.length; i++) {
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
          CharSequence str = mTitles[i];
          TextView textView = createTextView();
          textView.setText(str);
          textView.setTag(i);
          mTitleViews.add(textView);
          mTitleContainer.addView(textView, params);
          textView.measure(0, 0);
          textWidth += textView.getMeasuredWidth();
	    }
	    int space = getResources().getDisplayMetrics().widthPixels - 
	            textWidth - mTitleContainer.getPaddingLeft() - mTitleContainer.getPaddingRight();
	    space /= mTitles.length + 1;
	    space = Math.max(space, getResources().getDimensionPixelSize(R.dimen.pager_title_min_space));
	    for(int i = 0; i < mTitleViews.size(); i++) {
	        View view = mTitleViews.get(i);
	        if(i == 0 && mTitleViews.size() == 1){
	               view.setPadding(space, 0, space, 0);
	        }else if( i == 0){
                view.setPadding(space, 0, space /2, 0);
	        }else if( i == mTitleViews.size() - 1){
                view.setPadding(space/2, 0, space, 0); 
	        }else{
                view.setPadding(space/2, 0, space/2, 0);  
	        }
	    }
	    refreshTextColor();
	}
	
	protected void setCurPage(int curPage) {
		this.mCurPage = curPage;
		View curView = mTitleContainer.getChildAt(curPage);
		if(curView != null){
			float viewLeftP = curView.getX() + mTitleContainer.getX();
			float viewRightP = viewLeftP + curView.getWidth();
			int scrollX = getScrollX();
			int width = getWidth();
			if(viewLeftP - scrollX >= 0 && 
					viewRightP <= width + scrollX){
				
			}else if(viewLeftP - scrollX < 0){
				int d = (int) (viewLeftP - scrollX);
				smoothScrollBy(d, 0);
			}else if(viewRightP > width + scrollX){
				int d = (int) (viewRightP +  - scrollX - width);
				smoothScrollBy(d, 0);
			}
		}
		refreshTextColor();
	}
	
	//init
	private void init() {
		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
//		this.setFadingEdgeLength(mContext.getResources()
//				.getDimensionPixelSize(R.dimen.pager_title_fade_edge));
		mTextBgResId = R.drawable.transparent;
		mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.font_size_42);
		mTextColorNormal = mContext.getResources().getColor(R.color.text_color_light_dark);
		mTextColorSelected = mContext.getResources().getColor(R.color.orange);
		mTitleContainer = new LinearLayout(mContext);
		mTitleContainer.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
		addView(mTitleContainer, params);
	}
	
//	//packaged method
//	private void buildPagerTitle() {
//		if(mTitles == null || mTitles.length == 0) {
//			return;
//		}
//		mTitleViews.clear();
//		mTitleContainer.removeAllViews();
//		for(int i = 0; i < mTitles.length; i++) {
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//			if(i == 0){
//				params.leftMargin = mTitlePadding;
//			}else if(i == mTitleViews.size() - 1){
//				params.leftMargin = titleIntervalH;
//				params.rightMargin = mTitlePadding;
//			}else if(i != 0) {
//				params.leftMargin = titleIntervalH;
//			}
//			CharSequence str = mTitles[i];
//			TextView textView = createTextView();
////			if(i == 0) {
////				textView.setPadding(titleLeftMargin, 0, textPadding, 0);
////			} else if(i == titles.length - 1) {
////				textView.setPadding(textPadding, 0, titleRightMargin, 0);
////			}
//			textView.setText(str);
//			textView.setTag(i);
//			mTitleViews.add(textView);
//			mTitleContainer.addView(textView, params);
//		}
//		refreshTextColor();
//	}
	
	private void refreshTextColor() {
		for(int i = 0; i < mTitleViews.size(); i++) {
			TextView textView = mTitleViews.get(i);
			Object tag = textView.getTag();
			if(tag instanceof Integer) {
				int tagInt = (Integer) tag;
				if(tagInt == mCurPage) {
					textView.setTextColor(mTextColorSelected);
				} else {
					textView.setTextColor(mTextColorNormal);
				}
			}
		}
	}
	
//	private void refreshTextParams() {
//		for(int i = 0; i < mTitleViews.size(); i++) {
//			TextView textView = mTitleViews.get(i);
//			if(textView != null) {
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
//				if(i == 0){
//					params.leftMargin = mTitlePadding;
//				}else if(i == mTitleViews.size() - 1){
//					params.leftMargin = titleIntervalH;
//					params.rightMargin = mTitlePadding;
//				}else if(i != 0) {
//					params.leftMargin = titleIntervalH;
//				}
//				textView.setLayoutParams(params);
//			}
//		}
//	}
	
	private void refreshTextBold() {
		for(int i = 0; i < mTitleViews.size(); i++) {
			TextView textView = mTitleViews.get(i);
			if(textView != null) {
				textView.getPaint().setFakeBoldText(mTextBold);
			}
		}
	}
	
	private TextView createTextView() {
		TextView textView = new TextView(mContext);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		textView.setBackgroundResource(mTextBgResId);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setOnClickListener(mOnClickListener);
		return textView;
	}
	
	private void notifyTitleSelected(int position) {
		if(mListener != null) {
			mListener.onTitleSelected(position);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			int position = (Integer) tag;
			notifyTitleSelected(position);
		}
	};
	
	//self def class
	public interface OnTitleSelectedListener {
		public void onTitleSelected(int position);
	}
}
