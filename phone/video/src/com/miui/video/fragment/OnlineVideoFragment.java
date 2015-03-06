package com.miui.video.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.ChannelRecommendAdapter;
import com.miui.video.base.BaseFragment;
import com.miui.video.controller.ChannelEntryHandler;
import com.miui.video.controller.MediaViewClickHandler;
import com.miui.video.controller.RecommendationComposer;
import com.miui.video.controller.RecommendationComposer.UIActionListener;
import com.miui.video.live.TvEpgManager;
import com.miui.video.live.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.model.ImageManager;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.QuickEntryView;
import com.miui.video.widget.QuickEntryView.OnQuickEntryItemClickListener;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.banner.BannerView;
import com.miui.video.widget.banner.BannerView.OnBannerSelectListener;

/**
 *@author tangfuling
 *
 */

public class OnlineVideoFragment extends BaseFragment {
	
	public static String TAG = OnlineVideoFragment.class.getName();
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private RetryView mRetryView;
	
	//header view
	private View mHeaderView;
	private BannerView mBannerView;
	private QuickEntryView mQuickEntryView;
	
//	//data from network
//	private ArrayList<Object> mBanners;
//	
//	//recommend channel for list view and banner
//	private ArrayList<Channel> mListChannels;
	private ChannelRecommendAdapter mRecommendAdapter;
	private RecommendationComposer mRecommendationComposer;
	private TelevisionInfo[] mTvInfos;
	
//	//data supply
//	private BannerLoader mBannerLoader;
//	private RecommendationLoader mRecommendationLoader;
	
	//manager
	private TvEpgManager mTvEpgManager;
	
//	//flags
//	private boolean mEnableTv = true;
	
	private OnBannerSelectListener mBannerSelectListener;
	private OnQuickEntryItemClickListener mQuickEntryItemClickListener;
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    mContentView = inflater.inflate(R.layout.online_video, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startBannerIndicator();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		stopBannerIndicator();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mTvEpgManager.removeListeners(mTvUpdateInterface);
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
	
	public BannerView getBannerView() {
		return mBannerView;
	}
	
	public void setOnBannerSelectListener(OnBannerSelectListener listener) {
		this.mBannerSelectListener = listener;
	}
	
	public void setQuickEntryItemClickListener(OnQuickEntryItemClickListener listener) {
		this.mQuickEntryItemClickListener = listener;
	}
	
	private void init() {
		mContext = getActivity();
		initManager();
		initUI();
		initDataLoader();
		initData();
	}
	
	private void initManager() {
		mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
	}
	
	private void initUI() {
		initListView();
		mRecommendationComposer = new RecommendationComposer(mBannerView, -1);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.online_video_list);
		mListView = mLoadingListView.getListView();
		
		initHeaderView();
		
		mRecommendAdapter = new ChannelRecommendAdapter(mContext);
		mListView.setAdapter(mRecommendAdapter);
		mListView.setOnScrollListener(mOnScrollListener);
		mRecommendAdapter.setViewClickHandler(new MediaViewClickHandler
		        (getActivity(), SourceTagValueDef.PHONE_V6_HOME_ONLINE_VALUE));
		mRecommendAdapter.setChannelEntryHandler(new ChannelEntryHandler(getActivity()));
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.online_video_empty_top_margin);
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView, emptyViewTopMargin);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				loadData();
			}
		});
	}
	
	private void initHeaderView() {
	    mHeaderView = View.inflate(mContext, R.layout.online_video_header, null);
	    mHeaderView.setVisibility(View.GONE);
		mBannerView = (BannerView) mHeaderView.findViewById(R.id.online_banner_view);
		mBannerView.setVisibility(View.GONE);
		mBannerView.setListener(mBannerSelectListener);
		mQuickEntryView = (QuickEntryView) mHeaderView.findViewById(R.id.online_entry_view);
		mQuickEntryView.setOnQuickEntryItemClickListener(mQuickEntryItemClickListener);
		mListView.addHeaderView(mHeaderView);
	}
	
	private void initData() {
		loadData();
	}
	
	private void initDataLoader() {
//		mBannerLoader = new BannerLoader(-1, "");
//		mRecommendationLoader = new RecommendationLoader();
//		mBannerLoader.addListener(mLoadListener);
//		mRecommendationLoader.addListener(mLoadListener);
	}
	
	//get data
	private void loadData() {
		mLoadingListView.setShowLoading(true);
		mRecommendationComposer.action(mRecommendListener);
//		getBanner();
//		getRecommendVideos();
	}
	
//	private void getBanner() {
//		mBannerLoader.load();
//	}
//	
//	private void getRecommendVideos() {
//		mRecommendationLoader.load();
//	}

	private void refreshListView() {
	    if(!isAdded()){
	        return;
	    }
	    if(mRecommendAdapter != null){
	        mRecommendAdapter.refresh();
	    }
	}
	 
//	private void refreshListView(boolean isError) {
//	    if(isAdded()){
//	        mLoadingListView.setShowLoading(false);
//	        if(mListChannels != null && mListChannels.size() > 0) {
//	            mOnlineVideoAdapter.setRecommendChannels(mListChannels, mRecommendationLoader);
//	        }
//	        int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.online_video_empty_top_margin);
//	        if(isError) {
//	            mLoadingListView.setShowLoading(false);
//	            mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
//	        }
//	    }
//	}
//	
//	private void refreshBanner() {
//	    if(isAdded()){
//	        if(mBanners != null && mBanners.size() > 0){
//	            mBannerView.setBanners(mBanners);
//	            if(mHeaderView != null) {
//	                mHeaderView.setVisibility(View.VISIBLE);
//	            }
//	        }
//	    }
//	}
	
