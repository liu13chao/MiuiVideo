package com.miui.video.widget.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.MyVideoItem;

/**
 *@author tangfuling
 *
 */
public class MyVideoView extends FrameLayout {

	private Context mContext;
	private View mContentView;
	
	//UI
	private TextView mVideoViewItem;
	private TextView mVideoViewName;
	
	private View mClickView;
	
	//data
	private MyVideoItem mMyVideoItem;
	
	private OnMyVideoClickListener mOnMyVideoClickListener;
	
	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MyVideoView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public void setItem(MyVideoItem myVideoItem) {
		this.mMyVideoItem = myVideoItem;
		refresh();
	}
	
	public void setOnMyVideoClickListener(OnMyVideoClickListener listener) {
		this.mOnMyVideoClickListener = listener;
	}

	//init
	private void init() {
		mContentView = View.inflate(mContext, R.layout.my_video_view, null);
		int width = mContext.getResources().getDimensionPixelSize(R.dimen.my_video_view_width);
		int height = mContext.getResources().getDimensionPixelSize(R.dimen.my_video_view_height);
		LayoutParams params = new LayoutParams(width, height);
		addView(mContentView, params);
		
		mClickView = mContentView.findViewById(R.id.my_video_click);
		mClickView.setOnClickListener(mOnClickListener);
		mVideoViewItem = (TextView) mContentView.findViewById(R.id.my_video_view_item);
		mVideoViewName = (TextView) mContentView.findViewById(R.id.my_video_view_name);
	}
	
	//packaged method
	private void refresh() {
		if(mMyVideoItem == null) {
			this.setVisibility(View.INVISIBLE);
		} else {
			this.setVisibility(View.VISIBLE);
			Drawable drawableTop = mContext.getResources().getDrawable(mMyVideoItem.itemIconResId);
			mVideoViewItem.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
			mVideoViewItem.setText(mMyVideoItem.mDesc);
			mVideoViewName.setText(mMyVideoItem.itemName);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mOnMyVideoClickListener != null) {
				mOnMyVideoClickListener.onMyVideoClick(MyVideoView.this, mMyVideoItem);
			}
		}
	};
	
	public interface OnMyVideoClickListener {
		public void onMyVideoClick(MyVideoView view, MyVideoItem myVideoItem);
	}
}
