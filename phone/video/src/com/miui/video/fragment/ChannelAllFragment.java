package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.adapter.ChannelRankAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.datasupply.ChannelFilterSupply;
import com.miui.video.datasupply.ChannelFilterSupply.ChannelFilterListener;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.statistic.ChannelMediaInfoListTypeDef;
import com.miui.video.statistic.GetChannelMediaListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
/**
 *@author tangfuling
 *
 */

public class ChannelAllFragment extends Fragment {
	
	private final String TAG = ChannelAllFragment.class.getName();
	
	//  Intent keys
	public static String KEY_SORT_NAME = "channel_sort";
	public static String KEY_CHANNEL = "channel";
	public static String KEY_CATEGORY = "category";

	
	private Context mContext;
	private View mContentView;
	
	//UI
	
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private ChannelRankAdapter mAdapter;
	//received data
	private Channel mChannel;
	
	public int mCategory;
	
	//UI callback data
//	private int[] mSelectedChannelIds;
	
	//data from network
	private ArrayList<MediaInfo> mFilterMedias = new ArrayList<MediaInfo>();
	
	//data supply
	private ChannelFilterSupply mChannelFilterSupply;
	
	//request params
	private int mPageSize = 24;
	private int mPageNo = 1;
	private int mOrderBy = DKApi.ORDER_BY_ISSUEDATE;
	private int[] mChannelIds;
	
	//flags
	private boolean mCanLoadMore = true;
	
//	//flags
//	private boolean mIsDataInited = false;
	
//	private boolean mNeedAutoInitData = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object obj = bundle.getSerializable(KEY_CHANNEL);
			if(obj instanceof Channel) {
	             mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
			}
			mOrderBy = bundle.getInt(KEY_SORT_NAME);
//			mNeedAutoInitData = bundle.getBoolean(ChannelActivity.KEY_AUTO_INITDATA);
			mCategory = bundle.getInt(KEY_CATEGORY);
		}
	}
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.channel_all, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mChannelFilterSupply.removeListener(mChannelFilterListener);
	}
	
	public void onSelected() {
		Log.d(TAG, "onSelected");
//		if(mChannelFilterSupply != null) {
//			initData();
//		}
	}
	
	//init
	private void init() {
		initUI();
		initRequestParams();
		initDataSupply();
//		if(mNeedAutoInitData){
		initData();
//		}
	}
	
	private void initUI() {
//		initFilterView();
		initListView();
	}
	
//	private void initFilterView() {
//		mFilterBtn = (TextView) mContentView.findViewById(R.id.channel_all_filter_btn);
//		mFilterBtn.setOnClickListener(mOnClickListener);
//		mFilterBtn.setSelected(false);
//	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.channel_all_list);
		mListView = mLoadingListView.getListView();
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		int height = (int) getResources().getDimension(R.dimen.video_common_secondary_list_top_padding);
		int bottom = (int) getResources().getDimension(R.dimen.size_20);
		mListView.setPadding(0, height, 0, bottom);
		mListView.setClipToPadding(false);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mListView.setOnItemClickListener(mOnMediaClickListener);
		mAdapter = new ChannelRankAdapter(mContext, mChannel, mCategory);
		mAdapter.setShowScroe(mCategory == Channel.CHANNEL_TYPE_MOVIE);
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
				getFilterMedia(prepareFilterMediaStatisticInfo());
			}
		});
	}
	
	private void initRequestParams() {
		mChannelIds = new int[1];
		if(mChannel != null) {
			mChannelIds[0] = mChannel.id;
		}
	}
	
	private void initDataSupply() {
		if(mChannelFilterSupply == null) {
			mChannelFilterSupply = new ChannelFilterSupply();
		}
		mChannelFilterSupply.addListener(mChannelFilterListener);
	}
	
	private void initData() {
//		if(!mIsDataInited) {
			if(mFilterMedias == null || mFilterMedias.size() == 0) {
				getFilterMedia(prepareFilterMediaStatisticInfo());
			} else {
				refreshListView(false);
			}
//			mIsDataInited = true;
//		}
	}
	
	//get data
	private void getFilterMedia(String statisticInfo) {
		if(mFilterMedias != null && mFilterMedias.size() > 0 && !mCanLoadMore) {
			refreshListView(false);
		} else {
			if(mFilterMedias == null || mFilterMedias.size() == 0) {
				mLoadingListView.setShowLoading(true);
			}
			MediaInfoQuery query = new MediaInfoQuery();
			query.orderBy = mOrderBy;
			query.pageNo = mPageNo;
			query.pageSize = mPageSize;
			query.ids = mChannelIds;
			query.statisticInfo = statisticInfo;
			mChannelFilterSupply.getFilterMedias(query);
		}
	}
	
	//packaged method
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
	
