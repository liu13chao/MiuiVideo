package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewImageV extends MediaViewImage {

	private ImageView mPoster;
	private ImageView mStatus;
	
	private TextView mSouthText;
		
	public MediaViewImageV(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaViewImageV(Context context) {
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
	
//	@Override
//	public void setPosterResource(int resid) {
//		super.setPosterResource(resid);
//		mPoster.setImageResource(resid);
//	}
	
//	@Override
//	public void setPoster(BasePosterInfo basePosterInfo) {
//		super.setPoster(basePosterInfo);
//		if(basePosterInfo == null) {
//			return;
//		}
//		if(basePosterInfo instanceof ImageUrlInfo) {
//			ImageUrlInfo imageUrlInfo = (ImageUrlInfo) basePosterInfo;
//			setPoster(imageUrlInfo);
//		} else if(basePosterInfo instanceof ThumbnailTaskInfo) {
//			ThumbnailTaskInfo thumbnailTaskInfo = (ThumbnailTaskInfo) basePosterInfo;
//			setPoster(thumbnailTaskInfo);
//		}
//	}
	
//	public void setPoster(ImageUrlInfo imageUrlInfo) {
//		super.setPoster(imageUrlInfo);
//		if(imageUrlInfo == null) {
//			return;
//		}
//		if(!ImageManager.isUrlDone(imageUrlInfo, mPoster)) {
//			mPoster.setImageResource(R.drawable.transparent);
//			mImageManager.fetchImage(ImageManager.createTask(imageUrlInfo, null), mPoster);
//		}
//	}
	
//	public void setPoster(ThumbnailTaskInfo thumbnailTaskInfo) {
//		super.setPoster(thumbnailTaskInfo);
//		if(thumbnailTaskInfo == null) {
//			return;
//		}
//		if(mThumbnailManager.fetchThumbnail(thumbnailTaskInfo, mPoster, null, true) == false) {
//			mPoster.setImageResource(R.drawable.transparent);
//		}
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
		View view = View.inflate(mContext, R.layout.media_view_image_v, this);
		mPoster = (ImageView) view.findViewById(R.id.media_view_image_poster);
		mStatus = (ImageView) view.findViewById(R.id.media_view_image_status);
		mSouthText = (TextView) view.findViewById(R.id.media_view_image_south_text);
	}
}
