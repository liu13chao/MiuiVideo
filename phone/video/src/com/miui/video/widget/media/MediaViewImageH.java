package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;

/**
 *@author tangfuling
 *
 */
public class MediaViewImageH extends MediaViewImage {

	private ImageView mPoster;
	private ImageView mStatus;
	
	private TextView mSouthText;
	private TextView mSouthEastText;
	
	public MediaViewImageH(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaViewImageH(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	@Override
	public void setSouthText(String text) {
		super.setSouthText(text);
		mSouthText.setText(text);
	}
	
	@Override
	public void setSouthEastText(String text) {
		super.setSouthEastText(text);
		mSouthEastText.setText(text);
		if(Util.isEmpty(text)) {
			mSouthEastText.setVisibility(View.INVISIBLE);
		} else {
			mSouthEastText.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void setInEditMode(boolean isInEditMode) {
		super.setInEditMode(isInEditMode);
		if(isInEditMode) {
			mStatus.setVisibility(View.VISIBLE);
		} else {
			mStatus.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void setIsSelected(boolean isSelected) {
		super.setIsSelected(isSelected);
		mStatus.setSelected(isSelected);
	}
	
	@Override
	public void setMediaInfo(BaseMediaInfo baseMediaInfo) {
	    super.setMediaInfo(baseMediaInfo);
	    if(mMediaInfo != null){
	        if(mMediaInfo.getPosterInfo() != null){
	            MediaViewHelper.setPoster(mPoster, mMediaInfo.getPosterInfo(), 
	                    R.drawable.transparent);
	        }else{
	            MediaViewHelper.setThumbnail(mPoster, mMediaInfo, R.drawable.transparent);
	        }

	    }
	}

	//init
	private void init() {
		View view = View.inflate(mContext, R.layout.media_view_image_h, this);
		mPoster = (ImageView) view.findViewById(R.id.media_view_image_poster);
		mStatus = (ImageView) view.findViewById(R.id.media_view_image_status);
		mSouthText = (TextView) view.findViewById(R.id.media_view_image_south_text);
		mSouthEastText = (TextView) view.findViewById(R.id.media_view_image_south_east_text);
	}
}