//	private void prepareRecommendChannels(List<Channel> channels) {
//		mListChannels = new ArrayList<Channel>(channels);
//		//remove tv channel
//		if(!mEnableTv) {
//			for(int i = 0; i < mListChannels.size(); i++) {
//				Channel channel = mListChannels.get(i);
//				if(channel.isTvChannel()) {
//					mListChannels.remove(channel);
//				}
//			}
//		} else {
//			mTvInfos = mRecommendationLoader.getTvRecommendation();
//			mTvEpgManager.addTelevisionInfo(mTvInfos);
//		}
//	}
//	
//	private void hideHeaderView() {
//	}
	
//	private void saveSearchRecommend(String[] searchRecommend) {
//		if(searchRecommend == null) {
//			return;
//		}
//		LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
//		for(int i = 0; i < searchRecommend.length; i++) {
//			hashSet.add(searchRecommend[i]);
//		}
//		DKApp.getSingleton(AppSettings.class).saveSearchRecommend(hashSet);
//	}
	
	private void mergeExpiredTvInfo() {
		if(mTvInfos == null) {
			return;
		}
		for(int i = 0; i < mTvInfos.length; i++) {
			if(mTvInfos[i] != null) {
				int tvId = mTvInfos[i].getChannelId();
				TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);
				if(expiredTvInfo != null) {
				    expiredTvInfo.backgroundcolor = mTvInfos[i].backgroundcolor;
	                expiredTvInfo.channelname = mTvInfos[i].channelname;
	                expiredTvInfo.posterurl = mTvInfos[i].posterurl;
					mTvInfos[i] = expiredTvInfo;
				}
			}
		}
	}
	
	//UI callback
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		    if(scrollState == OnScrollListener.SCROLL_STATE_FLING) {
		        ImageManager.getInstance().pause();
		    } else {
		        ImageManager.getInstance().resume();
		    }
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};
	
//	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view,
//				int position, long id) {
//			Object obj = parent.getItemAtPosition(position);
//			if(obj instanceof TelevisionInfo) {
//				TelevisionInfo televisionInfo = (TelevisionInfo) obj;
//				TvPlayManager.playChannel(getActivity(), televisionInfo, SourceTagValueDef.PHONE_V6_HOME_ONLINE_VALUE);
//			} else if(obj instanceof MediaInfo) {
//				Intent intent = new Intent();
//				intent.setClass(mContext, MediaDetailActivity.class);
//				intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)obj);
//				intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_HOME_ONLINE_VALUE);
//				mContext.startActivity(intent);
//			} else if(obj instanceof SpecialSubject) {
//				SpecialSubject specialSubject = (SpecialSubject) obj;
//				Intent intent = new Intent();
//				intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
//				intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_HOME_SPECIAL_VALUE);
//				intent.setClass(getActivity(), FeatureMediaActivity.class);
//				startActivity(intent);
//			} else if (obj instanceof AddonInfo) {
//				AddonInfo addonInfo = (AddonInfo) obj;
//				new AddonHandler(getActivity(), null).onAddonClick(addonInfo);
//			} else if(obj instanceof InformationData) {
//				InformationData informationData = (InformationData) obj;
//				if(informationData.mediaid == 0){
//					InfoPlayManager.playInformation(mContext, informationData, SourceTagValueDef.PHONE_V6_HOME_ONLINE_VALUE);
//				}else{
//					Intent intent = new Intent();
//					intent.putExtra(InfoChannelPlayActivity.KEY_INFODATA, informationData);
//					intent.setClass(getActivity(), InfoChannelPlayActivity.class);
//					startActivity(intent);
//				}
//			}
//		}
//	};

//	//data callback
//	private LoadListener mLoadListener = new LoadListener() {
//		@Override
//		public void onLoadFinish(DataLoader loader) {
//			if(loader instanceof BannerLoader){
//				mBanners = mBannerLoader.getBanners();
//				DKApp.getSingleton(OnlineBgBmpManager.class).setBanners(mBanners);
//				saveSearchRecommend(mBannerLoader.getSearchKeywords());
//				refreshBanner();
//			} else if(loader instanceof RecommendationLoader){
//				List<Channel> channels = mRecommendationLoader.getChannels();
//				prepareRecommendChannels(channels);
//				mQuickEntryView.setChannels(channels);
//				refreshListView(false);
//			}
//		}
//		
//		@Override
//		public void onLoadFail(DataLoader loader) {
//			if(loader instanceof RecommendationLoader){
//				refreshListView(true);
//			}
//		}
//	};
//	
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			refreshListView();
		}
	};
	
	//self def class
	public interface OnBannerChangeListener {
		public void onBannerChanged(int position);
	}
	
	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//TODO:
//		refreshBanner();
//		refreshListView(false);
	}
	
	private UIActionListener mRecommendListener = new UIActionListener() {
        @Override
        public void onRecommendListener(boolean successful) {
            mLoadingListView.setShowLoading(false);
            int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.online_video_empty_top_margin);
            if(successful) {
                mTvInfos = mRecommendationComposer.getTelevisionRecommends();
                mTvEpgManager.addTelevisionInfo(mTvInfos);
                mRecommendAdapter.setChannelRecommendations(
                        mRecommendationComposer.getRecommendations());
            }else{
                mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
            }
            if(mRecommendAdapter != null && mRecommendAdapter.getCount() > 0){
                if(mHeaderView != null) {
                    mHeaderView.setVisibility(View.VISIBLE);
                }
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