//	private void resetListView() {
//		mFilterMedias = null;
//		mPageNo = 1;
//		mCanLoadMore = true;
//		refreshListView(false);
//	}
	
//	private void onFilterViewSelected(int[] selectedChannelIds) {
//		if(selectedChannelIds != null && selectedChannelIds.length > 0) {
//			resetListView();
//			mChannelIds = selectedChannelIds;
//			getFilterMedia(prepareFilterMediaStatisticInfo());
//		}
//	}
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getFilterMedia(null);
			}
		}
	};

	//data callback
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

	//UI callback
	private OnItemClickListener mOnMediaClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (parent != null && parent.getItemAtPosition(position) instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(mContext, MediaDetailActivity.class);
				intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)parent.getItemAtPosition(position));
				intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_CHANNEL_RANK_VALUE);
				mContext.startActivity(intent);
			}
		}
	};
	
	//statistic
	private String prepareFilterMediaStatisticInfo() {
		GetChannelMediaListStatisticInfo  getChannelMediaListStatisticInfo = new GetChannelMediaListStatisticInfo();
        getChannelMediaListStatisticInfo.categoryId = getCategoryId();
        getChannelMediaListStatisticInfo.listType = ChannelMediaInfoListTypeDef.LIST_FEATURE_TYPE_CODE;
        getChannelMediaListStatisticInfo.setFilter(getFilterTypes(), getFilterValues());
		return getChannelMediaListStatisticInfo.formatToJson();
	}
	
	private String getCategoryId() {
    	if(mChannel != null) {
    		StringBuilder categoryId = new StringBuilder();
            categoryId.append(mChannel.name);
            categoryId.append("(");
            categoryId.append(mChannel.id);
            categoryId.append(")");
            return categoryId.toString();
    	}
        return "";
    }
	
	private String[] getFilterTypes() {
		String[] filterTypes = null;
		if(mChannelIds == null || mChannel == null || mChannel.sub == null) {
			return filterTypes;
		}
		
		if(mChannelIds.length == 1) {
			filterTypes = new String[1];
			filterTypes[0] = mChannelIds[0] +"";
		} else {
			Channel[] subChannel = mChannel.sub;
			int filterSize = subChannel.length;
			filterTypes = new String[filterSize];
			for(int i = 0; i < filterSize; i++) {
				if(subChannel[i] == null) {
					break;
				}
	            StringBuilder filterType = new StringBuilder();
	            filterType.append(subChannel[i].name);
	            filterType.append("(");
	            filterType.append(subChannel[i].id);
	            filterType.append(")");
	            filterTypes[i] = filterType.toString();
	        }
		}
		return filterTypes;
	}
	
	private String[] getFilterValues() {
		String[] filterValues = null;
		if(mChannelIds == null) {
			return filterValues;
		}
		
		if(mChannelIds.length == 1) {
			filterValues = new String[1];
			filterValues[0] = mChannelIds[0] +"";
		} else {
			int filterSize = mChannelIds.length;
			filterValues = new String[filterSize];
			for(int i = 0; i < filterSize; i++) {
				filterValues[i] = mChannelIds[i] +"";
			}
		}
		return filterValues;
	}
}
