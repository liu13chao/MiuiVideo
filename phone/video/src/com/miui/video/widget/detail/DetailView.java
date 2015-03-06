package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.local.PlayHistory;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.detail.ObserverScrollView.OnScrollChangedListener;

public class DetailView extends FrameLayout {

	public static final String TAG = DetailView.class.getName();
	
	private Context mContext;
	
	//UI
	private View mContentView;
	private View mDetailViewContent;
	private View mDetailViewLoad;
	private RetryView mDetailViewRetry;
	
	private DetailPosterView mPosterView;

	private DetailScrollView mScrollView;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data from net
	private MediaDetailInfo2 mMediaDetailInfo2;
	private MediaDetailInfo mMediaDetailInfo;
	private BaseMediaInfo[] mRecommendations;
	
	//data from local
	private PlayHistory mPlayHistory;
	
	public DetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void playCurCi() {
		mScrollView.playCurCi();
	}
	
	public int getCurCi() {
		return mScrollView.getCurCi();
	}
	
	public void setOnRetryLoadListener(OnRetryLoadListener onRetryLoadListener) {
		mDetailViewRetry.setOnRetryLoadListener(onRetryLoadListener);
	}
	
	public void setPlayHistory(PlayHistory playHistory) {
		this.mPlayHistory = playHistory;
		refreshScrollView();
	}
	
	public void setRecommend(BaseMediaInfo[] recommendations) {
		this.mRecommendations = recommendations;
		refreshRecommend();
	}
	
	public void setData(MediaDetailInfo2 mediaDetailInfo2) {
	    mMediaDetailInfo2 = mediaDetailInfo2;
	    if(mMediaDetailInfo2 != null) {
	        mMediaInfo = mMediaDetailInfo2.mediainfo;
	        mMediaDetailInfo = mMediaDetailInfo2.mediainfo;
	        refresh();
	    }
	}
	
	public void setPreferenceSource(int source){
		mScrollView.setPreferenceSource(source);
	}
	
	public void showLoadingView() {
		mDetailViewLoad.setVisibility(View.VISIBLE);
		mDetailViewContent.setVisibility(View.INVISIBLE);
		mDetailViewRetry.setVisibility(View.INVISIBLE);
	}
	
	public void showRetryView() {
		mDetailViewRetry.setVisibility(View.VISIBLE);
		mDetailViewLoad.setVisibility(View.INVISIBLE);
		mDetailViewContent.setVisibility(View.INVISIBLE);
	}
	
	public void showContentView() {
		mDetailViewContent.setVisibility(View.VISIBLE);
		mDetailViewRetry.setVisibility(View.INVISIBLE);
		mDetailViewLoad.setVisibility(View.INVISIBLE);
	}
	
	//init
	private void init() {
		initUi();
	}
	
	private void initUi() {
		mContentView = View.inflate(mContext, R.layout.detail_view, null);
		addView(mContentView);
		
		mDetailViewContent = mContentView.findViewById(R.id.detail_view_content);
		mDetailViewLoad = mContentView.findViewById(R.id.detail_view_load);
		mDetailViewRetry = (RetryView) mContentView.findViewById(R.id.detail_view_retry);
		
		mPosterView = (DetailPosterView) mContentView.findViewById(R.id.detail_poster_view);
		mScrollView = (DetailScrollView) mContentView.findViewById(R.id.detail_scroll_view);
		mScrollView.setOnScrollChangedListener(mOnScrollChangedListener);
	}
	
	//packaged method
	private void refresh() {
		refreshPosterView();
		refreshScrollView();
	}
	
	private void refreshPosterView() {
		if(mMediaInfo != null) {
			mPosterView.setImageUrlInfo(mMediaInfo.getPosterInfo());
		}
	}
	
	private void refreshScrollView() {
		if(mMediaDetailInfo != null) {
			mScrollView.setIntroduce(mMediaDetailInfo.desc);
		}
		mScrollView.setData(mMediaInfo, mMediaDetailInfo2);
		mScrollView.setPlayHistory(mPlayHistory);
	}
	
	private void refreshRecommend() {
		mScrollView.setMediaViewContents(mRecommendations);
	}
	
	private OnScrollChangedListener mOnScrollChangedListener = new OnScrollChangedListener() {
		
		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			int posterViewTranslateSpeed = 2;
			int scrollViewTopPadding = mScrollView.getTopPadding();
			float scrollY = t;
			if(scrollY < 0) {
				scrollY = 0;
			} else if(scrollY > scrollViewTopPadding) {
				scrollY = scrollViewTopPadding;
			}
			float alpha = 1 - scrollY / scrollViewTopPadding;
			mPosterView.setPosterAlpha(alpha);
			mPosterView.setTranslationY(scrollViewTopPadding / posterViewTranslateSpeed * (alpha - 1));
		}
	};
}
