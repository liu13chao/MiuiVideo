package com.miui.video.dialog;

import java.util.List;

import miui.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.SearchResultActivity;
import com.miui.video.WebMediaActivity;
import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseFragmentDialog;
import com.miui.video.datasupply.MediaDetailInfoSupply;
import com.miui.video.datasupply.MediaDetailInfoSupply.MediaDetailInfoDoneListener;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.db.MediaInfoForPlayerUtil;
import com.miui.video.fragment.DetailCommentFragment;
import com.miui.video.fragment.DetailEpisodeFragment;
import com.miui.video.fragment.DetailEpisodeFragment.OnMediaPlayListener;
import com.miui.video.fragment.DetailIntroduceFragment;
import com.miui.video.fragment.DetailRecommendFragment;
import com.miui.video.local.FavoriteManager;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.model.AuthenticateAccountManager;
import com.miui.video.model.DeviceInfo;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.statistic.MediaSetTypeDef;
import com.miui.video.statistic.OpenMediaStatisticInfo;
import com.miui.video.statistic.PlayStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaSetInfoList;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.UIUtil;
import com.miui.video.util.Util;
import com.miui.video.widget.ActorsView;
import com.miui.video.widget.ActorsView.OnActorViewClickListener;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;
import com.miui.video.widget.statusbtn.StatusBtn;
import com.miui.video.widget.statusbtn.StatusBtnItem;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 *@author tangfuling
 *
 */

public class MediaDetailDialogFragment extends BaseFragmentDialog {
	
	public static final String TAG = MediaDetailDialogFragment.class.getName();
	
	public static String KEY_MEDIA_ID = "mediaId";
	public static String KEY_MEDIA_INFO = "mediaInfo";
	public static String KEY_IS_BANNER = "isBanner";
	public static String KEY_SOURCE_PATH = "enterPathInfo";
	
	//UI
	private ActorsView mActorsView;
	
	private Button[] mPagerTabBtn;
	private PagerView mPagerView;
	private Fragment[] mPages;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	
	private DetailEpisodeFragment mDetailEpisodeFragment;
	private DetailIntroduceFragment mDetailIntroduceFragment;
	private DetailCommentFragment mDetailCommentFragment;
	private DetailRecommendFragment mDetailRecommendFragment;
		
	private int PAGE_EPISODE;
	private int PAGE_INTRODUCE;
	private int PAGE_COMMENT;
	private int PAGE_RECOMMEND;
		
	private TextView mTitle;
		
	private Button mBtnFavorite;
	private StatusBtn mBtnPlay;
	private StatusBtn mBtnDownload;
	private MediaView mMediaView;
		
	//received data
	private int mMediaId;
	private MediaInfo mMediaInfo;
	private String mSourcePath;
		
	//data from network
	private MediaDetailInfo2 mMediaDetailInfo2;
	private MediaDetailInfo mMediaDetailInfo;
	private MediaSetInfoList mMediaSetInfoList;
	private MediaUrlInfoList mMediaUrlInfoList;
	private MediaUrlInfo mMediaUrlInfo;
	private int[] mAllowAbleSources;
	
	//data from local
	private PlayHistory mPlayHistory;
	
	//data for status btn
	private StatusBtnItem mStatusBtnItemPlay;
	private StatusBtnItem mStatusBtnItemDownload;
		
	//data supply
	private MediaDetailInfoSupply mMediaDetailInfoSupply;
	private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
		
	//flags
	private boolean mCanPlay = false;
	private boolean mIsBanner = false;
	private boolean mCurEpisodeLoading = false;
	
	private int mCi = 1;
	private int mPreferSource = -1;
	private int mStyle = MediaConstantsDef.MEDIA_TYPE_VARIETY;
		
