package com.miui.video.fragment;


import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.IntroduceAdapter;
import com.miui.video.datasupply.MediaDetailInfoSupply;
import com.miui.video.datasupply.MediaDetailInfoSupply.MediaDetailInfoDoneListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class DetailIntroduceFragment extends Fragment {
	
	private Context mContext;
	
	//UI
	private LoadingListView mIntroduceLoadingListView;
	private ListViewEx mIntroduceListView;
	private View mIntroduceLoadView;
	private View mIntroduceEmptyView;
	private RetryView mIntroduceRetryView;
	private IntroduceAdapter mIntroduceAdapter;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data from network
	private MediaDetailInfo2 mMediaDetailInfo;
		
	//data supply
	private MediaDetailInfoSupply mMediaDetailInfoSupply;
	
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
		mIntroduceLoadingListView = new LoadingListView(mContext);
		return mIntroduceLoadingListView;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mMediaDetailInfoSupply != null) {
			mMediaDetailInfoSupply.removeMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
	}
	
	//init
	private void init() {
		initUI();
		initData();
	}
	
	private void initUI() {
		mIntroduceListView = mIntroduceLoadingListView.getListView();
		mIntroduceAdapter = new IntroduceAdapter(mContext);
		mIntroduceListView.setAdapter(mIntroduceAdapter);
		
		mIntroduceLoadView = View.inflate(mContext, R.layout.load_view_black, null);
		mIntroduceLoadingListView.setLoadingView(mIntroduceLoadView);
		
		mIntroduceEmptyView = View.inflate(mContext, R.layout.empty_view_black, null);
		TextView emptyHint = (TextView) mIntroduceEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(mContext.getResources().getString(R.string.detail_introduce_empty_hint));
		
		mIntroduceRetryView = new RetryView(mContext, RetryView.STYLE_BLACK);
		mIntroduceRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getMediaDetailData();
			}
		});
	}
	
	private void initData() {
		initDataSupply();
		mIntroduceLoadingListView.setShowLoading(true);
	}
	
	private void initDataSupply() {
		if(mMediaDetailInfoSupply == null) {
			mMediaDetailInfoSupply = DKApp.getSingleton(MediaDetailInfoSupply.class);
			mMediaDetailInfoSupply.addMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
	}
	
	//get data
	private void getMediaDetailData() {
		if(mMediaDetailInfo == null) {
			if(mMediaInfo != null) {
				boolean getAll = true;
				mIntroduceLoadingListView.setShowLoading(true);
				mMediaDetailInfoSupply.getMediaDetailInfo(mMediaInfo.mediaid, getAll, 
							MediaFeeDef.MEDIA_ALL, null);
			}
		}
	}
	
	//packaged method
	private void refreshIntroduceListView(boolean isError) {
		if(mMediaDetailInfo != null && mMediaDetailInfo.mediainfo != null) {
			mIntroduceAdapter.setData(mMediaDetailInfo.mediainfo);
			return;
		}
		if(isError){
			mIntroduceLoadingListView.setEmptyView(mIntroduceRetryView);
		}else{
			mIntroduceLoadingListView.setEmptyView(mIntroduceEmptyView);
		}
	}

	//data callback
	private MediaDetailInfoDoneListener mMediaDetailInfoDoneListener = new MediaDetailInfoDoneListener() {
		
		@Override
		public void onMediaDetailInfoDone(MediaDetailInfo2 mediaDetailInfo,
				boolean isError) {
			mIntroduceLoadingListView.setShowLoading(false);
			mMediaDetailInfo = mediaDetailInfo;
			if(mMediaInfo != null && mMediaDetailInfo != null) {
				if(mMediaDetailInfo.mediainfo != null) {
					mMediaInfo.smallImageURL = mMediaDetailInfo.mediainfo.smallImageURL;
				}
			}
			refreshIntroduceListView(isError);
		}
	};
}
