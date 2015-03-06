package com.miui.video.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.ChannelActivity;
import com.miui.video.ChannelFilterActivity;
import com.miui.video.ChannelSubActivity;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.ChannelRecommendAdapter;
import com.miui.video.adapter.FilterItemAdapter;
import com.miui.video.controller.MediaViewClickHandler;
import com.miui.video.controller.RecommendationComposer;
import com.miui.video.controller.RecommendationComposer.UIActionListener;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.banner.BannerView;

/**
 *@author tangfuling
 *
 */

public class ChannelChoiceFragment extends Fragment {
	public static final String TAG = "ChannelChoiceFragment";
	
//	private static final int PAGE_NO = 1;
//	private static final int PAGE_SIZE = 6;
//	private static final int CHANNEL_SIZE = 15;
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private int mEmptyViewTopMargin;
	private GridView mGridView;
	private FilterItemAdapter mFilterAdapter;
	
	// Recommend
	private ChannelRecommendAdapter mRecommendAdapter;
	private RecommendationComposer mRecommendationComposer;

	   
	//header view
	private View mHeaderView;
	private BannerView mBannerView;
	
	ArrayList<String> mFilterNames = new ArrayList<String>();
	ArrayList<Channel> mFilterChannels = new ArrayList<Channel>();
	
	//received data
//	private Channel mChannel;
	private int mChannelId = -1;
	private int mCategory;
	
//	private ServiceRequest mRecommendRequest;
//	private ServiceRequest mBannerRequest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object obj = bundle.get(ChannelActivity.KEY_CHANNEL);
			if(obj instanceof Channel) {
				mChannelId = ((Channel) obj).id;
			}
			mCategory = bundle.getInt(ChannelActivity.KEY_CATEGORY);
		}
	}
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.channel_choice, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    Log.d(TAG, "onActivityCreated");
		mContext = getActivity();
		init();
		if(mChannelId <= 0){
		    mLoadingListView.setEmptyView(mEmptyView, mEmptyViewTopMargin);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		mBannerView.startIndicateTask();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		mBannerView.stopIndicateTask();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		cancel(mRecommendRequest);
//		cancel(mBannerRequest);
		if(mRecommendationComposer != null){
		    mRecommendationComposer.release();
		}
	}
	
	   public void startBannerIndicator(){
	        if(mBannerView != null){
	            mBannerView.startIndicateTask();
	        }
	    }
	    
	    public void stopBannerIndicator(){
	        if(mBannerView != null){
	            mBannerView.stopIndicateTask();
	        }
	    }
	
	public BannerView getBannerView() {
		return mBannerView;
	}
	
	private void init() {
	    initUI();
	    initRecommend();
	}
	
	private void initUI() {
	    initDimen();
		initListView();
		initFilterView();
	}
	
	private void initDimen(){
	       mEmptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.channel_choice_list);
		mListView = mLoadingListView.getListView();
        mListView.setClipToPadding(false);