	//manager
	private FavoriteManager mFavoriteManager;
	private OfflineMediaManager mOfflineMediaManager;
	private AuthenticateAccountManager mAuthenticateAccountManager;
	private PlayHistoryManager mPlayHistoryManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_detail);
		
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		uploadPlayerStatistic();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mMediaDetailInfoSupply != null) {
			mMediaDetailInfoSupply.removeMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
		if(mPlayHistoryManager != null) {
			mPlayHistoryManager.removeListener(mOnHistoryChangeListener);
		}
	}
	
	//init
	private void init() {
		getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		
		initAuthenticateAccountManager();
		initReceivedData();
		initFragment();
		initManager();
		initUI();
		initData();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		this.mMediaId = intent.getIntExtra(KEY_MEDIA_ID, -1);
		this.mIsBanner = intent.getBooleanExtra(KEY_IS_BANNER, false);
		this.mSourcePath = intent.getStringExtra(KEY_SOURCE_PATH);
		Object mediaInfo = intent.getSerializableExtra(KEY_MEDIA_INFO);
		if(mediaInfo instanceof MediaInfo) {
			this.mMediaInfo = (MediaInfo) mediaInfo;
		}
		
		if(mIsBanner && mMediaInfo != null) {
			mMediaInfo.smallImageURL = null;
		}
	}
	
	private void initCi() {
		if(mPlayHistory instanceof OnlinePlayHistory) {
			OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
			mCi = onlinePlayHistory.mediaCi;
		} else {
			if(mMediaSetInfoList != null && mMediaSetInfoList.isVariety() && mMediaSetInfoList.videos != null) {
				mCi = mMediaSetInfoList.videos.length;
			} else {
				mCi = 1;
			}
		}
	}
	
	private void initFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(MediaDetailDialogFragment.KEY_MEDIA_INFO, mMediaInfo);
		
		mDetailEpisodeFragment = new DetailEpisodeFragment();
		mDetailEpisodeFragment.setArguments(bundle);
		mDetailEpisodeFragment.addMediaPlayListener(mOnMediaPlayListener);
		mDetailIntroduceFragment = new DetailIntroduceFragment();
		mDetailIntroduceFragment.setArguments(bundle);
		mDetailCommentFragment = new DetailCommentFragment();
		mDetailCommentFragment.setArguments(bundle);
		mDetailRecommendFragment = new DetailRecommendFragment();
		mDetailRecommendFragment.setArguments(bundle);
	}
	
	private void initManager() {
		mFavoriteManager = DKApp.getSingleton(FavoriteManager.class);
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		mPlayHistoryManager = DKApp.getSingleton(PlayHistoryManager.class);
		mPlayHistoryManager.addListener(mOnHistoryChangeListener);
		mPlayHistoryManager.loadPlayHistory();
	}
	
	private void initUI() {
		initTitle();
		initMediaSummary();
		initPagerView();
		refreshBtnStatus();
	}
	
	private void initData() {
		initDataSupply();
		getMediaDetailData();
	}
	
	private void initDataSupply() {
		if(mMediaDetailInfoSupply == null) {
			mMediaDetailInfoSupply = DKApp.getSingleton(MediaDetailInfoSupply.class);
			mMediaDetailInfoSupply.addMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
		if(mMediaUrlInfoListSupply == null) {
			mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
			mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
		}
	}
	
	private void initTitle() {
		mTitle = (TextView) findViewById(R.id.detail_media_title);
		if(mMediaInfo != null) {
			mTitle.setText(mMediaInfo.medianame);
		}
	}
	
	private void initMediaSummary() {
		mActorsView = (ActorsView) findViewById(R.id.media_summary_actors);
		mActorsView.setOnActorViewClickListener(mOnActorViewClickListener);
		mBtnFavorite = (Button) findViewById(R.id.detail_media_favorite);
		mBtnFavorite.setOnClickListener(mOnClickListener);
		mBtnDownload = (StatusBtn) findViewById(R.id.detail_media_download);
		mBtnDownload.setOnClickListener(mOnClickListener);
		mBtnPlay = (StatusBtn) findViewById(R.id.detail_media_play);
		mBtnPlay.setOnClickListener(mOnClickListener);
		mMediaView = (MediaView) findViewById(R.id.media_summary_media_view);
		int width = this.getResources().getDimensionPixelSize(R.dimen.detail_media_cover_width);
		int height = this.getResources().getDimensionPixelSize(R.dimen.detail_media_cover_height);
		mMediaView.setMediaViewSize(width, height);
		mMediaView.setOnMediaClickListener(mOnMediaClickListener);
		refreshBtnFavoriteStatus();
		fillMediaInfo(mMediaInfo);
	}
	
	private void initPagerView() {
		if(mMediaInfo != null && !mMediaInfo.isMultiSetType()) {
			initPagerViewSingleSet();
		} else {
			initPagerViewMultiSet();
		}
		showSelectedPage(0);
	}
	
	private void initPagerViewMultiSet() {
		PAGE_EPISODE = 0;
		PAGE_INTRODUCE = 1;
		PAGE_COMMENT = 2;
		PAGE_RECOMMEND = 3;
		
		mPagerTabBtn = new Button[4];
		mPagerTabBtn[0] = (Button) findViewById(R.id.media_detail_pager_episode);
		mPagerTabBtn[1] = (Button) findViewById(R.id.media_detail_pager_introduce);
		mPagerTabBtn[2] = (Button) findViewById(R.id.media_detail_pager_reviewlist);
		mPagerTabBtn[3] = (Button) findViewById(R.id.media_detail_pager_recommend);
		for(int i = 0; i < mPagerTabBtn.length; i++) {
			mPagerTabBtn[i].setOnClickListener(mOnClickListener);
		}
		
		mPages = new Fragment[4];
		mPages[0] = mDetailEpisodeFragment;
		mPages[1] = mDetailIntroduceFragment;
		mPages[2] = mDetailCommentFragment;
		mPages[3] = mDetailRecommendFragment;
		
		mPagerView = (PagerView) findViewById(R.id.media_detail_pager_view);
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setOffscreenPageLimit(4);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
	}
	
	private void initPagerViewSingleSet() {
		PAGE_INTRODUCE = 0;
		PAGE_COMMENT = 1;
		PAGE_RECOMMEND = 2;
		PAGE_EPISODE = 3;
		
		mPagerTabBtn = new Button[4];
		mPagerTabBtn[0] = (Button) findViewById(R.id.media_detail_pager_introduce);
		mPagerTabBtn[1] = (Button) findViewById(R.id.media_detail_pager_reviewlist);
		mPagerTabBtn[2] = (Button) findViewById(R.id.media_detail_pager_recommend);
		mPagerTabBtn[3] = (Button) findViewById(R.id.media_detail_pager_episode);
		for(int i = 0; i < mPagerTabBtn.length; i++) {
			mPagerTabBtn[i].setOnClickListener(mOnClickListener);
		}
		
		android.widget.LinearLayout.LayoutParams params
			= (android.widget.LinearLayout.LayoutParams) mPagerTabBtn[0].getLayoutParams();
		params.leftMargin = 0;
		mPagerTabBtn[0].setLayoutParams(params);
		mPagerTabBtn[3].setVisibility(View.GONE);
		
		mPages = new Fragment[3];
		mPages[0] = mDetailIntroduceFragment;
		mPages[1] = mDetailCommentFragment;
		mPages[2] = mDetailRecommendFragment;
		
		mPagerView = (PagerView) findViewById(R.id.media_detail_pager_view);
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setOffscreenPageLimit(3);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
	}
	
	//get data
	private void getMediaDetailData() {
		if(mMediaDetailInfo2 == null) {
			if(mMediaInfo != null) {
				boolean getAll = true;
				mMediaDetailInfoSupply.getMediaDetailInfo(mMediaInfo.mediaid, getAll, 
						MediaFeeDef.MEDIA_ALL, prepareMediaDetailStatistic());
			} else {
				boolean getAll = true;
				mMediaDetailInfoSupply.getMediaDetailInfo(mMediaId, getAll, 
						MediaFeeDef.MEDIA_ALL, prepareMediaDetailStatistic());
			}
		}
	}
	
	private void getMediaUrlInfoList(int ci) {
		if(mMediaInfo != null) {
			if(mMediaUrlInfo != null && mCi == ci) {
				startWebMediaActivity();
			} else {
				if(mCi != ci) {
					mCi = ci;
				}
				mCurEpisodeLoading = true;
				mMediaUrlInfoListSupply.getMediaUrlInfoList(mMediaInfo.mediaid, mCi, -1);
			}
		}
	}
		
	//packaged method
	private void fillMediaInfo(MediaInfo mediaInfo) {
		if(mediaInfo != null) {
			UIUtil.fillMediaSummary(findViewById(R.id.media_summary), mediaInfo);
		}
	}
	
	private void showSelectedPage(int page) {
		for(int i = 0; i < mPagerTabBtn.length; i++) {
			if(i == page) {
				mPagerTabBtn[i].setSelected(true);
			} else {
				mPagerTabBtn[i].setSelected(false);
			}
		}
		
		if(mPages.length == 4) {
			switch (page) {
			case 2:
				mDetailCommentFragment.onSelected();
				break;
			case 3:
				mDetailRecommendFragment.onSelected();
				break;

			default:
				break;
			}
		} else if(mPages.length == 3){
			switch (page) {
			case 1:
				mDetailCommentFragment.onSelected();
				break;
			case 2:
				mDetailRecommendFragment.onSelected();
				break;

			default:
				break;
			}
		}
	}
	
	private void refreshBtnStatus() {
		if(mStatusBtnItemPlay == null) {
			mStatusBtnItemPlay = new StatusBtnItem();
		}
		if(mStatusBtnItemDownload == null) {
			mStatusBtnItemDownload = new StatusBtnItem();
		}
		
		String str = this.getString(R.string.play);
		if(mMediaInfo != null && mMediaInfo.isMultiSetType()) {
			if(mMediaSetInfoList != null && mMediaSetInfoList.isVariety()) {
				MediaSetInfo mediaSetInfo = getMediaSetInfoByCi(mCi);
				if(mediaSetInfo != null) {
					str = mediaSetInfo.date;
					if(!Util.isEmpty(str) && str.length() > 5) {
						str = str.substring(5);
					}
				}
			} else {
				str = this.getString(R.string.di_count_ji);
				str = String.format(str, mCi);
			}
		}
		mStatusBtnItemPlay.text = str;
		if(mCurEpisodeLoading) {
			mStatusBtnItemPlay.uiStatus = StatusBtn.UI_STATUS_LOADING;
		} else {
			mStatusBtnItemPlay.uiStatus = StatusBtn.UI_STATUS_PLAY;
		}
		mStatusBtnItemPlay.textColorEnable = true;
		mBtnPlay.setStatusBtnItem(mStatusBtnItemPlay);
		
		mStatusBtnItemDownload.uiStatus = StatusBtn.UI_STATUS_DOWNLOAD;
		mStatusBtnItemDownload.text = this.getResources().getString(R.string.offline);
		if (mMediaDetailInfo != null && mMediaDetailInfo.media_available_download_source != null 
				&& mMediaDetailInfo.media_available_download_source.length > 0) {
			mStatusBtnItemDownload.clickable = true;
			mBtnDownload.setVisibility(View.VISIBLE);
		} else {
			mStatusBtnItemDownload.clickable = false;
			mBtnDownload.setVisibility(View.GONE);
		}
		mStatusBtnItemDownload.textColorEnable = true;
		mBtnDownload.setStatusBtnItem(mStatusBtnItemDownload);
	}
	
	private MediaSetInfo getMediaSetInfoByCi(int ci) {
		if(mMediaSetInfoList != null && mMediaSetInfoList.videos != null) {
			for(int i = 0; i < mMediaSetInfoList.videos.length; i++) {
				MediaSetInfo mediaSetInfo = mMediaSetInfoList.videos[i];
				if(mediaSetInfo != null && mediaSetInfo.ci == ci) {
					return mediaSetInfo;
				}
			}
		}
		return null;
	}
		
	private String formatUrl(String url) {
		if (!Util.isEmpty(url)) {
			int pos = url.lastIndexOf("http://");
			if (pos >= 0) {
				url = url.substring(pos, url.length());
			}
		}
		return url;
	}
		
	private void startWebMediaActivity() {
		if(mMediaUrlInfo != null && mCanPlay) {
			Intent intent = new Intent(this, WebMediaActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(WebMediaActivity.KEY_MEDIA_INFO, mMediaInfo);
			intent.putExtra(WebMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PAD_DETAIL_VALUE);
			intent.putExtra(WebMediaActivity.KEY_CI, mCi);
			intent.putExtra(WebMediaActivity.KEY_CLARITY, mMediaUrlInfo.clarity);
			intent.putExtra(WebMediaActivity.KEY_SOURCE, mMediaUrlInfo.mediaSource);
			intent.putExtra(WebMediaActivity.KEY_URL, formatUrl(mMediaUrlInfo.mediaUrl));
			intent.putExtra(WebMediaActivity.KEY_MEDIA_SET_STYLE, mStyle);
			this.startActivity(intent);
		}
	}
		
	private void refreshBtnFavoriteStatus(boolean isFavorite) {
		mBtnFavorite.setSelected(isFavorite);
	}
	
	private void refreshBtnFavoriteStatus() {
		if(mFavoriteManager.isFavorite(mMediaInfo)) {
			refreshBtnFavoriteStatus(true);
		} else {
			refreshBtnFavoriteStatus(false);
		}
	}
	
	private void handlerFavoriteClick() {
		if(mFavoriteManager.isFavorite(mMediaInfo)) {
			mFavoriteManager.delFavorite(mMediaInfo);
			refreshBtnFavoriteStatus(false);
			removeFromMiPush();
			AlertMessage.show(R.string.cancel_favorite_success);
		} else {
			mFavoriteManager.addFavorite(mMediaInfo);
			refreshBtnFavoriteStatus(true);
			showLoginTip();
			addToMiPush();
			AlertMessage.show(R.string.add_favorite_success);
		}
	}
	
	private void addToMiPush() {
		if(mMediaInfo == null || mMediaInfo.isFinished()) {
			return;
		}
		MiPushClient.subscribe(this, String.valueOf(mMediaInfo.mediaid), null);
	}
	
	private void removeFromMiPush() {
		if(mMediaInfo == null) {
			return;
		}
		MiPushClient.unsubscribe(this, String.valueOf(mMediaInfo.mediaid), null);
	}
		
	private void playMedia(int ci) {
		mCanPlay = true;
		getMediaUrlInfoList(ci);
		refreshBtnStatus();
	}
	
	private void downloadMedia(int ci, int preferenceSource) {
		if(mMediaDetailInfo == null) {
			return;
		}
		
		if (mMediaDetailInfo.media_available_download_source == null
				|| mMediaDetailInfo.media_available_download_source.length <= 0) {
				AlertMessage.show(R.string.offline_no_source);
		} else {
			DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
			if(deviceInfo.isWifiUsed()) {
				startDownload(ci, preferenceSource);
			} else {
				if (DKApp.isUseCellular()) {
					startDownload(ci, preferenceSource);
				} else {
					showOfflineDataStreamDialog(ci, preferenceSource);
				}
			}
		}
		
		uploadOfflineStatistic();
	}
	
	private void startDownload(int ci, int preferenceSource) {
		if(mMediaDetailInfo.isMultiSetType()) {
			if(mMediaDetailInfo2 != null) {
				Intent intent = new Intent();
				intent.setClass(this, DownloadSelectDialog.class);
				intent.putExtra(DownloadSelectDialog.KEY_MEDIA_DETAIL_INFO2, mMediaDetailInfo2);
				this.startActivity(intent);
			}
		} else {
			if (mOfflineMediaManager.isInOfflineMediaList(mMediaDetailInfo.mediaid, ci)) {
				AlertMessage.show(R.string.download_is_in_tasklist);				
			} else {
				mOfflineMediaManager.addOfflineMedia(mMediaDetailInfo2, ci, preferenceSource, mAllowAbleSources);
				AlertMessage.show(R.string.download_add_success);
			}
		}
	}
	
	private void startSystemSettingActivity() {
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		startActivity(intent);	
	}
	
	private void showOfflineDataStreamDialog(final int ci, final int preferenceSource) {
		new AlertDialog.Builder(this)
		.setCancelable(true).setTitle(R.string.offline_alert_title)
		.setPositiveButton(R.string.good, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startDownload(ci, preferenceSource);
			}
		})
		.setNegativeButton(R.string.setting, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startSystemSettingActivity();
			}
		})
		.setMessage(R.string.offline_alert_wifi)
		.create().show();
	}
	
	private void showLoginTip() {
		if(mAuthenticateAccountManager.needAuthenticate()) {
			if(mAuthenticateAccountManager.isNoAccount()) {
				AlertDialog loginTipDlg = new AlertDialog.Builder(this)
		        .setCancelable(true).setTitle(R.string.login_tip_title)
		        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				 } )
		        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mAuthenticateAccountManager.authAccount();
					}
		        })
		        .setMessage(R.string.login_tip_content)
		        .create();
				try {
					loginTipDlg.show();
				} catch(Exception e) {
					DKLog.d(TAG, e.getLocalizedMessage());
				}
			} else {
				mAuthenticateAccountManager.authAccount();
			}
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.media_detail_pager_episode:
				mPagerView.setCurPage(PAGE_EPISODE);
				break;
			case R.id.media_detail_pager_introduce:
				mPagerView.setCurPage(PAGE_INTRODUCE);
				break;
			case R.id.media_detail_pager_reviewlist:
				mPagerView.setCurPage(PAGE_COMMENT);
				break;
			case R.id.media_detail_pager_recommend:
				mPagerView.setCurPage(PAGE_RECOMMEND);
				break;
			case R.id.detail_media_favorite:
				handlerFavoriteClick();
				break;
			case R.id.detail_media_play:
				playMedia(mCi);
				break;
			case R.id.detail_media_download:
				downloadMedia(mCi, -1);
				break;
			default:
				break;
			}
		}
	};
	
	private OnActorViewClickListener mOnActorViewClickListener = new OnActorViewClickListener() {
		
		@Override
		public void onActorViewClick(String actorName) {
			Intent intent = new Intent();
			intent.putExtra(SearchResultActivity.SEARCH_KEY_WORD_TAG, actorName);
			intent.setClass(MediaDetailDialogFragment.this, SearchResultActivity.class);
			MediaDetailDialogFragment.this.startActivity(intent);
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaInfo) {
				playMedia(mCi);
			}
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			showSelectedPage(page);
		}
	};
	
	private OnMediaPlayListener mOnMediaPlayListener = new OnMediaPlayListener() {
		
		@Override
		public void onMediaPlay(int ci) {
			mCi = ci;
			mCurEpisodeLoading = false;
			refreshBtnStatus();
		}
	};
	
	//Data callback
	private MediaDetailInfoDoneListener mMediaDetailInfoDoneListener = new MediaDetailInfoDoneListener() {
		
		@Override
		public void onMediaDetailInfoDone(MediaDetailInfo2 mediaDetailInfo,
				boolean isError) {
			mMediaDetailInfo2 = mediaDetailInfo;
			if(mMediaDetailInfo2 != null) {
				mMediaDetailInfo = mMediaDetailInfo2.mediainfo;
				if(mMediaInfo == null) {
					mMediaInfo = mMediaDetailInfo;
					initTitle();
				}
				if(mMediaDetailInfo != null) {
					mAllowAbleSources = mMediaDetailInfo.media_available_download_source;
				}
				if(mIsBanner && mMediaInfo != null && mMediaDetailInfo != null) {
					mMediaInfo.smallImageURL = mMediaDetailInfo.smallImageURL;
				}
				mMediaSetInfoList = mMediaDetailInfo2.mediaciinfo;
				if(mMediaSetInfoList != null) {
					mStyle = mMediaSetInfoList.style;
				}
			}
			
			initCi();
			fillMediaInfo(mMediaInfo);
			refreshBtnStatus();

			MediaInfoForPlayerUtil.getInstance().set(mMediaDetailInfo2);
		}
	};
	
	private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {
		
		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			mCurEpisodeLoading = false;
			refreshBtnStatus();
			mMediaUrlInfoList = mediaUrlInfoList;
			mMediaUrlInfo = mMediaUrlInfoListSupply.filterMediaUrlInfoList(mMediaUrlInfoList, -1);
			startWebMediaActivity();
		}
	};
	
	private OnHistoryChangedListener mOnHistoryChangeListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			if(mMediaInfo != null) {
				PlayHistory playHistory = mPlayHistoryManager.getPlayHistoryById(mMediaInfo.mediaid);
				mPlayHistory = playHistory;
				if(mPlayHistory instanceof OnlinePlayHistory) {
					OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
					mCi = onlinePlayHistory.mediaCi;
				}
			}
		}
	};
	
	//auth callback
	private void initAuthenticateAccountManager() {
		mAuthenticateAccountManager = new AuthenticateAccountManager(MediaDetailDialogFragment.this) {
			
			@Override
			protected void onAuthSuccess() {

			}

			@Override
			protected void onAuthFailed(String failedReason) {
				AlertMessage.show(failedReason);
			}

			@Override
			protected void onAuthNoAccount() {
				
			}
			
		};
	}
	//statistic
	private void uploadPlayerStatistic() {
		Thread uploadPlayerStatisticThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//set play info
		        PlayStatisticInfo  playStatisticInfo = PlayStatisticInfo.getInstance();
		        String statisticInfo = playStatisticInfo.getPlayInfo(MediaDetailDialogFragment.this);
		        if( !Util.isEmpty(statisticInfo)) {
		        	DKApi.setPlayInfo(statisticInfo);
		        	playStatisticInfo.clearPlayInfo(MediaDetailDialogFragment.this);
		        }
			}
		});
		uploadPlayerStatisticThread.start();
	}
	
	private void uploadOfflineStatistic() {
		if(mMediaInfo == null) {
			return;
		}
		ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
		statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_OFFLINE;
		statisticInfo.mediaId = mMediaInfo.mediaid;
		statisticInfo.ci = mCi;
		statisticInfo.mediaSource = mPreferSource;
		statisticInfo.sourcePath = SourceTagValueDef.PAD_DETAIL_VALUE;
		DKApi.uploadComUserData(statisticInfo.formatToJson());
	}

	private String prepareMediaDetailStatistic() {
		OpenMediaStatisticInfo  openMediaStatisticInfo = new OpenMediaStatisticInfo();
		if(mMediaInfo != null) {
			openMediaStatisticInfo.mediaId = mMediaInfo.mediaid;
			if(mMediaInfo.isMultiSetType()) {
				openMediaStatisticInfo.ci = -1;
			} else {
				openMediaStatisticInfo.ci = mCi;
			}
		}
		openMediaStatisticInfo.sourcePath = mSourcePath;
		openMediaStatisticInfo.mediaSetType = MediaSetTypeDef.getMediaSetType(this, mMediaInfo);
		return openMediaStatisticInfo.formatToJson();
	}
}
