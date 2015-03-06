/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   TopStatusBar.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-25
 */

package com.miui.videoplayer.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.fragment.VideoProxy;


/**
 * @author tianli
 *
 */
public class StatusBar extends RelativeLayout {
	
	private TextView mTitleView;
	private TextView mSubTitleView;
	private View mBackView;
	private VideoProxy mVideoProxy;
	private FrameLayout mCustomView;
	
	public StatusBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public StatusBar(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void updateStatus(CharSequence title, CharSequence subTitle){
		mTitleView.setText(title);
		mSubTitleView.setText(subTitle);
	}
	
	private void init() {
	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.vp_top_title);
        mSubTitleView = (TextView) findViewById(R.id.vp_top_sub_title);
        mBackView = findViewById(R.id.vp_top_back);
        mBackView.setOnClickListener(mOnClickListener);
        mCustomView = (FrameLayout)findViewById(R.id.custom_view);
    }
	
	public void addCustomView(View view){
	    mCustomView.addView(view);
	}
	
    public void attachVideoProxy(VideoProxy mVideoProxy) {
        this.mVideoProxy = mVideoProxy;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == mBackView) {
			    if(mVideoProxy != null){
			        mVideoProxy.exitPlayer();
			    }
			}
		}
	};
}
