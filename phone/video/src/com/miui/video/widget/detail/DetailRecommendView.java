package com.miui.video.widget.detail;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.content.MediaInfoContentBuilder;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.recommend.MediaViewRows;

public class DetailRecommendView extends FrameLayout {

	private Context mContext;
	
	private View mDetailRecommendView;
//	private MediaViewGrid mMediaViewGrid;
	private MediaViewRows mMediaViewContainer;
	
	public DetailRecommendView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailRecommendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailRecommendView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	protected void setMediaViewContents(BaseMediaInfo[] mediaViewContents) {
	    mMediaViewContainer.setMediaContentBuilder(new MediaInfoContentBuilder(getContext()));
	    mMediaViewContainer.buildRows(mediaViewContents, 3, R.layout.media_view_grid_similar);
		if(mediaViewContents == null || mediaViewContents.length == 0) {
			this.setVisibility(View.GONE);
		} else {
			this.setVisibility(View.VISIBLE);
		}
	}
	
	//init
	private void init() {
		mDetailRecommendView = View.inflate(mContext, R.layout.detail_recommend, null);
		addView(mDetailRecommendView);
		mMediaViewContainer = (MediaViewRows) mDetailRecommendView.findViewById(R.id.detail_recommend_media);
		mMediaViewContainer.setMediaViewClickListener(mOnMediaClickListener);
		this.setVisibility(View.GONE);
	}
	
	private MediaViewClickListener mOnMediaClickListener = new MediaViewClickListener() {
        
        @Override
        public void onMediaLongClick(View view, BaseMediaInfo media) {
        }
        
        @Override
        public void onMediaClick(View view, BaseMediaInfo media) {
            if(media instanceof MediaInfo) {
                Intent intent = new Intent();
                intent.setClass(mContext, MediaDetailActivity.class);
                intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)media);
                intent.putExtra(MediaDetailActivity.KEY_IS_BANNER, false);
                intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_DETAIL_RECOMMEND_VALUE);
                mContext.startActivity(intent);
            } 
        }
    };
}
