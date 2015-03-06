package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewImageTv extends MediaViewImage {

	private ImageView mBorder;
	private ImageView mPoster;
	private ImageView mStatus;
	
	public MediaViewImageTv(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaViewImageTv(Context context) {
		super(context);
		mContext = context;
		init();
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
	public void setBorderResource(int resid) {
		super.setBorderResource(resid);
		mBorder.setBackgroundResource(resid);
	}
	
//	@Override
//	public void setPosterResource(int resid) {
//		super.setPosterResource(resid);
//		mPoster.setImageResource(resid);
//	}

	@Override
    public void setMediaInfo(BaseMediaInfo baseMediaInfo) {
        super.setMediaInfo(baseMediaInfo);
        if(mMediaInfo != null){
            // TODO: default poster
            MediaViewHelper.setPoster(mPoster, mMediaInfo.getPosterInfo(), R.drawable.transparent);
        }
    }

    //init
	private void init() {
		View view = View.inflate(mContext, R.layout.media_view_image_tv, this);
		mBorder = (ImageView) view.findViewById(R.id.media_view_image_border);
		mPoster = (ImageView) view.findViewById(R.id.media_view_image_poster);
		mStatus = (ImageView) view.findViewById(R.id.media_view_image_status);
	}
}
