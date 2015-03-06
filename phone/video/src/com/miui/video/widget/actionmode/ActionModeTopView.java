package com.miui.video.widget.actionmode;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class ActionModeTopView extends RelativeLayout {
	
	private Context mContext;
	
	private Button mBtnCancel;
	private Button mBtnSelectAll;
	private TextView mTitlle;
	
	public ActionModeTopView(Context context) {
		super(context);
		this.mContext = context;
		
		init();
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
	}
	
	private void initView() {
		Resources res = getResources();
		int paddingH = res.getDimensionPixelSize(R.dimen.video_common_title_top_btn_paddingH);
		int paddingV = res.getDimensionPixelSize(R.dimen.video_common_title_top_btn_paddingV);
		
		mBtnCancel = new Button(mContext);
		mBtnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_29));
		mBtnCancel.setTextColor(res.getColor(R.color.white));
		mBtnCancel.setText(R.string.cancel);
		mBtnCancel.setBackgroundResource(R.drawable.btn_top_action_mode_bg);
		mBtnCancel.setPadding(paddingH, paddingV, paddingH, paddingV);
		LayoutParams btnCancelParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		btnCancelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnCancelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnCancelParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_bottom_margin);
		btnCancelParams.leftMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_edge_margin);
		mBtnCancel.setLayoutParams(btnCancelParams);
		addView(mBtnCancel, btnCancelParams);
		
		mBtnSelectAll = new Button(mContext);
		mBtnSelectAll.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_29));
		mBtnSelectAll.setTextColor(res.getColor(R.color.white));
		mBtnSelectAll.setText(R.string.select_all);
		mBtnSelectAll.setBackgroundResource(R.drawable.btn_top_action_mode_bg);
		mBtnSelectAll.setPadding(paddingH, paddingV, paddingH, paddingV);
		LayoutParams btnSelecteAllParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		btnSelecteAllParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnSelecteAllParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnSelecteAllParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_bottom_margin);
		btnSelecteAllParams.rightMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_btn_edge_margin);
		addView(mBtnSelectAll, btnSelecteAllParams);
		
		mTitlle = new TextView(mContext);
		mTitlle.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.font_size_29));
		mTitlle.setTextColor(res.getColor(R.color.white));
		LayoutParams btnTitleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		btnTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btnTitleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnTitleParams.bottomMargin = res.getDimensionPixelSize(R.dimen.action_mode_top_title_bottom_margin);
		addView(mTitlle, btnTitleParams);
	}
	
	//UI callback
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}

