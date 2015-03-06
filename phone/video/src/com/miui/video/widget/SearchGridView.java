package com.miui.video.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 * @author tangfuling
 * 
 */

public class SearchGridView extends LinearLayout {

	private static final int MAX_ROW = 5;
	private static final int MAX_COUNT_PER_ROW = 5;
	private int mMaxRow = MAX_ROW;
	
	private int mItemDefaultHeight;
	private int mItemDefaultIntervalH;
	private int mItemDefaultIntervalV;
	private int mTextDefaultPaddingH;

	private int mTextSize;
	private int mTextColor;

	private int ITEM_WIDTH = 0;

	private OnGridItemClickListener mOnItemClickListener;

	private List<ItemMetaInfo> mItemMetaInfoList;
	private List<LinearLayout> mRowLayoutList;

	private class ItemMetaInfo {
		public String name;
		public int nameWidth;
	}

	public SearchGridView(Context context) {
		super(context);
		init(context);
	}

	public SearchGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SearchGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		initDimen(context);
		initUI(context);
	}
	
	private void initDimen(Context context) {
		Resources res = context.getResources();
		mTextColor = res.getColor(R.color.p_90_black);
		mTextSize = res.getDimensionPixelSize(R.dimen.font_size_42);
		mItemDefaultHeight = res.getDimensionPixelSize(R.dimen.grid_item_default_height);
		mItemDefaultIntervalH = res.getDimensionPixelSize(R.dimen.grid_item_default_intervalH);
		mItemDefaultIntervalV = res.getDimensionPixelSize(R.dimen.grid_item_default_intervalV);
		mTextDefaultPaddingH = res.getDimensionPixelSize(R.dimen.grid_text_default_paddingH);
	}
	
	private void initUI(Context context) {
		mItemMetaInfoList = new ArrayList<ItemMetaInfo>();
		mRowLayoutList = new ArrayList<LinearLayout>();
		for (int i = 0; i < MAX_ROW; i++) {
			LinearLayout curRowLayout = new LinearLayout(context);
			curRowLayout.setOrientation(HORIZONTAL);
			LinearLayout.LayoutParams ltParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			if(i != 0) {
				ltParams.topMargin = mItemDefaultIntervalV;
			}
			curRowLayout.setLayoutParams(ltParams);

			for (int j = 0; j < MAX_COUNT_PER_ROW; j++) {
				TextView itemView = new TextView(context);
				itemView.setFocusable(false);
				itemView.setFocusableInTouchMode(false);
				itemView.setOnClickListener(mOnClickListener);
				itemView.setEllipsize(TruncateAt.END);
				itemView.setGravity(Gravity.CENTER);
				itemView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
				itemView.setTextColor(mTextColor);
				itemView.setBackgroundResource(R.drawable.com_btn_bg);
				
				LinearLayout.LayoutParams itemLtParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, mItemDefaultHeight);
				if(j != 0) {
					itemLtParams.leftMargin = mItemDefaultIntervalV;
				}
				itemView.setLayoutParams(itemLtParams);
				curRowLayout.addView(itemView, j);
			}

			addView(curRowLayout, i);
			mRowLayoutList.add(curRowLayout);
		}

		setOrientation(VERTICAL);
		getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}

	public void setItems(List<String> items) {
		mItemMetaInfoList.clear();
		if(items != null) {
			TextView button = new TextView(getContext());
			button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
			Paint paint = button.getPaint();
			int itemCount = items.size();
			for (int i = 0; i < itemCount; i++) {
				String str = items.get(i);
				if (TextUtils.isEmpty(str)) {
					continue;
				}
				ItemMetaInfo itemMetaInfo = new ItemMetaInfo();
				itemMetaInfo.name = str;
				itemMetaInfo.nameWidth = (int) paint.measureText(str);
				mItemMetaInfoList.add(itemMetaInfo);
			}
		}

		if (ITEM_WIDTH != 0) {
			layoutItemViews();
		} else {
			bLayoutFinished = false;
			requestLayout();
		}
	}

	public interface OnGridItemClickListener {
		public void onGridItemClick(String itemName);
	}

	public void setOnGridItemClickListener(OnGridItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v instanceof TextView) {
				if (mOnItemClickListener != null) {
					TextView itemNameView = (TextView) v;
					mOnItemClickListener.onGridItemClick(itemNameView.getText()
							.toString());
				}
			}
		}
	};

	private boolean bLayoutFinished = false;

	private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			if (bLayoutFinished)
				return;

			int pWidth = getWidth();
			ITEM_WIDTH = pWidth;
			layoutItemViews();
			bLayoutFinished = true;
		}
	};

	private void layoutItemViews() {
		for (int i = 0; i < MAX_ROW; i++) {
			LinearLayout curRowLayout = mRowLayoutList.get(i);
			for (int m = 0; m < MAX_COUNT_PER_ROW; m++) {
				curRowLayout.getChildAt(m).setVisibility(View.GONE);
			}
			curRowLayout.setVisibility(View.GONE);
		}

		int count = mItemMetaInfoList.size();
		if (count == 0)
			return;

		int curRowWidth = 0;
		int curRowIndex = 0;
		int curRowItemCount = 0;
		LinearLayout curRowLayout = null;
		LinearLayout.LayoutParams itemLtParams = null;
		TextView itemView = null;
		for (int i = 0; i < count; i++) {
			if (curRowIndex >= mMaxRow)
				break;
			ItemMetaInfo aMetaInfo = mItemMetaInfoList.get(i);
			int itemNameWidth = aMetaInfo.nameWidth + mTextDefaultPaddingH * 2;
			curRowWidth = curRowWidth + itemNameWidth + mItemDefaultIntervalH;
			if (curRowWidth <= ITEM_WIDTH || curRowItemCount == 0) {
				if (curRowLayout == null) {
					curRowLayout = mRowLayoutList.get(curRowIndex);
					curRowLayout.setVisibility(View.VISIBLE);
				}
				itemView = (TextView) curRowLayout
						.getChildAt(curRowItemCount);
				if (itemView != null) {
					itemView.setVisibility(View.VISIBLE);
					itemView.setText(aMetaInfo.name);
					itemLtParams = (LayoutParams) itemView.getLayoutParams();
					itemLtParams.width = itemNameWidth;
					curRowItemCount++;
				}
			} else {
				curRowItemCount = 0;
				curRowWidth = 0;
				curRowIndex += 1;
				curRowLayout = null;
			}
		}
	}
}
