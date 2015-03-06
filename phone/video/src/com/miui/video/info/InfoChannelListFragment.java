package com.miui.video.info;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.InfoListAdapter;
import com.miui.video.datasupply.InformationListSupply.InformationDataRecommendedList;
import com.miui.video.info.InfoChannelDataManager.InfoDataChangeListener;
import com.miui.video.type.InformationData;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.videoplayer.fragment.VideoFragment;

public class InfoChannelListFragment extends Fragment implements InfoDataChangeListener{
	
	private final static String TAG = InfoChannelListFragment.class.getName();

	private Context mContext;
	private View mContentView;

	// UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private InfoListAdapter mAdapter;
	
	private VideoFragment mVideoFragment;

	private InfoChannelDataManager mDataManager = null;
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.info_list_fragment, null);
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}

	// init
	private void init() {
		initUI();
		initData();
		if(mDataManager != null){
		    int selection = mDataManager.getSelectionPosition() + 1;
	        if(selection < mListView.getAdapter().getCount()){
	            mListView.setSelection(selection);
	        }		    
		}
	}

	private void initUI() {
		initListView();
	}

	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView
				.findViewById(R.id.info_channel_fragment_list);
		mListView = mLoadingListView.getListView();
		mListView.setOnItemClickListener(mOnItemClickListener);
		View headView = new View(mContext);
		int height = (int) getResources().getDimension(
				R.dimen.video_common_interval_30);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		int bottom = (int) getResources().getDimension(R.dimen.size_20);
		mListView.setPadding(0, 0, 0, bottom);
		mListView.addHeaderView(headView);

		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
//		mListView.setOnLoadMoreListener(mOnLoadMoreListener);

		mAdapter = new InfoListAdapter(mContext);
		mListView.setAdapter(mAdapter);

		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);

		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);

		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getInfoRecData();
			}
		});
	}
	
	public void attachVideoFragment(VideoFragment fragment){
	    mVideoFragment = fragment;
	}

	public void setSelection(int position){
		if(mListView != null && mAdapter != null && position >= 0 && position < mAdapter.getCount()){
			mAdapter.setSelection(position);
			mAdapter.notifyDataSetChanged();
			View selectedV = mListView.getChildAt(position + 1 - mListView.getFirstVisiblePosition());
			if(selectedV != null){
				if(selectedV.getY() + selectedV.getHeight() / 2 > mListView.getY() + mListView.getHeight() ||
						selectedV.getY() < mListView.getY()){
					mListView.setSelection(position + 1);
				}
			}else{
				mListView.setSelection(position + 1);
			}
		}
	}
	
	public void setDataManager(InfoChannelDataManager manager){
	    if(manager != null){
	        mDataManager = manager;
	        mDataManager.addInfoDataChangeListener(this);
	    }
	}
	
	private void initData() {
		getInfoRecData();
	}

	private void getInfoRecData(){
		if(mDataManager != null){
			mLoadingListView.setShowLoading(true);
			mDataManager.doGetInfoRecommendList();
		}
	}
	
	// packaged method
	public void refreshListView(boolean isError) {
		if(mListView == null){
			return;
		}
		Log.d(TAG, "refreshListView");
		if(mLoadingListView != null){
			mLoadingListView.setShowLoading(false);
		}
		InformationData[] informations = null;
		if(mDataManager == null){
			return;
		}
		InformationDataRecommendedList recommendedList = mDataManager.getRecommendedInfoList();
		if (recommendedList != null && recommendedList.mRecommendedList != null) {
		    List<InformationData> list = new ArrayList<InformationData>();
		    if(recommendedList.mInfoData != null){
		        list.add(recommendedList.mInfoData);
		    }
		    if(recommendedList.mRecommendedList.medialist != null){
		        list.addAll(recommendedList.mRecommendedList.medialist);
		    }
			mListView.setCanLoadMore(recommendedList.mRecommendedList.canLoadMore);
			mListView.setSelection(mDataManager.getSelectionPosition());
	         mAdapter.setSelection(mDataManager.getSelectionPosition());
			mAdapter.setGroup(list);
		} else {
			mAdapter.setGroup(informations);
		}

		int emptyViewTopMargin = getResources().getDimensionPixelSize(
				R.dimen.video_common_empty_top_margin2);
		if (isError) {
			mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
		} else {
			mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
		}
	}

	public void showLoading(){
		if(mLoadingListView != null){
			mLoadingListView.setShowLoading(true);
		}
	}
	
//	// UI callback
//	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
//		@Override
//		public void onLoadMore(ListView listView) {
//			if(mDataManager != null){
//				mDataManager.doGetInfoRecommendList();
//			}
//		}
//	};

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(position >= mListView.getHeaderViewsCount()){
				if(mVideoFragment != null){
				    mVideoFragment.playCi(position - mListView.getHeaderViewsCount());
				}
			}
		}
	};

}
