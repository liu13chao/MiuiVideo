package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.adapter.ChannelRankAdapter;
import com.miui.video.adapter.FilterResultItemAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.datasupply.ChannelFilterSupply;
import com.miui.video.datasupply.ChannelFilterSupply.ChannelFilterListener;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.bg.OnlineBg;

public class ChannelFilterResultActivity extends BaseFragmentActivity{

	private View mTitleTop;
	private TextView mTitleName;
	private LoadingListView mLoadingListView;
	private GridView mGridView;
	private FilterResultItemAdapter mFilterAdapter;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private ChannelRankAdapter mAdapter;
	
	private int mPageNo = 1;
	
	private Channel mChannel;
	private int[] mSelectedChannelIds;
	private String[] mSelectedChannelNames;
	
	private boolean mCanLoadMore = true;
	private boolean mIsDataInited = false;
	
	private ArrayList<MediaInfo> mFilterMedias = new ArrayList<MediaInfo>();
	private ChannelFilterSupply mChannelFilterSupply;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_filter_result);
		init();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mChannelFilterSupply.removeListener(mChannelFilterListener);
	}
	
	private void init() {
		initReceivedData();
		initUI();
		initData();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(ChannelActivity.KEY_CHANNEL);
		if(obj instanceof Channel) {
			mChannel = (Channel) obj;
		}
		mSelectedChannelIds = intent.getIntArrayExtra(ChannelActivity.KEY_CHANNEL_IDS);
		mSelectedChannelNames = intent.getStringArrayExtra(ChannelActivity.KEY_CHANNEL_NAMES);
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initUI() {
		initDecorView();
		
		mTitleTop = findViewById(R.id.title_top);
		mTitleName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChannelFilterResultActivity.this.finish();
			}
		});
		if(mChannel != null) {
			mTitleName.setText(mChannel.name + getString(R.string.filter));
		}
		mLoadingListView = (LoadingListView) findViewById(R.id.channel_filter_result_list);
		mListView = mLoadingListView.getListView();
        mListView.setClipToPadding(false);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
        mListView.setPadding(0, paddingTop,  0, paddingTop);
        mListView.setVerticalScrollBarEnabled(false);
		LinearLayout gridLayout = (LinearLayout) View.inflate(this, R.layout.channel_filter_result_gridview, null);
		mGridView = (GridView) gridLayout.findViewById(R.id.channel_filter_result_filters);
		mFilterAdapter = new FilterResultItemAdapter(this);
		mFilterAdapter.setSelectedChannelChangeListener(mChannelListener);
		mFilterAdapter.setFilterItems(mSelectedChannelNames);
		if(mFilterAdapter.getCount() == 0){
			mGridView.setVisibility(View.GONE);
		}else{
			mGridView.setAdapter(mFilterAdapter);
		}
		mListView.addHeaderView(gridLayout);
		mLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mListView.setOnItemClickListener(mOnMediaClickListener);
		mAdapter = new ChannelRankAdapter(this, mChannel, mChannel.getChannelType());
		mAdapter.setShowRank(false);
//		mAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(this, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_filter_title));
//		TextView emptySub = (TextView) mEmptyView.findViewById(R.id.empty_sub_title);
//		emptySub.setText(getResources().getString(R.string.error_empty_filter_sub));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.screening_no_icon);
		
		mRetryView = new RetryView(this);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getFilterMedia(null);
			}
		});
	}
	
	private void initData() {
		initDataSupply();
		if(!mIsDataInited) {
			if(mFilterMedias == null || mFilterMedias.size() == 0) {
				getFilterMedia(null);
			} else {
				refreshListView(false);
			}
			mIsDataInited = true;
		}
	}
	
	private void initDataSupply() {
		if(mChannelFilterSupply == null) {
			mChannelFilterSupply = new ChannelFilterSupply();
		}
		mChannelFilterSupply.addListener(mChannelFilterListener);
	}
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getFilterMedia(null);
			}
		}
	};
	
	private void getFilterMedia(String statisticInfo) {
		if(mFilterMedias != null && mFilterMedias.size() > 0 && !mCanLoadMore) {
			refreshListView(false);
		} else {
			if(mFilterMedias == null || mFilterMedias.size() == 0) {
				mLoadingListView.setShowLoading(true);
			}
			MediaInfoQuery query = new MediaInfoQuery();
			query.orderBy = DKApi.ORDER_BY_HOT;
			query.pageNo = mPageNo;
			query.pageSize = 24;
			query.ids = mSelectedChannelIds.length == 0 ? new int[] {mChannel.id} : mSelectedChannelIds;
			query.statisticInfo = statisticInfo;
			mChannelFilterSupply.getFilterMedias(query);
		}
	}
	
	private OnItemClickListener mOnMediaClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (parent != null && parent.getItemAtPosition(position) instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(ChannelFilterResultActivity.this, MediaDetailActivity.class);
				intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)parent.getItemAtPosition(position));
				intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_CHANNEL_RANK_VALUE);
				startActivity(intent);
			}
		}
	};
	
	OnSelectedChannelChangedLinstener mChannelListener = new OnSelectedChannelChangedLinstener() {
		
		@Override
		public void onChannelRemoved(int position) {
			if(position >= 0 && position < mSelectedChannelIds.length){
				int[] ids = new int[mSelectedChannelIds.length - 1];
				String[] names = new String[mSelectedChannelIds.length - 1];
				int k = 0;
				for(int i = 0 ; i < mSelectedChannelIds.length; i ++){
					if(i != position){
						ids[k] = mSelectedChannelIds[i];
						names[k] = mSelectedChannelNames[i];
						k ++;
					}
				}
				mSelectedChannelIds = ids;
				mSelectedChannelNames = names;
				resetListView();
				getFilterMedia(null);
			}
		}
	};
	
	public interface OnSelectedChannelChangedLinstener{
		public void onChannelRemoved(int position);
	}
	
	private void resetListView() {
		mFilterAdapter.setFilterItems(mSelectedChannelNames);
		if(mFilterAdapter.getCount() == 0){
			mGridView.setVisibility(View.GONE);
		}else{
			mGridView.setAdapter(mFilterAdapter);
		}
		mFilterMedias = null;
		mPageNo = 1;
		mCanLoadMore = true;
		refreshListView(false);
	}
	
	private void refreshListView(boolean isError) {
		mListView.setCanLoadMore(mCanLoadMore);
		mAdapter.setGroup(mFilterMedias);
		if(mFilterMedias != null && mFilterMedias.size() > 0) {
			return;
		}
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		if(isError){
			mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
		}else{
			mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
		}
	}
	
	private ChannelFilterListener mChannelFilterListener = new ChannelFilterListener() {
		
		@Override
		public void onFilterMediasDone(List<MediaInfo> filterMedias, boolean isError,
				boolean canLoadMore) {
			mCanLoadMore = canLoadMore;
			mLoadingListView.setShowLoading(false);
			mFilterMedias = new ArrayList<MediaInfo>(filterMedias);
			refreshListView(isError);
			if(canLoadMore) {
				mPageNo++;
			}
		}
	};
	
}
