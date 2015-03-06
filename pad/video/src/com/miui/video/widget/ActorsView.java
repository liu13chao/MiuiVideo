package com.miui.video.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 * @author tangfuling
 * 
 */

public class ActorsView extends LinearLayout {

	private static final int MAX_ACTORS_LINE = 2;
	private static final int MAX_ROW_ACTORS_COUNT = 4;
	private int mMaxActorsLine = MAX_ACTORS_LINE;

	private int mActorNamePaddingH;
	private int mActorNameSize;
	private int mActorNameColor;

	private static int ACTOR_VIEW_WIDTH = 0;

	private OnActorViewClickListener mOnActorViewClickListener;

	private List<ActorMetaInfo> mActorMetaInfoList;
	private List<LinearLayout> mActorRowLayoutList;

	private class ActorMetaInfo {
		public String actorName;
		public int nameWidth;
	}

	public ActorsView(Context context) {
		super(context);
		init(context);
	}

	public ActorsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ActorsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		Resources res = context.getResources();
		mActorNameColor = res.getColor(R.color.blue);
		mActorNameSize = res.getDimensionPixelSize(R.dimen.detail_summary_info_font_size);
		mActorNamePaddingH = res
				.getDimensionPixelSize(R.dimen.detail_actor_name_padding_h);
		mActorMetaInfoList = new ArrayList<ActorMetaInfo>();
		mActorRowLayoutList = new ArrayList<LinearLayout>();
		for (int i = 0; i < MAX_ACTORS_LINE; i++) {
			LinearLayout curRowLayout = new LinearLayout(context);
			curRowLayout.setOrientation(HORIZONTAL);
			LinearLayout.LayoutParams ltParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			curRowLayout.setLayoutParams(ltParams);

			for (int j = 0; j < MAX_ROW_ACTORS_COUNT; j++) {
				TextView actorView = new TextView(context);
				actorView.setFocusable(false);
				actorView.setFocusableInTouchMode(false);
				actorView.setOnClickListener(mOnClickListener);
				actorView.setOnTouchListener(mOnTouchListener);
				actorView.setEllipsize(TruncateAt.END);
				actorView.setGravity(Gravity.CENTER_HORIZONTAL);
				LinearLayout.LayoutParams actorLtParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				actorView.setLayoutParams(actorLtParams);
				actorView
						.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActorNameSize);
				actorView.setTextColor(mActorNameColor);
				curRowLayout.addView(actorView, j);
			}

			addView(curRowLayout, i);
			mActorRowLayoutList.add(curRowLayout);
		}

		setOrientation(VERTICAL);
		getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}

	public static void resetActorViewWidth() {
		ACTOR_VIEW_WIDTH = 0;
	}

	public void setActors(String actors) {
		mActorMetaInfoList.clear();
		TextView button = new TextView(getContext());
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActorNameSize);
		Paint paint = button.getPaint();
		if(actors != null) {
			String[] sActors = actors.split(" ");
			int actorCount = sActors.length;
			for (int i = 0; i < actorCount; i++) {
				if (TextUtils.isEmpty(sActors[i])) {
					continue;
				}
				ActorMetaInfo actorMetaInfo = new ActorMetaInfo();
				actorMetaInfo.actorName = sActors[i];
				actorMetaInfo.nameWidth = (int) paint.measureText(sActors[i]);
				mActorMetaInfoList.add(actorMetaInfo);
			}
		}

		if (ACTOR_VIEW_WIDTH != 0) {
			layoutActorViews();
		} else {
			bLayoutFinished = false;
			requestLayout();
		}
	}

	public interface OnActorViewClickListener {
		public void onActorViewClick(String actorName);
	}

	public void setOnActorViewClickListener(
			OnActorViewClickListener onActorViewClickListener) {
		this.mOnActorViewClickListener = onActorViewClickListener;
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v instanceof TextView) {
				if (ActorsView.this.mOnActorViewClickListener != null) {
					TextView actorNameView = (TextView) v;
					ActorsView.this.mOnActorViewClickListener.onActorViewClick(actorNameView.getText()
							.toString());
				}
			}
		}
	};
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(v instanceof TextView) {
				TextView actorNameView = (TextView) v;
				String actorName = actorNameView.getText().toString();
				int action = event.getAction();
				if(action == MotionEvent.ACTION_DOWN) {
					actorNameView.setText(Html.fromHtml("<u>" +actorName +"</u>"));
				} else if(action == MotionEvent.ACTION_UP) {
					actorNameView.setText(actorName);
				}
			}
			return false;
		}
	};

	private boolean bLayoutFinished = false;

	private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			if (bLayoutFinished)
				return;

			int pWidth = getWidth();
			ACTOR_VIEW_WIDTH = pWidth;
			layoutActorViews();
			bLayoutFinished = true;
		}
	};

	private void layoutActorViews() {
		for (int i = 0; i < MAX_ACTORS_LINE; i++) {
			LinearLayout curRowLayout = mActorRowLayoutList.get(i);
			for (int m = 0; m < MAX_ROW_ACTORS_COUNT; m++) {
				curRowLayout.getChildAt(m).setVisibility(View.GONE);
			}
			curRowLayout.setVisibility(View.GONE);
		}

		int count = mActorMetaInfoList.size();
		if (count == 0)
			return;

		int curRowWidth = 0;
		int curRowIndex = 0;
		int curRowActorCount = 0;
		LinearLayout curRowLayout = null;
		LinearLayout.LayoutParams actorLtParams = null;
		TextView actorView = null;
		for (int i = 0; i < count; i++) {
			if (curRowIndex >= mMaxActorsLine)
				break;
			ActorMetaInfo aMetaInfo = mActorMetaInfoList.get(i);
			int actorNameWidth = aMetaInfo.nameWidth + mActorNamePaddingH * 2;
			curRowWidth += actorNameWidth;
			if (curRowWidth > ACTOR_VIEW_WIDTH) {
				curRowWidth -= mActorNamePaddingH;
			}

			if (curRowWidth <= ACTOR_VIEW_WIDTH || curRowActorCount == 0) {
				if (curRowLayout == null) {
					curRowLayout = mActorRowLayoutList.get(curRowIndex);
					curRowLayout.setVisibility(View.VISIBLE);
				}
				actorView = (TextView) curRowLayout
						.getChildAt(curRowActorCount);
				if (actorView != null) {
					actorView.setVisibility(View.VISIBLE);
					actorView.setText(aMetaInfo.actorName);
					actorLtParams = (LayoutParams) actorView.getLayoutParams();
					actorLtParams.width = actorNameWidth;
					curRowActorCount++;
				}
			} else {
				curRowActorCount = 0;
				curRowWidth = 0;
				curRowIndex += 1;
				curRowLayout = null;
			}
		}
	}

	public void setActorViewClickable(boolean bEnable) {
		for (int i = 0; i < MAX_ACTORS_LINE; i++) {
			LinearLayout curRowLayout = mActorRowLayoutList.get(i);
			if (!curRowLayout.isShown())
				continue;

			for (int m = 0; m < MAX_ROW_ACTORS_COUNT; m++) {
				Button childView = (Button) curRowLayout.getChildAt(m);
				if (childView.isShown()) {
					childView.setEnabled(bEnable);
				}
			}
		}
	}
}
