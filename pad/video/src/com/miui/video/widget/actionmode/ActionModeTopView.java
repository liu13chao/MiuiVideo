package com.miui.video.widget.actionmode;

import com.miui.video.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class ActionModeTopView extends FrameLayout {
	
	private Context mContext;
	
	private Button mBtnCancel;
	private Button mBtnSelectAll;
	private TextView mTitlle;
	private boolean mIsLight;
		
	public ActionModeTopView(Context context, boolean isLight) {
		super(context);
		this.mContext = context;
		this.mIsLight = isLight;
		init();
	}
	
	public void setEdit(boolean isEdit) {
		if (isEdit) {
			mBtnCancel.setVisibility(View.VISIBLE);
			mTitlle.setVisibility(View.VISIBLE);
			setUiSelectPart();
		} else {
			mBtnCancel.setVisibility(View.INVISIBLE);
			mTitlle.setVisibility(View.INVISIBLE);
			setUiEdit();
		}
	}

	//UI状态与mSelectAll状态相反	
	public void setEnable(boolean isEnable) {
		if (isEnable) {
			mBtnSelectAll.setVisibility(View.VISIBLE);
		} else {
			mBtnSelectAll.setVisibility(View.INVISIBLE);
			mBtnCancel.setVisibility(View.INVISIBLE);
			mTitlle.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setUiEdit() {
		mBtnSelectAll.setText(R.string.edit);
	}
	
	public void setUISelectAll() {
		mBtnSelectAll.setText(R.string.select_all);
	}
	
	public void setUISelectNone() {
		mBtnSelectAll.setText(R.string.select_none);
	}
	
	public void setUiSelectPart() {
		mBtnSelectAll.setText(R.string.select_all);
	}
	
	protected void setTitle(String title) {
		mTitlle.setText(title);
	}
	
	protected Button getCancelBtn() {
		return mBtnCancel;
	}
	
	protected Button getSelecteAllBtn() {
		return mBtnSelectAll;
	}
	
	//init
	private void init() {
		initView();
		setEdit(false);
	}
	
	private void initView() {
		Resources res = getResources();
		
		mBtnCancel = new Button(mContext);
		mBtnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_13));
		if (mIsLight) {
			mBtnCancel.setTextColor(res.getColor(R.color.white));
			mBtnCancel.setBackgroundResource(R.drawable.btn_dark_bg);
		} else {
			mBtnCancel.setTextColor(res.getColor(R.color.p_60_black));
			mBtnCancel.setBackgroundResource(R.drawable.btn_light_bg);
		}
		mBtnCancel.setText(R.string.cancel);
		LayoutParams btnCancelParams = new LayoutParams(res.getDimensionPixelSize(R.dimen.action_mode_top_btn_width), 
				res.getDimensionPixelSize(R.dimen.action_mode_top_btn_height));
		btnCancelParams.gravity = Gravity.CENTER_VERTICAL;
//		btnCancelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		if (mIsLight) {
//			btnCancelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//			btnCancelParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_bottom_margin);
//		} else {
//			btnCancelParams.topMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_top_margin);			
//		}
		mBtnCancel.setLayoutParams(btnCancelParams);
		addView(mBtnCancel, btnCancelParams);
		
		mBtnSelectAll = new Button(mContext);
		mBtnSelectAll.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_13));

		if (mIsLight) {
			mBtnSelectAll.setTextColor(res.getColor(R.color.white));
			mBtnSelectAll.setBackgroundResource(R.drawable.btn_dark_bg);
		} else {
			mBtnSelectAll.setTextColor(res.getColor(R.color.p_60_black));
			mBtnSelectAll.setBackgroundResource(R.drawable.btn_light_bg);
		}
		mBtnSelectAll.setText(R.string.select_all);
		LayoutParams btnSelecteAllParams = new LayoutParams(res.getDimensionPixelSize(R.dimen.action_mode_top_btn_width), 
				res.getDimensionPixelSize(R.dimen.action_mode_top_btn_height));

		btnSelecteAllParams.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
//		btnSelecteAllParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		if (mIsLight) {
//			btnSelecteAllParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//			btnSelecteAllParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_bottom_margin);
//		} else {
//			btnSelecteAllParams.topMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_top_margin);			
//		}
		btnSelecteAllParams.rightMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_edge_margin);
		addView(mBtnSelectAll, btnSelecteAllParams);
		
		mTitlle = new TextView(mContext);
		mTitlle.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_13));
		if (mIsLight) {
			mTitlle.setTextColor(res.getColor(R.color.white));
		} else {
			mTitlle.setTextColor(res.getColor(R.color.p_90_black));			
		}
		LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		titleParams.gravity = Gravity.CENTER;
//		btnTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		btnTitleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		btnTitleParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_title_bottom_margin);
		addView(mTitlle, titleParams);
	}
	
	//UI callback
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}

