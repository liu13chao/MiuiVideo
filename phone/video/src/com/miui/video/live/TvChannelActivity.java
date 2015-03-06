package com.miui.video.live;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcc.cmvsdk.main.MvSdkJar;
import com.miui.video.ChannelActivity;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.GridRowInfoAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseMediaListActivity;
import com.miui.video.controller.MediaViewClickHandler;
import com.miui.video.controller.content.MediaInfoContentBuilder;
import com.miui.video.live.TvChannelManager.TelevisionInfoListener;
import com.miui.video.live.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Channel;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class TvChannelActivity extends BaseMediaListActivity {
	
	//UI
//	private LoadingListView mLoadingListView;
//	private ListViewEx mListView;
	private View mLoadMoreView;
//	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private GridRowInfoAdapter<TelevisionInfo> mAdapter;
	
	//data from net
	private ArrayList<TelevisionInfo> mTelevisionInfos = new ArrayList<TelevisionInfo>();
	
	//data supply
	private TvChannelManager mTvChannelManager;
	
	//request params
	private int mTvChannelPageNo = 1;
	
	//manager
	private TvEpgManager mTvEpgManager;
	
	//flags
	private boolean mTvChannelCanLoadMore = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		
		Channel channel = (Channel) getIntent().getSerializableExtra(ChannelActivity.KEY_CHANNEL);
		uploadChannelStatistic(channel);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTvEpgManager.removeListeners(mTvUpdateInterface);
	}
	
	private void init() {
		initManager();
		initUI();
		initData();
	}
	
	private void initManager() {
		mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
	}
	
	private void initUI() {
		setTopTitle(R.string.tv);
		initChannelListView();
	}
	
	private void initChannelListView() {
//		mLoadingListView = (LoadingListView) findViewById(R.id.media_list);
//		mListView = mLoadingListView.getListView();
	    mLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
	    mListView.setLoadMoreView(mLoadMoreView);
	    mListView.setCanLoadMore(true);
	    mListView.setOnLoadMoreListener(mOnLoadMoreListener);
//		mAdapter = new GridRowInfoAdapter<TelevisionInfo>(this, 3, R.layout.media_view_recommended_tv);
////		mAdapter.setOnItemClickListener(mOnItemClickListener);
//		mListView.setAdapter(mAdapter);
//		
//		mListView.setSelector(R.drawable.transparent);
//		mListView.setVerticalScrollBarEnabled(false);
//		mListView.setAdapter(mAdapter);
//		mLoadingView = View.inflate(this, R.layout.load_view, null);
//		mLoadingListView.setLoadingView(mLoadingView);
	    mEmptyView = View.inflate(this, R.layout.empty_view, null);
	    TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_title);
	    emptyHint.setText(getResources().getString(R.string.tv_channel_empty_hint));

	    mRetryView = new RetryView(this);
	    mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
	        @Override
	        public void OnRetryLoad(View vClicked) {
	            getTelevisionInfo();
	        }
	    });
	}
	
	private void initData() {
		if(mTvChannelManager == null) {
			mTvChannelManager = new TvChannelManager();
		}
		mTvChannelManager.addListener(mTelevisionInfoListener);
		getTelevisionInfo();
	}
	
	//get data
	private void getTelevisionInfo() {
		if(mTelevisionInfos.size() > 0 && !mTvChannelCanLoadMore) {
			refreshListView(false);
		} else {
			if(mTelevisionInfos.size() == 0) {
				mLoadingListView.setShowLoading(true);
			}
			mTvChannelManager.getTelevisionInfo(mTvChannelPageNo);
		}
	}
	
	//packaged method
	private void refreshListView(boolean isError) {
		mListView.setCanLoadMore(mTvChannelCanLoadMore);
//		mAdapter.setGroup(mTelevisionInfos);
		mAdapter.setDataList(mTelevisionInfos);
		if(mTelevisionInfos.size() > 0) {
			return;
		}
		
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		if(isError){
			mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
		}else{
			mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
		}
	}
	
	private void mergeExpiredTvInfo() {
		for(int i = 0; i < mTelevisionInfos.size(); i++) {
			Object obj = mTelevisionInfos.get(i);
			if(obj instanceof TelevisionInfo) {
				TelevisionInfo tvInfo = (TelevisionInfo) obj;
				if(tvInfo != null) {
					int tvId = tvInfo.getChannelId();
					TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);
					if(expiredTvInfo != null) {
					    expiredTvInfo.backgroundcolor = tvInfo.backgroundcolor;
					    expiredTvInfo.channelname = tvInfo.channelname;
					    expiredTvInfo.posterurl = tvInfo.posterurl;
						mTelevisionInfos.set(i, expiredTvInfo);
					}
				}
			}
		}
	}

//	//UI callback
//	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			Object obj = parent.getItemAtPosition(position);
//			if(obj instanceof TelevisionInfo) {
//				TelevisionInfo tvInfo = (TelevisionInfo) obj;
//				TvPlayManager.playChannel(TvChannelActivity.this, tvInfo, SourceTagValueDef.PHONE_V6_TV_CHANNEL_LIST_VALUE);
//			}
//		}
//	};

	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mTvChannelCanLoadMore) {
				getTelevisionInfo();
			}
		}
	};

	//data callback
	private TelevisionInfoListener mTelevisionInfoListener = new TelevisionInfoListener() {
		
		@Override
		public void onTelevisionInfosDone(
				ArrayList<TelevisionInfo> televisionInfos, boolean isError,
				boolean canLoadMore) {
			mLoadingListView.setShowLoading(false);
			mTelevisionInfos.clear();
			if(televisionInfos != null) {
				mTelevisionInfos.addAll(televisionInfos);
				mTvEpgManager.addTelevisionInfo(televisionInfos);
			}
			mTvChannelCanLoadMore = canLoadMore;
			refreshListView(isError);
			if(canLoadMore && !isError) {
				mTvChannelPageNo++;
			}
		}
	};
	
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			refreshListView(false);
		}
	};
	
	//statistic
	private void uploadChannelStatistic(Channel channel) {
		if(channel == null) {
			return;
		}
		ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
		statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_LIVE;
		StringBuilder categoryId = new StringBuilder();
		categoryId.append(channel.name);
		categoryId.append("(");
		categoryId.append(channel.id);
		categoryId.append("");
		statisticInfo.categoryId = categoryId.toString();
		DKApi.uploadComUserData(statisticInfo.formatToJson());
	}

	@Override
	protected int getContentViewRes() {
		return R.layout.activity_media_info_list;
	}

    @Override
    protected CharSequence getPageTitle() {
        return "";
    }

    @Override
    protected void onItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof TelevisionInfo) {
            TelevisionInfo tvInfo = (TelevisionInfo) mediaInfo;
            TvPlayManager.playChannel(TvChannelActivity.this, tvInfo, 
                    SourceTagValueDef.PHONE_V6_TV_CHANNEL_LIST_VALUE);
        }
    }

    @Override
    protected void onItemLongClick(BaseMediaInfo mediaInfo) {
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mAdapter = new GridRowInfoAdapter<TelevisionInfo>(this, 3, R.layout.media_view_grid_tv);
        mAdapter.setMediaContentBuilder(new MediaInfoContentBuilder(this));
//        mAdapter.setMediaViewClickListener(new MediaViewClickHandler(this, SourceTagValueDef.PHONE_V6_TV_CHANNEL_LIST_VALUE));
        return mAdapter;
    }

    @Override
    protected View getEmptyView() {
        return null;
    }
}
