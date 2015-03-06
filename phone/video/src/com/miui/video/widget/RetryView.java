/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   RetryLoadingView.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-4-15
 */

package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author xuanmingliu
 *
 */

public class RetryView extends FrameLayout{

	public OnRetryLoadListener onRetryLoadListener;
	private TextView mTitle;
	public static int STYLE_NORMAL = 0;
	public static int STYLE_LIGHT = 1;
	private int style = STYLE_NORMAL;
	
	public interface OnRetryLoadListener {
		public void OnRetryLoad(View vClicked);
	}

	public RetryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RetryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RetryView(Context context) {
		super(context);
		init();
	}
	
	public RetryView(Context context, int style) {
		super(context);
		this.style = style;
		init();
	}
	
	private void init() {
		View vContent = null;
		if(style == STYLE_NORMAL) {
			vContent = View.inflate(getContext(), R.layout.reload_view_dark, null);
		} else if(style == STYLE_LIGHT) {
			vContent = View.inflate(getContext(), R.layout.reload_view_light, null);
		} else {
			vContent = View.inflate(getContext(), R.layout.reload_view_dark, null);
		}
		mTitle = (TextView) vContent.findViewById(R.id.retry_view_title);
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( onRetryLoadListener != null) {
					onRetryLoadListener.OnRetryLoad(v);
				}
			}
		});
		
		FrameLayout.LayoutParams ltParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 
				FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER;
		vContent.setLayoutParams(ltParams);
		addView(vContent);
	}	
	
	public void setTitle(int res){
		if(mTitle != null){
			mTitle.setText(res);
		}
	}
	
    public void setOnRetryLoadListener(OnRetryLoadListener onRetryLoadListener) {
    	this.onRetryLoadListener = onRetryLoadListener;
    }
}


