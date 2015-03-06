package com.miui.video.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.FeatureListAdapter;
import com.miui.video.base.BaseFragment;
import com.miui.video.controller.MediaViewClickHandler;
import com.miui.video.datasupply.FeatureListSupply;
import com.miui.video.datasupply.FeatureListSupply.FeatureListListener;
import com.miui.video.statistic.BannerListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.SpecialSubject;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class FeatureListFragment extends BaseFragment {

	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mFeatureLoadingListView;
	private ListViewEx mFeatureListView;
	private View mLoadMoreView;
	private View mFeatureLoadingView;
	private View mFeatureEmptyView;
	private RetryView mFeatureRetryView;
	private FeatureListAdapter mFeatureListAdapter;
	
	//data supply
	private FeatureListSupply mFeatureListSupply;
	
	//data from network
	private ArrayList<SpecialSubject> mFeatureList = new ArrayList<SpecialSubject>();
	
	private int PAGE_SIZE = 20;
	private int mPageNo = 1;
	
	//flags
	private boolean mCanLoadMore = true;
	private boolean mIsDataInited = false;
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.feature_list, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onSelected() {
		super.onSelected();
//		initData();
	}
	
	//init
	private void init() {
		initUI();
		initDataSupply();
		initData();
	}

	private void initUI() {
		initFeatureListView();
		refreshFeatureListView(false);
	}
	
	private void initFeatureListView() {
		mFeatureLoadingListView = (LoadingListView) mContentView.findViewById(R.id.feature_list_list);
		mFeatureListView = mFeatureLoadingListView.getListView();
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mFeatureListView.setLoadMoreView(mLoadMoreView);
		mFeatureListView.setCanLoadMore(true);
		mFeatureListView.setOnLoadMoreListener(mOnLoadMoreListener);

		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		mFeatureListView.setPadding(0, height, 0, 0);
		mFeatureListView.setClipToPadding(false);
		
		mFeatureListAdapter = new FeatureListAdapter(mContext);
		mFeatureListAdapter.setMediaViewClickListener(new MediaViewClickHandler(getActivity(), 
		        SourceTagValueDef.PHONE_V6_FEATURE_LIST_VALUE));
		mFeatureListView.setAdapter(mFeatureListAdapter);
		
		mFeatureLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mFeatureLoadingListView.setLoadingView(mFeatureLoadingView);
		
		mFeatureEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mFeatureEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mFeatureEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
		
		mFeatureRetryView = new RetryView(mContext);
		mFeatureRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getSpecialSubjectList();
			}
		});
	}
	
	private void initData() {
		if(!mIsDataInited) {
			getSpecialSubjectList();
			mIsDataInited = true;
		}
	}
	
	private void initDataSupply() {
		mFeatureListSupply = new FeatureListSupply();
		mFeatureListSupply.addListener(mFeatureListListener);
	}
	
	//get data
	private void getSpecialSubjectList() {
		if(mFeatureList == null || mFeatureList.size() == 0) {
			mFeatureLoadingListView.setShowLoading(true);
		}
		mFeatureListSupply.getFeatureList(mPageNo, PAGE_SIZE, prepareFeatureListStatisticInfo());
	}
	
	//packaged method
	private void refreshFeatureListView(boolean isError) {
	    if(!isAdded()){
	        return;
	    }
		mFeatureListView.setCanLoadMore(mCanLoadMore);
		mFeatureListAdapter.setDataList(mFeatureList);
		if(mFeatureList.size() > 0) {
			return;
		}
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		if(isError) {
			mFeatureLoadingListView.setEmptyView(mFeatureRetryView, emptyViewTopMargin);
		} else {
			mFeatureLoadingListView.setEmptyView(mFeatureEmptyView, emptyViewTopMargin);
		}
	}
	
//	//UI callback
//	private OnFeatureMediaClickListener mOnFeatureMediaClickListener = new OnFeatureMediaClickListener() {
//		
//		@Override
//		public void onFeatureMediaClick(MediaViewFeature view, Object contentInfo) {
//			if(contentInfo instanceof SpecialSubject) {
//				SpecialSubject specialSubject = (SpecialSubject) contentInfo;
//				Intent intent = new Intent();
//				intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
//				intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_FEATURE_LIST_VALUE);
//				intent.setClass(mContext, FeatureMediaActivity.class);
//				startActivity(intent);
//			}
//		}
//	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getSpecialSubjectList();
			}
		}
	};
	
	//data callback
	private FeatureListListener mFeatureListListener = new FeatureListListener() {
		
		@Override
		public void onFeatureListDone(ArrayList<SpecialSubject> featureList, boolean isError, boolean canLoadMore) {
			mCanLoadMore = canLoadMore;
			mFeatureLoadingListView.setShowLoading(false);
			mFeatureList.clear();
			if(featureList != null) {
				mFeatureList.addAll(featureList);
			}
			refreshFeatureListView(isError);
			
			if(canLoadMore && !isError) {
				mPageNo++;
			}
		}
	};
	
	//statistic
	private String prepareFeatureListStatisticInfo() {
		BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
		statisticInfo.cateogry = SourceTagValueDef.PHONE_V6_FEATURE_LIST_VALUE;
		return statisticInfo.formatToJson();
	}
}
