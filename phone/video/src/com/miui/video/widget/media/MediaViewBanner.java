package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.api.ApiConfig;
import com.miui.video.type.Banner;
import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewBanner extends FrameLayout {

	private Context mContext;
	
	//UI
	private View mContentView;
//	private ImageView mPosterView;
//	private View mClickView;
	private MediaPosterView mPosterView;
	
	//data
	private Object mContentInfo;
	
	private OnBannerMediaClickListener mListener;
	
	public MediaViewBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MediaViewBanner(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public void setBanner(Object banner) {
		if(banner instanceof Banner) {
			Banner tmpBanner = (Banner) banner;
			switch(tmpBanner.midtype) {
			case ApiConfig.ID_TYPE_MEDIA:
				mContentInfo = tmpBanner.mediaInfo;
				break;
			case ApiConfig.ID_TYPE_SPECIALSUBJECT:
				mContentInfo = tmpBanner.specialSubjectInfo;
				break;
			case ApiConfig.ID_TYPE_ADDON:
				mContentInfo = tmpBanner.addonInfo;
				break;
			}
		} else {
			mContentInfo = banner;
		}
		refresh();
	}
	
	public void setOnBannerMediaClickListener(OnBannerMediaClickListener listener) {
		this.mListener = listener;
	}

	//init
	private void init() {
		mContentView = LayoutInflater.from(mContext).
		        inflate(R.layout.media_view_banner, this, false);
		addView(mContentView);
//		LayoutParams contentViewParams = new LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//		addView(mContentView, contentViewParams);
//		mPosterView = (ImageView) mContentView.findViewById(R.id.banner_media_poster);
//		mClickView = mContentView.findViewById(R.id.banner_media_click);
//		mClickView.setOnClickListener(mOnClickListener);
		mPosterView = (MediaPosterView)findViewById(R.id.poster);
		mPosterView.setPosterType(MediaPosterView.POSTER_TYPE_FULL_CORNER);
		mPosterView.setOnClickListener(mOnClickListener);
	}
	
	//packaged method
	private void refresh() {
		if(mContentInfo instanceof  BaseMediaInfo) {
		      mPosterView.setMediaInfo((BaseMediaInfo)mContentInfo);
		}
//		ImageUrlInfo imageUrlInfo = getContentImageUrlInfo();
//		if(!ImageManager.isUrlDone(imageUrlInfo, mPosterView)) {
//			mPosterView.setImageResource(R.drawable.transparent);
//			int radius = mContext.getResources().getDimensionPixelSize(R.dimen.video_common_radius_9);
//		     ImageManager.getInstance().fetchImage(ImageManager.createTask(imageUrlInfo, 
//		             new CornerBitmapFilter(radius)),  mPosterView);
//		}
	}
	
//	private ImageUrlInfo getContentImageUrlInfo() {
//		if(mContentInfo instanceof MediaInfo) {
//			return ((MediaInfo) mContentInfo).getPosterInfo();
//		} else if(mContentInfo instanceof SpecialSubject) {
//			return ((SpecialSubject) mContentInfo).getPosterInfo();
//		} else if(mContentInfo instanceof AddonInfo) {
//			return ((AddonInfo) mContentInfo).getPosterInfo();
//		} else if(mContentInfo instanceof TelevisionInfo) {
//			return ((TelevisionInfo) mContentInfo).getPosterInfo();
//		}
//		return null;
//	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mListener != null) {
				mListener.onBannerMediaClick(MediaViewBanner.this, mContentInfo);
			}
		}
	};
	
	public interface OnBannerMediaClickListener {
		public void onBannerMediaClick(MediaViewBanner view, Object contentInfo);
	}
}
