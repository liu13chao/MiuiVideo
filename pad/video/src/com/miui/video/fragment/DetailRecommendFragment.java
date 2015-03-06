package com.miui.video.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewSingleRowAdapter;
import com.miui.video.datasupply.MediaRecommendSupply;
import com.miui.video.datasupply.MediaRecommendSupply.MediaRecommendListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author tangfuling
 *
 */

public class DetailRecommendFragment extends Fragment {
	
	private Context mContext;
	
	//UI
	private LoadingListView mRecommendLoadingListView;
	private ListViewEx mRecommendListView;
	private View mRecommendLoadView;
	private View mRecommendEmptyView;
	private RetryView mRecommendRetryView;
	private MediaViewSingleRowAdapter mRecommendAdapter;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data from network
	private Object[] mRecommendations;
	
	//data supply
	private MediaRecommendSupply mMediaRecommendSupply;
	
	//flags
	private boolean mIsDataInited = false;
	
	private int mPosterW;
	private int mPosterH;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object mediaInfo = bundle.getSerializable(MediaDetailDialogFragment.KEY_MEDIA_INFO);
			if(mediaInfo instanceof MediaInfo) {
				this.mMediaInfo = (MediaInfo) mediaInfo;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRecommendLoadingListView = new LoadingListView(mContext);
		return mRecommendLoadingListView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	//public method
	public void onSelected() {
		initData();
	}
	
	//init
	private void init() {
		initDimen();
		initUI();
	}
	
	private void initUI() {
		mRecommendListView = mRecommendLoadingListView.getListView();
		mRecommendAdapter = new MediaViewSingleRowAdapter(mContext);
		
		View headView = new View(mContext);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mRecommendListView.addHeaderView(headView);
		
		mRecommendListView.setAdapter(mRecommendAdapter);
		int nameViewColor = mContext.getResources().getColor(R.color.p_80_black);
		int statusViewColor = mContext.getResources().getColor(R.color.p_30_black);
		mRecommendAdapter.setInfoViewColor(nameViewColor, statusViewColor);
		mRecommendAdapter.setPosterSize(mPosterW, mPosterH);
		mRecommendAdapter.setOnMediaClickListener(mOnMediaClickListener);
		
		mRecommendLoadView = View.inflate(mContext, R.layout.load_view_black, null);
		mRecommendLoadingListView.setLoadingView(mRecommendLoadView);
		
		mRecommendEmptyView = View.inflate(mContext, R.layout.empty_view_black, null);
		TextView emptyHint = (TextView) mRecommendEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(mContext.getResources().getString(R.string.detail_recommend_empty_hint));
		
		mRecommendRetryView = new RetryView(mContext, RetryView.STYLE_BLACK);
		mRecommendRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getRecommendData();
			}
		});
	}
	
	private void initDimen() {
		mPosterW = mContext.getResources().getDimensionPixelSize(R.dimen.media_detail_media_width);
		mPosterH = mContext.getResources().getDimensionPixelSize(R.dimen.media_detail_media_height);
	}
	
	private void initData() {
		if(!mIsDataInited) {
			initDataSupply();
			getRecommendData();
			mIsDataInited = true;
		}
	}
	
	private void initDataSupply() {
		if(mMediaRecommendSupply == null) {
			mMediaRecommendSupply = new MediaRecommendSupply();
			mMediaRecommendSupply.addListener(mMediaRecommendListener);
		}
	}
	
	//get data
	private void getRecommendData() {
		if(mRecommendations == null) {
			if(mMediaInfo != null) {
				mRecommendLoadingListView.setShowLoading(true);
				mMediaRecommendSupply.getMediaRecommend(mMediaInfo.mediaid, 4);
			}
		}
	}
	
	//packaged method
	private void refreshRecommendListView(boolean isError) {
		if(mRecommendations != null) {
			mRecommendAdapter.setGroup(mRecommendations);
			return;
		}
		if(isError){
			mRecommendLoadingListView.setEmptyView(mRecommendRetryView);
		}else{
			mRecommendLoadingListView.setEmptyView(mRecommendEmptyView);
		}
	}

	//data callback
	private MediaRecommendListener mMediaRecommendListener = new MediaRecommendListener() {
		
		@Override
		public void onMediaRecommendDone(Object[] recommendations, boolean isError) {
			mRecommendLoadingListView.setShowLoading(false);
			mRecommendations = recommendations;
			refreshRecommendListView(isError);
		}
	};
	
	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(mContext, MediaDetailDialogFragment.class);
				intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, (MediaInfo)media);
				intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_DETAIL_RECOMMEND_VALUE);
				mContext.startActivity(intent);
			}
		}
	};
}
