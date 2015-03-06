package com.miui.video.tv;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.ChannelActivity;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseActivity;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.tv.TvChannelManager;
import com.miui.video.tv.TvEpgManager;
import com.miui.video.tv.TvChannelManager.TelevisionInfoListener;
import com.miui.video.tv.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.type.Channel;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

public class TvChannelActivity extends BaseActivity {
	
	public static String KEY_CHANNEL = "key_channel";
	
	//UI
	private View mTitleTop;
	private TextView mTitleName;
	
	private LoadingListView mChannelLoadingListView;
	private ListViewEx mChannelListView;
	private View mChannelLoadMoreView;
	private View mChannelLoadingView;
	private View mChannelEmptyView;
	private RetryView mChannelRetryView;
	private MediaViewListAdapter mChannelAdapter;
	
	//received data
	private Channel mChannel;
	
	//data from net
	private ArrayList<Object> mTelevisionInfos = new ArrayList<Object>();
	
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
		setContentView(R.layout.tv_channel);
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
		initReceivedData();

		initManager();
		initUI();
		initData();
	}
	
	private void initManager() {
		mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
	}
	
	private void initUI() {
		initDecorView();
		
		mTitleTop = findViewById(R.id.tv_title_top);
		mTitleName = (TextView) findViewById(R.id.tv_title_name);
		mTitleTop.setOnClickListener(mOnClickListener);
		
		initChannelListView();
		
		if(mChannel != null) {
			mTitleName.setText(mChannel.name);
		}
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initChannelListView() {
		mChannelLoadingListView = (LoadingListView) findViewById(R.id.tv_channel_list);
		mChannelListView = mChannelLoadingListView.getListView();
		mChannelListView.setVerticalFadingEdgeEnabled(true);
		mChannelListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		View headView = new View(this);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mChannelListView.addHeaderView(headView);
		
		mChannelLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
		mChannelListView.setLoadMoreView(mChannelLoadMoreView);
		mChannelListView.setCanLoadMore(true);
		mChannelListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mChannelAdapter = new MediaViewListAdapter(this);
		mChannelAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mChannelListView.setAdapter(mChannelAdapter);
		
		mChannelLoadingView = View.inflate(this, R.layout.load_view, null);
		mChannelLoadingListView.setLoadingView(mChannelLoadingView);
		
		mChannelEmptyView = View.inflate(this, R.layout.empty_view, null);
		TextView emptyHint = (TextView) mChannelEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.tv_channel_empty_hint));
		
		mChannelRetryView = new RetryView(this);
		mChannelRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getTelevisionInfo();
			}
		});
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_CHANNEL);
		if(obj instanceof Channel) {
			mChannel = (Channel) obj;
		}
	}
	
	private void initData() {
		if(mTvChannelManager == null) {
			mTvChannelManager = new TvChannelManager(this);
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
				mChannelLoadingListView.setShowLoading(true);
			}
			mTvChannelManager.getTelevisionInfo(mTvChannelPageNo);
		}
	}
	
	//packaged method
	private void refreshListView(boolean isError) {
		mChannelListView.setCanLoadMore(mTvChannelCanLoadMore);
		mChannelAdapter.setGroup(mTelevisionInfos);
		
		if(mTelevisionInfos.size() > 0) {
			return;
		}
		if(isError){
			mChannelLoadingListView.setEmptyView(mChannelRetryView);
		}else{
			mChannelLoadingListView.setEmptyView(mChannelEmptyView);
		}
	}
	
	private void mergeExpiredTvInfo() {
		for(int i = 0; i < mTelevisionInfos.size(); i++) {
			Object obj = mTelevisionInfos.get(i);
			if(obj instanceof TelevisionInfo) {
				TelevisionInfo tvInfo = (TelevisionInfo) obj;
				if(tvInfo != null) {
					int tvId = tvInfo.mediaid;
					TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);
					if(expiredTvInfo != null) {
						mTelevisionInfos.set(i, expiredTvInfo);
					}
				}
			}
		}
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mTitleTop.getId()) {
				TvChannelActivity.this.finish();
			}
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof TelevisionInfo) {
				TelevisionInfo tvInfo = (TelevisionInfo) media;
				TvPlayManager.playChannel(TvChannelActivity.this, tvInfo, SourceTagValueDef.PAD_TV_CHANNEL_LIST_VALUE);
			}
		}
	};
	
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
			mChannelLoadingListView.setShowLoading(false);
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
}
