package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.miui.video.ChannelActivity;
import com.miui.video.DKApp;
import com.miui.video.FeatureListActivity;
import com.miui.video.FeatureMediaActivity;
import com.miui.video.R;
import com.miui.video.adapter.OnlineVideoAdapter;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.model.loader.BannerLoader;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.model.loader.SpecialSubjectLoader;
import com.miui.video.screenfit.ScreenFitHelper;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.tv.TvChannelActivity;
import com.miui.video.tv.TvEpgManager;
import com.miui.video.tv.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.tv.TvPlayManager;
import com.miui.video.type.Banner;
import com.miui.video.type.Channel;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.ShowBaseInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaViewRow;
import com.miui.video.widget.reflect.ReflectedView;

/**
 *@author tangfuling
 *
 */

public class OnlineVideoFragment extends Fragment {
	
	private static String TAG = OnlineVideoFragment.class.getName();
	
	private Context context;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private RetryView mRetryView;
	private OnlineVideoAdapter mOnlineVideoAdapter;
	
	//header view
	private View mHeaderView;
	private ReflectedView mBannerReflectedView;
	private MediaViewRow mBannerViewRow;
	private MediaView[] mBannerViews;
	private View mBannerCategoryWrapper;
	
	private TextView mBannerCategoryName;
	private Button mBannerBtnMore;
	private MediaViewRow mBannerCategoryRow;
	private Object[] mBannerCategoryContents;

	private int mBlurRadius;
	private int mMirrorHeight;
	private float mMirrorAlpha = 0.3f;
	
	//foot view
	private View mFootView;
	private Button mFootBtnMore;
	private MediaViewRow mFootCategoryRow;
	private MediaView[] mFootMediaViews;
	
	//data from network
	private ArrayList<Object> mBannerVideos;
	private ArrayList<Object> mFootVideos;
	
	//recommend channel for list view and banner
	private ArrayList<Channel> mListChannels;
	private Channel mBannerChannel;
	private TelevisionInfo[] mTvInfos;
	
	//data supply
	private SpecialSubjectLoader mSpecialSubjectLoader;
	private BannerLoader mBannerLoader;
	private RecommendationLoader mRecommendationLoader;

	private int mBannerSizePerRow;
	private int mRecommendSizePerRow;
	private int mOnlineVideoSizePerRow;
	
	public static String SP_SEARCH_RECOMMEND = "sp_search_recommend";
	public static String KEY_SEARCH_RECOMMEND = "key_search_recommend";
	
	//manager
	private TvEpgManager mTvEpgManager;
	
	//flags
	private boolean mEnableTv = true;
	private boolean mCanAccessNet = true;
	
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
	public void onDestroy() {
		super.onDestroy();
		mSpecialSubjectLoader.removeListener(mLoadListener);
		mBannerLoader.removeListener(mLoadListener);
		mRecommendationLoader.removeListener(mLoadListener);
		mTvEpgManager.removeListeners(mTvUpdateInterface);
	}
	
	public void enableAccessNet() {
		mCanAccessNet = true;
		initData();
	}
	
	public void disableAccessNet() {
		mCanAccessNet = false;
	}
	
	//init
	private void init() {
		context = getActivity();
		initConfig();
		initManager();
		initUI();
		initDataLoader();
		
		if(mCanAccessNet) {
			initData();
		}
	}
	
	private void initConfig() {
		mBannerSizePerRow = ScreenFitHelper.getBannerSizePerRow();
		mRecommendSizePerRow = ScreenFitHelper.getVideoSizePerRow();
		mOnlineVideoSizePerRow = ScreenFitHelper.getVideoSizePerRow();
	}
	