//        int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
        mListView.setPadding(0, paddingTop,  0, 0);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setSelector(R.drawable.transparent);
		mRecommendAdapter = new ChannelRecommendAdapter(mContext);
		mListView.setAdapter(mRecommendAdapter);
		mHeaderView = View.inflate(mContext, R.layout.channel_banner, null);
		mHeaderView.setVisibility(View.GONE);
		mBannerView = (BannerView) mHeaderView.findViewById(R.id.channel_banner_view);
		mBannerView.setVisibility(View.GONE);
		mListView.addHeaderView(mHeaderView);
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
			}
		});
	}
	
	private void initFilterView(){
        mGridView = (GridView) mHeaderView.findViewById(R.id.channel_all_filter_btn);
        mFilterAdapter = new FilterItemAdapter(getActivity());
	    Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannelId);
	    if(channel != null){
            int counter = 0; int MAX_COUNTER = 7;
            if(channel.subfilter != null && channel.subfilter.length > 0){
                for(Channel c : channel.subfilter){
                    if(c != null && c.subfilter != null && c.subfilter.length > 0){
                        for(Channel cc : c.subfilter){
                            if(counter < MAX_COUNTER){
                                counter ++;
                            }else{
                                break;
                            }
                            mFilterNames.add(cc.name);
                            mFilterChannels.add(cc);
                        }
                        if(counter == MAX_COUNTER){
                            break;
                        }
                    }
                }
            }
            if(mFilterNames.size() == 0){
                mGridView.setVisibility(View.GONE);
            }else{
                mFilterAdapter.setFilterItems(mFilterNames);
                mGridView.setAdapter(mFilterAdapter);
                mGridView.setOnItemClickListener(mFilterItemClickListener);
            }
	    }else{
	        mGridView.setVisibility(View.GONE);
	    }
	}
	
	private void initRecommend(){
	    if(mChannelId > 0){
	        mRecommendAdapter.setViewClickHandler(new MediaViewClickHandler(getActivity(),
	                SourceTagValueDef.PHONE_V6_CHANNEL_CHOICE_VALUE));
	        mRecommendationComposer = new RecommendationComposer(mBannerView, 
	                mChannelId);
	        loadData();
	    }
	}
	
	private void showHeaderView(){
	    if(mHeaderView != null){
	        if(mBannerView != null && mBannerView.getVisibility() == View.VISIBLE){
	            mHeaderView.setVisibility(View.VISIBLE);
	        }else if(mFilterAdapter != null && mFilterAdapter.getCount() > 0){
	               mHeaderView.setVisibility(View.VISIBLE);
	        }
	    }
	}
	
	private void loadData(){
	    mLoadingListView.setShowLoading(true);
	    if(mRecommendationComposer != null){
	        mRecommendationComposer.action(mRecommendListener);   
	    }
	}
	
	OnItemClickListener mFilterItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		    Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannelId);
		    if(channel == null){
		        return;
		    }
			if(position < mFilterChannels.size()){
				Intent intent = new Intent();
				intent.putExtra(ChannelActivity.KEY_ROOT_CHANNEL, channel);
				intent.putExtra(ChannelActivity.KEY_CHANNEL, mFilterChannels.get(position));
				intent.putExtra(ChannelActivity.KEY_CATEGORY, mCategory);
				intent.setClass(getActivity(), ChannelSubActivity.class);
				startActivity(intent);
			}else{
				Intent intent = new Intent();
				intent.putExtra(ChannelActivity.KEY_CHANNEL, channel);
				intent.setClass(getActivity(), ChannelFilterActivity.class);
				startActivity(intent);
			}
		}
	};
	
//	private void initData() {
//		getChoiceMedia();
//		getBannerMedia();
//	}
	
//	private void cancel(ServiceRequest request) {
//		if (request != null) {
//			request.cancelRequest();
//		}
//	}
	
//	private void getSelectedMedia(int id){
//		cancel(mRecommendRequest);
//		if (mChannel != null) {
//			mLoadingListView.setShowLoading(true);
//			MediaInfoQuery q = new MediaInfoQuery();
//			q.pageNo = PAGE_NO;
//			q.pageSize = PAGE_SIZE;
//			q.ids = new int[1];
//			q.ids[0] = id;
//			q.statisticInfo = prepareChoiceStatisticInfo();
//			mRecommendRequest = DKApi.getChannelRecommendation(q, false, this);
//		}
//	}
	
//	//get data
//	private void getChoiceMedia() {
//		cancel(mRecommendRequest);
//		if (mChannel != null) {
//			mLoadingListView.setShowLoading(true);
//			MediaInfoQuery q = new MediaInfoQuery();
//			q.pageNo = PAGE_NO;
//			q.pageSize = PAGE_SIZE;
//			q.ids = new int[CHANNEL_SIZE];
//			for (int i = 0; i < q.ids.length; i++) {
//				q.ids[i] = mChannel.id;
//			}
//			q.statisticInfo = prepareChoiceStatisticInfo();
//			mRecommendRequest = DKApi.getChannelRecommendation(q, false, this);
//		}
////		if (mChannel != null && mChannel.sub != null) {
////			DKLog.e(TAG, "size: " + mChannel.sub.length);
////			mLoadingListView.setShowLoading(true);
////			MediaInfoQuery q = new MediaInfoQuery();
////			q.pageNo = PAGE_NO;
////			q.pageSize = PAGE_SIZE;
////			q.ids = new int[mChannel.sub.length];
////			for (int i = 0; i < q.ids.length; i++) {
////				Channel channel = mChannel.sub[i];
////				if (channel != null) {
////					q.ids[i] = channel.id;
////				}
////			}
////			q.statisticInfo = prepareChoiceStatisticInfo();
////			mRecommendRequest = DKApi.getChannelRecommendation(q, false, this);
////		}
//	}
	
