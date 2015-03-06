package com.miui.video;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.base.BaseActivity;
import com.miui.video.datasupply.FeatureListSupply;
import com.miui.video.datasupply.FeatureListSupply.FeatureListListener;
import com.miui.video.statistic.BannerListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.SpecialSubject;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author tangfuling
 *
 */

public class FeatureListActivity extends BaseActivity {
	
	//UI
	private LoadingListView mFeatureLoadingListView;
	private ListViewEx mFeatureListView;
	private View mLoadMoreView;
	private View mFeatureLoadingView;
	private View mFeatureEmptyView;
	private RetryView mFeatureRetryView;
	private MediaViewListAdapter mMediaViewListAdapter;
	
	private TextView mTitleTopName;
	private TextView mTitleTopStatus;
	private View mTitleTop;
	
	//data supply
	private FeatureListSupply mFeatureListSupply;
	
	//data from network
	private ArrayList<Object> mFeatureList = new ArrayList<Object>();
	
	private int PAGE_SIZE = 20;
	private int mPageNo = 1;
	
	//flags
	private boolean mCanLoadMore = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature_list);
		init();
	}
	
	//init
	private void init() {
		initUI();
		initData();
	}
	
	private void initUI() {
		initDecorView();
		initFeatureListView();
		initTitleTop();
		refreshFeatureListView(false);
		refreshTitleTop();
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initFeatureListView() {
		mFeatureLoadingListView = (LoadingListView) findViewById(R.id.feature_list_list);
		mFeatureListView = mFeatureLoadingListView.getListView();
		mFeatureListView.setVerticalFadingEdgeEnabled(true);
		mFeatureListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		View headView = new View(this);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mFeatureListView.addHeaderView(headView);
		
		mLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
		mFeatureListView.setLoadMoreView(mLoadMoreView);
		mFeatureListView.setCanLoadMore(true);
		mFeatureListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mMediaViewListAdapter = new MediaViewListAdapter(this);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mMediaViewListAdapter.setShowText(false);
		mFeatureListView.setAdapter(mMediaViewListAdapter);
		
		mFeatureLoadingView = View.inflate(this, R.layout.load_view, null);
		mFeatureLoadingListView.setLoadingView(mFeatureLoadingView);
		
		mFeatureEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mFeatureEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.feature_list_empty_hint));
		ImageView emptyIcon = (ImageView) mFeatureEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);
		
		mFeatureRetryView = new RetryView(this);
		mFeatureRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getSpecialSubjectList();
			}
		});
	}
	
	private void initTitleTop() {
		mTitleTopName = (TextView) findViewById(R.id.title_top_name);
		mTitleTopStatus = (TextView) findViewById(R.id.title_top_status);
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		mTitleTopName.setText(getResources().getString(R.string.feature));
	}
	
	private void initData() {
		initDataSupply();
		getSpecialSubjectList();
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
		mFeatureListView.setCanLoadMore(mCanLoadMore);
		
		mMediaViewListAdapter.setGroup(mFeatureList);
		
		if(mFeatureList.size() > 0) {
			return;
		}
		if(isError) {
			mFeatureLoadingListView.setEmptyView(mFeatureRetryView);
		} else {
			mFeatureLoadingListView.setEmptyView(mFeatureEmptyView);
		}
	}
	
	private void refreshTitleTop() {
		String str = getString(R.string.count_ge_feature);
		str = String.format(str, mFeatureList.size());
		mTitleTopStatus.setText(str);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mTitleTop.getId()) {
				FeatureListActivity.this.finish();
			}
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof SpecialSubject) {
				SpecialSubject specialSubject = (SpecialSubject) media;
				Intent intent = new Intent();
				intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
				intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PAD_FEATURE_LIST_VALUE);
				intent.setClass(FeatureListActivity.this, FeatureMediaActivity.class);
				startActivity(intent);
			}
		}
	};
	
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
		public void onFeatureListDone(ArrayList<Object> featureList, boolean isError, boolean canLoadMore) {
			mCanLoadMore = canLoadMore;
			mFeatureLoadingListView.setShowLoading(false);
			mFeatureList.clear();
			if(featureList != null) {
				mFeatureList.addAll(featureList);
			}
			refreshFeatureListView(isError);
			refreshTitleTop();
			
			if(canLoadMore && !isError) {
				mPageNo++;
			}
		}
	};
	
	//statistic
	private String prepareFeatureListStatisticInfo() {
		BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
		statisticInfo.cateogry = SourceTagValueDef.PAD_FEATURE_LIST_VALUE;
		return statisticInfo.formatToJson();
	}
}