	private void initManager() {
		mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initListView() {
		mBlurRadius = getResources().getDimensionPixelSize(R.dimen.media_banner_blur_radius);
		mMirrorHeight = getResources().getDimensionPixelSize(R.dimen.online_video_banner_mirror_height);
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.online_video_list);
		mListView = mLoadingListView.getListView();
		mListView.setVerticalFadingEdgeEnabled(true);
		mListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		initHeaderView();
		initFootView();
		hideHeadAndFootView();
		
		mOnlineVideoAdapter = new OnlineVideoAdapter(context, mOnlineVideoSizePerRow);
		mListView.setAdapter(mOnlineVideoAdapter);
		mOnlineVideoAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mOnlineVideoAdapter.setOnClickListener(mOnClickListener);
		
		mLoadingView = View.inflate(context, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mRetryView = new RetryView(context);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getData();
			}
		});
	}
	
	private void initHeaderView() {
		mHeaderView = View.inflate(context, R.layout.online_video_banner, null);
		
		mBannerReflectedView = (ReflectedView) mHeaderView.findViewById(R.id.online_banner_reflected_view);
		mBannerViewRow = new MediaViewRow(context);
		initHeaderBanner();
		
		mBannerCategoryWrapper = mHeaderView.findViewById(R.id.online_banner_category_wrapper);
		
		mBannerCategoryName = (TextView) mHeaderView.findViewById(R.id.online_banner_category_name);
		mBannerBtnMore = (Button) mHeaderView.findViewById(R.id.online_banner_btn_more);
		mBannerBtnMore.setOnClickListener(mOnClickListener);
		
		mBannerCategoryRow = (MediaViewRow) mHeaderView.findViewById(R.id.online_banner_category_media_row);
		initHeaderCategory();
		
		mListView.addHeaderView(mHeaderView);
	}
	
	private void initHeaderBanner() {
		mBannerViews = new MediaView[mBannerSizePerRow];
		for(int i = 0; i < mBannerSizePerRow; i++) {
			mBannerViews[i] = new MediaView(context, MediaViewHelper.UI_BANNER_TYPE);
			mBannerViews[i].setOnMediaClickListener(mOnMediaClickListener);
			mBannerViews[i].setShowText(false);
		}
		mBannerViewRow.setMediaViews(mBannerViews);
		mBannerReflectedView.setSelfView(mBannerViewRow);
		mBannerReflectedView.setMirrorAlpha(mMirrorAlpha);
		mBannerReflectedView.setMirrorHeight(mMirrorHeight);
		mBannerReflectedView.setBlurRadius(mBlurRadius);
		mBannerReflectedView.setMirrorMask(R.drawable.media_banner_refrect_mask);
		mBannerViewRow.setMirrorCanvas(mBannerReflectedView.getMirrorCanvas());
	}
	
	private void initHeaderCategory() {
		mBannerCategoryContents = new Object[mRecommendSizePerRow];
		mBannerCategoryRow.setShowText(false);
		mBannerCategoryRow.setMediaViewContents(mBannerCategoryContents);
		mBannerCategoryRow.setOnMediaClickListener(mOnMediaClickListener);
	}
	
	private void initFootView() {
		mFootView = View.inflate(context, R.layout.online_video_foot, null);
		mFootBtnMore = (Button) mFootView.findViewById(R.id.online_foot_btn_more);
		mFootCategoryRow = (MediaViewRow) mFootView.findViewById(R.id.online_foot_item_row);
		mFootBtnMore.setOnClickListener(mOnClickListener);
		
		initFootMediaViews();
		
		mListView.addFooterView(mFootView);
	}
	
	private void initFootMediaViews() {
		mFootMediaViews = new MediaView[mBannerSizePerRow];
		for(int i = 0; i < mBannerSizePerRow; i++) {
			mFootMediaViews[i] = new MediaView(context, MediaViewHelper.UI_BANNER_TYPE);
			mFootMediaViews[i].setOnMediaClickListener(mOnMediaClickListener);
			mFootMediaViews[i].setShowText(false);
		}
		mFootCategoryRow.setMediaViews(mFootMediaViews);
	}
	
	private void initData() {
		getData();
	}
	
	private void initDataLoader() {
		mSpecialSubjectLoader = new SpecialSubjectLoader();
		mBannerLoader = new BannerLoader(-1, "");
		mRecommendationLoader = new RecommendationLoader();
		mSpecialSubjectLoader.addListener(mLoadListener);
		mBannerLoader.addListener(mLoadListener);
		mRecommendationLoader.addListener(mLoadListener);
	}
	
	//get data
	private void getData() {
		mLoadingListView.setShowLoading(true);
		getSpecialSubject();
		getBanner();
		getRecommendVideos();
	}
	
	private void getSpecialSubject() {
		mSpecialSubjectLoader.load();
	}
	
	private void getBanner() {
		mBannerLoader.load();
	}
	
	private void getRecommendVideos() {
		mRecommendationLoader.load();
	}
	
	//packaged method
	private void refreshBannerRefrectViewHeight() {
		mMirrorHeight = getResources().getDimensionPixelSize(R.dimen.online_video_banner_mirror_height);
		mBannerViewRow.setMediaViews(mBannerViews);
		mBannerReflectedView.setSelfView(mBannerViewRow);
		mBannerReflectedView.setMirrorAlpha(mMirrorAlpha);
		mBannerReflectedView.setMirrorHeight(mMirrorHeight);
		mBannerViewRow.setMirrorCanvas(mBannerReflectedView.getMirrorCanvas());
	}
	
	private void refreshBannerCategoryWrapperTopMargin() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = getResources().getDimensionPixelSize(R.dimen.online_video_banner_top_margin);
		mBannerCategoryWrapper.setLayoutParams(params);
	}
	
	private void refresh(boolean isError) {
		if(mListChannels != null && mListChannels.size() > 0
				&& mBannerVideos != null && mBannerVideos.size() > 0) {
			mLoadingListView.setShowLoading(false);
			mBannerBtnMore.setTag(mBannerChannel);
			showHeadAndFootView();
			refreshBanner();
			refreshFoot();
			refreshListView();
			return;
		}
		if(isError) {
			mLoadingListView.setShowLoading(false);
			mLoadingListView.setEmptyView(mRetryView);
		}
	}
	
	private void refreshListView() {
		if(mListChannels != null && mListChannels.size() > 0) {
			mOnlineVideoAdapter.setSizePerRow(mOnlineVideoSizePerRow);
			mOnlineVideoAdapter.setRecommendChannels(mListChannels, mRecommendationLoader);
		}
	}
	
	private void refreshFoot() {
		if(mFootVideos != null && mFootVideos.size() > 0) {
			if(mFootMediaViews.length != mBannerSizePerRow) {
				initFootMediaViews();
			}
			for(int i = 0; i < mFootMediaViews.length; i++) {
				if(i < mFootVideos.size()) {
					Object obj = mFootVideos.get(i);
					mFootMediaViews[i].setContentInfo(obj);
				}
			}
		}
	}
	
	private void refreshBanner() {
		//refresh banner
		if(mBannerVideos != null && mBannerVideos.size() > 0) {
			if(mBannerViews.length != mBannerSizePerRow) {
				initHeaderBanner();
			}
			for(int i = 0; i < mBannerViews.length; i++) {
				if(i < mBannerVideos.size()) {
					Object obj = mBannerVideos.get(i);
					if(obj instanceof Banner) {
						Banner banner = (Banner) obj;
						if(banner.mediaInfo != null) {
							mBannerViews[i].setContentInfo(banner.mediaInfo);
						} else if(banner.specialSubjectInfo != null) {
							mBannerViews[i].setContentInfo(banner.specialSubjectInfo);
						}
					} else {
						mBannerViews[i].setContentInfo(obj);
					}
				}
			}
		}
		
		//refresh recommend for banner
		if(mBannerChannel != null) {
			if(mBannerCategoryContents.length != mRecommendSizePerRow) {
				initHeaderCategory();
			}
			mBannerCategoryName.setText(mBannerChannel.name);
			ShowBaseInfo[] bannerRecommends = mRecommendationLoader.getRecommendation(mBannerChannel);
			if(bannerRecommends != null) {
				for(int i = 0; i < mBannerCategoryContents.length; i++) {
					if(i < bannerRecommends.length) {
						Object obj = bannerRecommends[i];
						mBannerCategoryContents[i] = obj;
					}
				}
			}
		}
		mBannerCategoryRow.setMediaViewContents(mBannerCategoryContents);
	}
	
	private void divideRecommendChannels(ArrayList<Channel> channels) {
		if(channels == null || channels.size() == 0) {
			return;
		}
		
		//divide for banner
		mBannerChannel = channels.get(0);
		
		//divide for list
		if(channels.size() > 1) {
			mListChannels = new ArrayList<Channel>(channels);
			mListChannels.remove(0);
			
			//remove tv channel
			if(!mEnableTv) {
				for(int i = 0; i < mListChannels.size(); i++) {
					Channel channel = mListChannels.get(i);
					if(channel.isTvChannel()) {
						mListChannels.remove(channel);
					}
				}
			} else {
				mTvInfos = mRecommendationLoader.getTvRecommendation();
				mTvEpgManager.addTelevisionInfo(mTvInfos);
			}
		}
	}
	
	private void hideHeadAndFootView() {
		if(mHeaderView != null) {
			mHeaderView.setVisibility(View.GONE);
		}
		if(mFootView != null) {
			mFootView.setVisibility(View.GONE);
		}
	}
	
	private void showHeadAndFootView() {
		if(mHeaderView != null) {
			mHeaderView.setVisibility(View.VISIBLE);
		}
		if(mFootView != null) {
			mFootView.setVisibility(View.VISIBLE);
		}
	}
	
	private void saveSearchRecommend(String[] searchRecommend) {
		if(searchRecommend == null) {
			return;
		}
		
		LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
		for(int i = 0; i < searchRecommend.length; i++) {
			hashSet.add(searchRecommend[i]);
		}
		try {
			SharedPreferences sp = context.getSharedPreferences(SP_SEARCH_RECOMMEND, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putStringSet(KEY_SEARCH_RECOMMEND, hashSet);
			editor.commit();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}
	
	private void mergeExpiredTvInfo() {
		if(mTvInfos == null) {
			return;
		}
		
		for(int i = 0; i < mTvInfos.length; i++) {
			if(mTvInfos[i] != null) {
				int tvId = mTvInfos[i].mediaid;
				TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);
				if(expiredTvInfo != null) {
					mTvInfos[i] = expiredTvInfo;
				}
			}
		}
	}
	
	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof TelevisionInfo) {
				TelevisionInfo televisionInfo = (TelevisionInfo) media;
				TvPlayManager.playChannel(getActivity(), televisionInfo, SourceTagValueDef.PAD_HOME_ONLINE_VALUE);
			} else if(media instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(context, MediaDetailDialogFragment.class);
				intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, (MediaInfo)media);
				intent.putExtra(MediaDetailDialogFragment.KEY_IS_BANNER, mediaView.isBanner());
				if(mediaView.isBanner()) {
					intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_HOME_BANNER_VALUE);
				} else {
					intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_HOME_ONLINE_VALUE);
				}
				context.startActivity(intent);
			} else if(media instanceof SpecialSubject) {
				SpecialSubject specialSubject = (SpecialSubject) media;
				Intent intent = new Intent();
				intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
				intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PAD_HOME_SPECIAL_VALUE);
				intent.setClass(getActivity(), FeatureMediaActivity.class);
				startActivity(intent);
			}
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.online_video_btn_more || id == R.id.online_banner_btn_more) {
				Object tag = v.getTag();
				if(tag instanceof Channel) {
					Channel channel = (Channel) tag;
					if(channel.isTvChannel()) {
						Intent intent = new Intent();
						intent.putExtra(TvChannelActivity.KEY_CHANNEL, channel);
						intent.setClass(getActivity(), TvChannelActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.putExtra(ChannelActivity.KEY_CHANNEL, channel);
						intent.setClass(getActivity(), ChannelActivity.class);
						startActivity(intent);
					}
				}
			} else if(id == R.id.online_foot_btn_more) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), FeatureListActivity.class);
				startActivity(intent);
			}
		}
	};

	//data callback
	private LoadListener mLoadListener = new LoadListener() {
		
		@Override
		public void onLoadFinish(DataLoader loader) {
			if(loader instanceof SpecialSubjectLoader){
				mFootVideos = mSpecialSubjectLoader.getSpecialSubjectList();
				refreshFoot();
				return;
			}
			mBannerVideos = mBannerLoader.getBanners();
			saveSearchRecommend(mBannerLoader.getSearchKeywords());
			divideRecommendChannels(mRecommendationLoader.getChannels());
			refresh(false);
		}
		
		@Override
		public void onLoadFail(DataLoader loader) {
			if(loader instanceof SpecialSubjectLoader) {
				return;
			}
			refresh(true);
		}
	};
	
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			refreshListView();
		}
	};
	
	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mBannerSizePerRow = ScreenFitHelper.getBannerSizePerRow();
		mRecommendSizePerRow = ScreenFitHelper.getVideoSizePerRow();
		mOnlineVideoSizePerRow = ScreenFitHelper.getVideoSizePerRow();
		
		refresh(false);
		refreshBannerRefrectViewHeight();
		refreshBannerCategoryWrapperTopMargin();
	}
}
