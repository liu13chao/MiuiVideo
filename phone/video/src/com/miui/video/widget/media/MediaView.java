package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaView extends FrameLayout {
	
	private Context mContext;

	private TextView mTitlte;
	private TextView mSubTitle;
	
	protected MediaViewImage mPoster;
	protected BaseMediaInfo mBaseMediaInfo;
	
	public MediaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MediaView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setContentInfo(BaseMediaInfo baseMediaInfo) {
		mBaseMediaInfo = baseMediaInfo;
		refreshContentInfo();
	}
	
	public BaseMediaInfo getContentInfo() {
		return mBaseMediaInfo;
	}
	
	public void setInEditMode(boolean isInEditMode) {
		mPoster.setInEditMode(isInEditMode);
	}
	
	public boolean isSelected() {
		if(mBaseMediaInfo != null) {
			return mBaseMediaInfo.mIsSelected;
		}
		return false;
	}
	
	public void setShowSubTitle(boolean showSubTitle) {
		if(showSubTitle) {
			mSubTitle.setVisibility(View.VISIBLE);
		} else {
			mSubTitle.setVisibility(View.GONE);
		}
	}
	
	protected int getContentViewRes() {
		return -1;
	}
	
	protected void refreshContentInfo() {
		if(mBaseMediaInfo == null) {
			return;
		}
		mTitlte.setText(mBaseMediaInfo.getName());
		mSubTitle.setText(mBaseMediaInfo.getDesc());
		mPoster.setIsSelected(mBaseMediaInfo.mIsSelected);
		mPoster.setSouthText(mBaseMediaInfo.getDescSouth());
		mPoster.setSouthEastText(mBaseMediaInfo.getDescSouthEast());
		mPoster.setMediaInfo(mBaseMediaInfo);
	      //TODO: 
//		if(mBaseMediaInfo instanceof BaseDevice) {
//			mPoster.setPosterResource(R.drawable.poster_device);
//		} else {
//			mPoster.setPoster(mBaseMediaInfo.getPosterInfo());
//		}
	}
	
	//init
	private void init() {
		View view = View.inflate(mContext, getContentViewRes(), this);
		mPoster = (MediaViewImage) view.findViewById(R.id.media_view_poster);
		mTitlte = (TextView) view.findViewById(R.id.media_view_name);
		mSubTitle = (TextView) view.findViewById(R.id.media_view_status);
	}
}
