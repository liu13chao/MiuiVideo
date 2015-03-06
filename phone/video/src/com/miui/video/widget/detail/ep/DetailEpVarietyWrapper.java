package com.miui.video.widget.detail.ep;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class DetailEpVarietyWrapper extends LinearLayout {

	private Context mContext;
	
	private List<SetInfoStatusVariety> mSetInfoStatusVarietys = new ArrayList<SetInfoStatusVariety>();
	
	private OnClickListener mOnClickListener;
	
	public DetailEpVarietyWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public DetailEpVarietyWrapper(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setData(List<SetInfoStatusVariety> setInfoStatusVarietys) {
		mSetInfoStatusVarietys.clear();
		mSetInfoStatusVarietys.addAll(setInfoStatusVarietys);
		refresh();
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		this.mOnClickListener = l;
	}
	
	//init
	private void init() {
		this.setOrientation(VERTICAL);
	}
	
	//packaged method
	private void refresh() {
		removeAllViews();
		if(mSetInfoStatusVarietys.size() == 0) {
			this.setVisibility(View.GONE);
		} else {
			this.setVisibility(View.VISIBLE);
			for(int i = 0; i < mSetInfoStatusVarietys.size(); i++) {
				SetInfoStatusVariety setInfoStatusVariety = mSetInfoStatusVarietys.get(i);
				DetailEpItemVariety detailEpItemVariety = (DetailEpItemVariety)LayoutInflater.from(mContext).
				        inflate(R.layout.detail_ep_item_variety, this, false);
				detailEpItemVariety.setData(setInfoStatusVariety);
				detailEpItemVariety.setOnClickListener(mOnClickListener);
				addView(detailEpItemVariety);
				if(i == 0){
				    detailEpItemVariety.setBackgroundResource(R.drawable.com_bg_white_corner_t);
				}else{
                    detailEpItemVariety.setBackgroundResource(R.drawable.com_bg_white_corner_v_m);
				}
			}
		}
	}
}
