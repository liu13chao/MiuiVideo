package com.miui.video.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.ChannelActivity;
import com.miui.video.R;
import com.miui.video.adapter.CategoryListAdapter;
import com.miui.video.base.BaseFragment;
import com.miui.video.datasupply.CategoryListSupply;
import com.miui.video.datasupply.CategoryListSupply.CategoryListListener;
import com.miui.video.info.InfoChannelActivity;
import com.miui.video.live.TvChannelActivity;
import com.miui.video.statistic.BannerListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Category;
import com.miui.video.type.Channel;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.media.MediaViewCategory;
import com.miui.video.widget.media.MediaViewCategory.OnCategoryMediaClickListener;

public class CategoryFragment extends BaseFragment {

	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private CategoryListAdapter mCategoryListAdapter;
	
	//data supply
	private CategoryListSupply mCategoryListSupply;
//	private ChannelLoader mChannelLoader;
	//data from network
	private ArrayList<Category> mCategoryList = new ArrayList<Category>();
//	private HashMap<Integer, Channel> mChannelMap = new HashMap<Integer, Channel>();
	//flags
//	private boolean mIsDataInited = false;
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.category_list, null);
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
		refreshListView(false);
	}
	
	private void initFeatureListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.category_list_list);
		mListView = mLoadingListView.getListView();
		
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		mListView.setPadding(0, height, 0, 0);
		mListView.setClipToPadding(false);
		
		mCategoryListAdapter = new CategoryListAdapter(mContext);
		mCategoryListAdapter.setOnCategoryMediaClickListener(mCategoryClickListener);
		mListView.setAdapter(mCategoryListAdapter);
		
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
			    loadData();
//				getCategoryList();
			}
		});
	}
	
	private void initData() {
	    loadData();
//		if(!mIsDataInited) {
//			getData();
//			mIsDataInited = true;
//		}
	}
	
	private void initDataSupply() {
		mCategoryListSupply = new CategoryListSupply();
		mCategoryListSupply.addListener(mCategoryListListener);
//		mChannelLoader = new ChannelLoader();
//		mChannelLoader.addListener(mLoadListener);
	}
	
	//get data
	private void loadData() {
	    loadCategoryList();
//		getCategoryList();
//		getChannelList();
	}
	
	private void loadCategoryList() {
//		if(mCategoryList == null || mCategoryList.size() == 0) {
	    mLoadingListView.setShowLoading(true);
//		}
		mCategoryListSupply.getCategoryList(prepareFeatureListStatisticInfo());
	}
	
//	private void getChannelList() {
//		if(mChannelMap == null || mChannelMap.size() == 0) {
//			mChannelLoader.load();
//		}
//	}
	
	//packaged method
	private void refreshListView(boolean isError) {
	    if(!isAdded()){
	        // fragment has bean removed.
	        return;
	    }
//		mCategoryListAdapter.setChannelMap(mChannelMap);
	    if(mCategoryList != null && mCategoryList.size()  > 0){
	        mCategoryListAdapter.setGroup(mCategoryList);
	    }else{
	        int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
	        if(isError) {
	            mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
	        } else {
	            mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
	        }
	    }
	}
	
	//UI callback
	private OnCategoryMediaClickListener mCategoryClickListener = new OnCategoryMediaClickListener() {
		
		@Override
		public void onCategoryMediaClick(MediaViewCategory view, Channel channel) {
			if(channel == null) {
				return;
			}
			if(channel.isTvChannel()) {
				Intent intent = new Intent();
				intent.setClass(mContext, TvChannelActivity.class);
				startActivity(intent);
			} else if(channel.isInformationType()) {
				Intent intent = new Intent();
				intent.putExtra(InfoChannelActivity.KEY_CHANNEL, channel);
				intent.setClass(mContext, InfoChannelActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent();
				intent.putExtra(ChannelActivity.KEY_CHANNEL, channel);
				intent.setClass(mContext, ChannelActivity.class);
				startActivity(intent);
			}
		}
	};
	
	//data callback
	private CategoryListListener mCategoryListListener = new CategoryListListener() {
		@Override
		public void onCategoryListDone(ArrayList<Category> categoryList,
				boolean isError) {
			mLoadingListView.setShowLoading(false);
			mCategoryList.clear();
			mCategoryList.addAll(categoryList);
			refreshListView(isError);
		}
	};
	
//	private LoadListener mLoadListener = new LoadListener() {
//		@Override
//		public void onLoadFinish(DataLoader loader) {
//			mChannelMap.clear();
//			List<Channel> channels = mChannelLoader.getChannels();
//			if(channels != null) {
//				for(int i = 0; i < channels.size(); i++) {
//					Channel channel = channels.get(i);
//					if(channel != null) {
//						mChannelMap.put(channel.id, channel);
//					}
//				}
//			}
//			refreshListView(false);
//		}
//		
//		@Override
//		public void onLoadFail(DataLoader loader) {
//			
//		}
//	};
	//statistic
	private String prepareFeatureListStatisticInfo() {
		BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
		statisticInfo.cateogry = SourceTagValueDef.PHONE_V6_FEATURE_LIST_VALUE;
		return statisticInfo.formatToJson();
	}
}
