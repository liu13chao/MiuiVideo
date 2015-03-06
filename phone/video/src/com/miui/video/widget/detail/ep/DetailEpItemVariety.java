package com.miui.video.widget.detail.ep;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;

public class DetailEpItemVariety extends RelativeLayout {

	private Context mContext;
//	private View mContentView;
	
	//UI
	private View mPoster;
	private TextView mData;
	private TextView mName;
	
	private int mColorNormal;
	private int mColorSelected;
	
	//data
	private SetInfoStatusVariety mItem;
	
	public DetailEpItemVariety(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public DetailEpItemVariety(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public SetInfoStatusVariety getData() {
		return mItem;
	}
	
	public void setData(SetInfoStatusVariety item) {
		this.mItem = item;
		refresh();
	}

	//init
	private void init() {

	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mColorNormal = mContext.getResources().getColor(R.color.p_80_black);
        mColorSelected = mContext.getResources().getColor(R.color.orange);
        
//        mContentView = View.inflate(mContext, R.layout.detail_ep_item_variety, null);
//        mContentView.setBackgroundResource(R.drawable.media_click_bg);
//        int height = mContext.getResources().getDimensionPixelSize(R.dimen.detail_variety_item_height);
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
//        addView(mContentView, params);
        
        mPoster = findViewById(R.id.detail_variety_item_poster);
        mData = (TextView) findViewById(R.id.detail_variety_item_data);
        mName = (TextView) findViewById(R.id.detail_variety_item_name);
    }

    //packaged method
	private void refresh() {
		if(mItem == null) {
			return;
		}
		mData.setText(mItem.date);
		mName.setText(mItem.videoName);
		
		boolean selected = mItem.isSelected;
		mPoster.setSelected(selected);
		if(selected) {
			mName.setTextColor(mColorSelected);
			mData.setTextColor(mColorSelected);
		} else {
			mName.setTextColor(mColorNormal);
			mData.setTextColor(mColorNormal);
		}
	}
}
