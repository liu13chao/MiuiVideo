package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;
import com.miui.video.local.PlayHistory;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.detail.ep.DetailEpView;

public class DetailScrollView extends ObserverScrollView {

	private Context mContext;
	
//	private View mContentView;
	private DetailInfoView mInfoView;
	private DetailEpView mEpView;
	private DetailIntroduceView mIntroduceView;
	private DetailCommentView mCommentView;
	private DetailRecommendView mRecommendView;
	
	private int mScrollViewTopPadding;
	
	public DetailScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
//		init();
	}

	public DetailScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
//		init();
	}

	public DetailScrollView(Context context) {
		super(context);
		this.mContext = context;
//		init();
	}
	
	protected int getTopPadding() {
		return mScrollViewTopPadding;
	}
	
	protected int getCurCi() {
		return mEpView.getCurCi();
	}
	
	protected void playCurCi() {
		mEpView.playCurCi();
	}
	
	protected void setData(MediaInfo mediaInfo, MediaDetailInfo2 mediaDetailInfo2) {
		mEpView.setData(mediaInfo, mediaDetailInfo2);
		mCommentView.setMediaInfo(mediaInfo);
		mInfoView.setMediaInfo(mediaInfo);
	}
	
	protected void setPlayHistory(PlayHistory playHistory) {
		mEpView.setPlayHistory(playHistory);
	}
	
	protected void setIntroduce(String introduce) {
		mIntroduceView.setIntroduce(introduce);
	}
	
	protected void setMediaViewContents(BaseMediaInfo[] mediaViewContents) {
		mRecommendView.setMediaViewContents(mediaViewContents);
	}
//	
//	public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
//		mScrollView.setOnScrollChangedListener(onScrollChangedListener);
//	}
	public void setPreferenceSource(int source){
		mEpView.setPreferenceSource(source);
	}
	
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScrollViewTopPadding = mContext.getResources().getDimensionPixelSize(R.dimen.detail_scroll_top_padding);
        mInfoView = (DetailInfoView) findViewById(R.id.detail_info_view);
        mEpView = (DetailEpView) findViewById(R.id.detail_ep_view);
        mIntroduceView = (DetailIntroduceView) findViewById(R.id.detail_introduce_view);
        mCommentView = (DetailCommentView) findViewById(R.id.detail_comment_view);
        mRecommendView = (DetailRecommendView) findViewById(R.id.detail_recommend_view);
    }
}