//	private void getBannerMedia() {
//		cancel(mBannerRequest);
//		if (mChannel != null) {
//		    BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
//          statisticInfo.cateogry = mChannel.name;
//			mBannerRequest = DKApi.getBannerList(mChannel.id,  statisticInfo.formatToJson(), this);
//		} else {
//			mLoadingListView.setEmptyView(mEmptyView, mEmptyViewTopMargin);
//		}
//	}

	// data callback
//	@Override
//	public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
//		if (response instanceof ChannelRecommendationResponse) {
//			mLoadingListView.setShowLoading(false);
//			if (response.isSuccessful()) {
//				ChannelRecommendationResponse recommendations = (ChannelRecommendationResponse) response;
//				if (recommendations.data != null && recommendations.data.length > 0 && 
//						checkRecommendData(recommendations.data)) {
//					ChannelChoiceAdapter adapter = new ChannelChoiceAdapter(mContext, mChannel);
//					adapter.setOnMediaInfoSelectListener(mListener);
//					adapter.setGroup(recommendations.data);
//					mListView.setAdapter(adapter);
//				} else {
//					mLoadingListView.setEmptyView(mEmptyView, mEmptyViewTopMargin);
//				}
//			} else {
//				mLoadingListView.setEmptyView(mRetryView, mEmptyViewTopMargin);
//			}
//		} else if (response instanceof BannerListResponse) {
//			BannerListResponse banners = (BannerListResponse) response;
//			mBannerView.setBanners(banners.data);
//		}
//	}
	
//	private boolean checkRecommendData(ChannelRecommendation[] data){
//	    //TODO:
////		for(ChannelRecommendation item : data){
////			if (item == null || item.getRecommendCount() > 0){
////				return false;
////			}
////		}
//		return true;
//	}
//	
//	@Override
//	public void onProgressUpdate(ServiceRequest request, int progress) {
//	}
	
//	// UI callback
//	private OnMediaInfoSelectListener mMediaInfoListener = new OnMediaInfoSelectListener() {
//		@Override
//		public void OnMediaInfoSelect(MediaInfo media) {
//			Intent intent = new Intent(mContext, MediaDetailActivity.class);;
//			intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, media);
//			intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_CHANNEL_CHOICE_VALUE);
//			mContext.startActivity(intent);
//		}
//	};
	
//	//statistic
//	private String prepareChoiceStatisticInfo() {
//		 GetChannelMediaListStatisticInfo  getChannelMediaListStatisticInfo = new GetChannelMediaListStatisticInfo();
//         getChannelMediaListStatisticInfo.categoryId = getCategoryId();
//         getChannelMediaListStatisticInfo.listType = ChannelMediaInfoListTypeDef.LIST_HOT_TYPE_CODE;
//		return getChannelMediaListStatisticInfo.formatToJson();
//	}
//	
//    private String getCategoryId() {
//    	if(mChannel != null) {
//    		StringBuilder categoryId = new StringBuilder();
//            categoryId.append(mChannel.name);
//            categoryId.append("(");
//            categoryId.append(mChannel.id);
//            categoryId.append(")");
//            return categoryId.toString();
//    	}
//        return "";
//    }
    
    public boolean isHitBannerView(MotionEvent event){
        if(mBannerView != null && isAdded()){
            Rect hitRect = new Rect();
            mBannerView.getHitRect(hitRect);
            int bannerMarginTop =  (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
            int x = (int) event.getX();
            int y = (int) (event.getY() - bannerMarginTop);
            if (hitRect.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    private UIActionListener mRecommendListener = new UIActionListener() {
        @Override
        public void onRecommendListener(boolean successful) {
            mLoadingListView.setShowLoading(false);
            if(!successful) {
                mLoadingListView.setEmptyView(mRetryView, mEmptyViewTopMargin);
            }else{
                showHeaderView();
                mRecommendAdapter.setChannelRecommendations(
                        mRecommendationComposer.getRecommendations());
            }
        }
        
        @Override
        public void onBannerListener(boolean successful) {
            if(successful){
                if(mBannerView != null) {
                    mBannerView.setVisibility(View.VISIBLE);
                }
            }
        }
    };
}
