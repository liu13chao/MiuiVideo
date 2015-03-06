package com.miui.video.live;

import java.text.SimpleDateFormat;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseActivity;
import com.miui.video.live.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.live.popup.TvCtrlForecastPopup;
import com.miui.video.live.popup.TvCtrlSelectTvPopup;
import com.miui.video.live.popup.TvCtrlSelectTvPopup.TvSelectedListener;
import com.miui.video.statistic.TvStaticInfo;
import com.miui.video.statistic.TvStaticInfoList;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.controller.ControllerView;
import com.miui.videoplayer.controller.ControllerView.OnControlEventListener;
import com.miui.videoplayer.controller.Settings;
import com.miui.videoplayer.framework.popup.BrightnessPopupWindow;
import com.miui.videoplayer.framework.popup.VolumePopupWindow;
import com.miui.videoplayer.framework.popup.WindowFactory;
import com.miui.videoplayer.media.IMediaPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.statistic.StartPlayStatistics;
import com.miui.videoplayer.videoview.IVideoView;
import com.miui.videoplayer.videoview.IVideoView.OnVideoLoadingListener;
import com.miui.videoplayer.widget.AdView;
import com.miui.videoplayer.widget.AdView.NotifyAdsPlayListener;
import com.miui.videoplayer.widget.MenuItemView;
import com.miui.videoplayer.widget.MenuView;

public class TvPlayerActivity extends BaseActivity {
	
	private static final String TAG = TvPlayerActivity.class.getName();
	
	public static final String KEY_TV_INFO = "key_tv_info";
	//UI
	private View mDecorView;
	private View mLoadView;
	private TextView mLoadPercentView;
	private TextView mLoadTopTitle;
	private TextView mLoadTopSubTitle;
	private View mLoadTopBackImg;
	
	private View mBgView;
	private View mBufferView;
	
	private View mCtrlView;
	private TextView mCtrlTopTitle;
	private TextView mCtrlTopSubTitle;
	private View mCtrlTopBack;

    private ControllerView mControllerView;
	
    /* Popup */
    private VolumePopupWindow mVolumePopupWindow;
    private BrightnessPopupWindow mBrightnessPopupWindow;
	
	private MenuView mCtrlMenu;
	private TvCtrlSelectTvPopup mCtrlSelectTv;
	private TvCtrlForecastPopup mCtrlForecast;
	
	private MenuItemView mMenuItemSelectTv;
	private MenuItemView mMenuItemForecast;
	private MenuItemView mMenuItemScaleScreen;
	
	// player
    private IVideoView mVideoView;
    private AdView mAdView;
    private ViewGroup mRootView;
	
	//data
	private TelevisionInfo mCurTvInfo = new TelevisionInfo();	
	private TvEpgManager mTvEpgManager;
	private AudioManager mAudioManager;
	private Settings mSetting;
	
	private Handler mHandler = new Handler();
	private final int SHOW_NULL_VIEW_DELAY = 2000;
	
	//exit double click
	private long mLastClickTime;
	private int DOUBLE_CLICK_INTERVAL = 5000;
	
	//statistic
	private TvStaticInfoList mTvStatisticInfoList = new TvStaticInfoList();
	private TvStaticInfo mStatisticInfo = new TvStaticInfo();
	private String mTvEntry = "";
	
	public static final String KEY_TV_ENTRY = "key_tv_entry";
	
	//flag
	private boolean mIsFullScreen = false;
	@SuppressLint("SimpleDateFormat")
	static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private StartPlayStatistics mStartPlayStatistics;
	private UUID mUUID;
	private boolean isAdsPlaying = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_player);
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if(mVideoView != null){
            mVideoView.start();
            mVideoView.onActivityResume();
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mVideoView != null){
            mVideoView.pause();
            mVideoView.onActivityPause();
        }
		mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseVideoView();
		if(mTvEpgManager != null) {
			mTvEpgManager.removeListeners(mTvUpdateInterface);
		}
		mStatisticInfo.endTime = System.currentTimeMillis() / 1000.0;
		uploadTvStaticInfo(mTvStatisticInfoList.formatToJson());
		if(mSetting != null){
		      mSetting.saveSettings();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || 
				event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Log.i(TAG, "handle VOLUME UP");
			getVolumeWindow().adjustVolume(this, event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP 
					?  - 1 : 1).show(mControllerView, this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//init
	private void init() {
		initReceivedData();
		initUI();
		initData();
		playTv(mCurTvInfo);
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		mTvEntry = intent.getStringExtra(KEY_TV_ENTRY);
		Object obj = intent.getSerializableExtra(KEY_TV_INFO);
		if(obj instanceof TelevisionInfo) {
			mCurTvInfo = (TelevisionInfo) obj;			
		}
	}
	
    private void initData() {
    	mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		mSetting = new Settings(this);
		mSetting.loadSettings();
    }
	
	private void initUI() {
		mRootView = (ViewGroup)findViewById(R.id.root);
		mDecorView = getWindow().getDecorView();
		mControllerView = (ControllerView)findViewById(R.id.controller);
		mControllerView.setGestureListener(mGestureListener);
		initBgView();
		initBufferView();
		initLoadView();
		initCtrlView();
	}
	
	private void initBgView() {
		mBgView = findViewById(R.id.tv_bg_view);
	}
	
	private void initBufferView() {
		mBufferView = findViewById(R.id.tv_buffer_view);
	}
	
	private void initLoadView() {
		mLoadView = findViewById(R.id.tv_load_view);
		mLoadView.setVisibility(View.INVISIBLE);
		mLoadTopTitle = (TextView) mLoadView.findViewById(R.id.vp_top_title);
		mLoadTopSubTitle = (TextView) mLoadView.findViewById(R.id.vp_top_sub_title);
		mLoadTopBackImg = mLoadView.findViewById(R.id.vp_top_back);
		mLoadPercentView = (TextView) mLoadView.findViewById(R.id.vp_loading_percent);
		mLoadTopBackImg.setOnClickListener(mOnClickListener);
	}
	
	private void initCtrlView() {
		mCtrlView = findViewById(R.id.tv_ctrl_view);
		initCtrlTop();
		initCtrlMenu();
		initCtrlSelectTvForecast();
//		initCtrlVolumeBrightness();
	}
	
	private void initCtrlTop() {
		if(mCurTvInfo != null) {
			mCtrlTopTitle = (TextView) mCtrlView.findViewById(R.id.vp_top_title);
			mCtrlTopSubTitle = (TextView) mCtrlView.findViewById(R.id.vp_top_sub_title);
			refreshCtrlTopView();
		}
		mCtrlTopBack = mCtrlView.findViewById(R.id.vp_top_back);
		mCtrlTopBack.setOnClickListener(mOnClickListener);
	}
	
	private void initCtrlMenu() {
		mCtrlMenu = (MenuView) findViewById(R.id.tv_ctrl_menu);
		mMenuItemSelectTv = new MenuItemView(this);
		mMenuItemSelectTv.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemSelectTv.setIcon(R.drawable.icon_select_tv);
		mMenuItemSelectTv.setText(R.string.select_tv);
		mMenuItemSelectTv.setOnClickListener(mOnClickListener);
		mCtrlMenu.addLeftMenu(mMenuItemSelectTv);
		mMenuItemForecast = new MenuItemView(this);
		mMenuItemForecast.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemForecast.setIcon(R.drawable.icon_forecast);
		mMenuItemForecast.setText(R.string.forecast);
		mMenuItemForecast.setOnClickListener(mOnClickListener);
		mMenuItemForecast.setDividerVisibility(View.INVISIBLE);
		mCtrlMenu.addLeftMenu(mMenuItemForecast);
		
		mMenuItemScaleScreen = new MenuItemView(this);
		mMenuItemScaleScreen.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemScaleScreen.setOnClickListener(mOnClickListener);
		mMenuItemScaleScreen.setDividerVisibility(View.INVISIBLE);
		mCtrlMenu.addRightMenu(mMenuItemScaleScreen);
		if(mIsFullScreen) {
			setMenuItemScaleToDefault();
		} else {
			setMenuItemScaleToFullScreen();
		}
	}
	
	private void initCtrlSelectTvForecast() {
		mCtrlSelectTv = new TvCtrlSelectTvPopup(this);
		mCtrlSelectTv.setTvSelectedListener(mTvSelectedListener);
		mCtrlSelectTv.setOnDismissListener(mOnDismissListener);
		
		mCtrlForecast = new TvCtrlForecastPopup(this);
		mCtrlForecast.setOnDismissListener(mOnDismissListener);
	}
	
//	private void initCtrlVolumeBrightness() {
//		mCtrlVolumeBrightness = new TvCtrlVolumeBrightness(this);
//		mCtrlVolumeBrightness.setBrightness(true, true);
//	}
	
    private void releaseVideoView(){
        if(mVideoView != null){
            mVideoView.close();
            mRootView.removeView(mVideoView.asView());
            mVideoView = null;
        }
    }
    
    private void createVideoView(int source) {
        releaseVideoView();
        mVideoView = LiveFactory.createLiveVideoView(this, source);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener); 
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener); 
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnVideoLoadingListener(mOnVideoLoadingListener);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoView.asView().setLayoutParams(params);
        mVideoView.asView().setBackgroundColor(Color.BLACK);
        mRootView.addView(mVideoView.asView(), 0);
        initAdView();
    }
    
    private void initAdView(){
        if(mAdView != null){
            mRootView.removeView(mAdView);
            mAdView = null;
        }
        mAdView = new AdView(this);
        mAdView.setNotifyAdsPlayListener(mAdsListener);
        mAdView.setVisibility(View.GONE);
        mVideoView.setAdsPlayListener(mAdView);
        mVideoView.attachAdView(mAdView);
        mRootView.addView(mAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
    }
	
//	private void initSurfaceView() {
//		mSurfaceView = (SurfaceView) findViewById(R.id.tvsurface);
//		SurfaceHolder holder = mSurfaceView.getHolder();
//		holder.setKeepScreenOn(true);
//		holder.addCallback(mSurfaceHolderCallback);
//	}
	
//	private void initTvPlayer(SurfaceHolder holder) {
//		if(mIplayer == null){
//			mIplayer = IplayerFactory.create(this, holder, mCurTvInfo.source);
//			mLastSource = mCurTvInfo.source;
//		}else{
//			mIplayer.setDisplay(holder);
//		}
//		
//		setListenerForPlayer();
//		prepareAndPlayTvChannel();
////		playCurTvInfo();
//	}
	
	private boolean noCmccPlayInfo(TelevisionShow show){
		if(show == null || Util.isEmpty(show.cmccplayinfo)){
			return true;
		}
		return false;
	}
	
	private void prepareAndPlayTvChannel(){
		if((mCurTvInfo == null) ||
				(mCurTvInfo.source == 0 && Util.isEmpty(String.valueOf(mCurTvInfo.epgid))) ||
				(mCurTvInfo.source == 1 && Util.isEmpty(mCurTvInfo.cmccid) && 
				noCmccPlayInfo(mCurTvInfo.currentprogramme))){
			return;
		}
		prepareTvChannel();
		playTvChannel();
	}
	
	private void prepareTvChannel(){
//		hideBufferingView();
//		initLoadingView();
//		initmCtrlView();
//		initMyFavoriteView();
//		showLoadView();
		checkNetWorkConnectivity();
	}
	
    private void playTvChannel(){
        if(mVideoView != null){
    		updateStatisticInfo();
        	LivePlayInfo playInfo = new LivePlayInfo();
            playInfo.setTvId(mCurTvInfo.getChannelId());
            playInfo.setTvChannelName(mCurTvInfo.getChannelName());
            TelevisionShow tvShow = mCurTvInfo.currentprogramme;
            if(tvShow != null){
                playInfo.setTvPrograme(tvShow.videoname);
            }
            mVideoView.setPlayInfo(playInfo);
            mVideoView.setDataSource(LiveFactory.createLivePlayInfo(this, mCurTvInfo));
            mVideoView.start();
        }
		mTvEpgManager.addTelevisionInfo(mCurTvInfo);
    }
    
    private void playTv(TelevisionInfo tvInfo){
        if(tvInfo != null){
        	mUUID = UUID.randomUUID();
        	mStartPlayStatistics = new StartPlayStatistics(tvInfo, mUUID);
            mCurTvInfo = tvInfo;
            createVideoView(tvInfo.source);
            prepareAndPlayTvChannel();
        }
    }
	
	private void checkNetWorkConnectivity(){
		if(!AndroidUtils.isNetworkConncected(TvPlayerActivity.this)){
			Toast.makeText(TvPlayerActivity.this, R.string.check_network, Toast.LENGTH_LONG).show();
		}
	}

	private void updateStatisticInfo(){
		if(mStatisticInfo != null) {
			// save last play.
			mStatisticInfo.endTime = System.currentTimeMillis() / 1000.0;
		}
		mStatisticInfo = new TvStaticInfo();
		mStatisticInfo.tvId = mCurTvInfo.getChannelId();
		mStatisticInfo.entry = mTvEntry;
		mStatisticInfo.source = mCurTvInfo.source;
		mStatisticInfo.startTime = System.currentTimeMillis() / 1000.0;
		mTvStatisticInfoList.addTvStaticInfo(mStatisticInfo);
	}
	
	private void refreshCtrlTopView() {
		if(mCurTvInfo == null) {
			return;
		}
		mCtrlTopTitle.setText(mCurTvInfo.getChannelName());
		if(mCurTvInfo.getCurrentShow() != null) {
			mCtrlTopSubTitle.setText(mCurTvInfo.getCurrentShow().videoname);
		}
	}
	
	private void refreshLoadTopView() {
		if(mCurTvInfo != null) {
			mLoadTopTitle.setText(mCurTvInfo.getChannelName());
			if(mCurTvInfo.getCurrentShow() != null) {
				mLoadTopSubTitle.setText(mCurTvInfo.getCurrentShow().videoname);
			}
		}
	}
	
	private void refreshLoadPercent(int percent) {
		String str = TvPlayerActivity.this.getResources().getString(R.string.vp_loading_video);
//		str = String.format(str, percent);
		mLoadPercentView.setText(str);
	}
	
	//show view
	private void showLoadView() {
		refreshLoadTopView();
		refreshLoadPercent(0);
		mLoadView.setVisibility(View.VISIBLE);
		mBgView.setVisibility(View.INVISIBLE);
		mBufferView.setVisibility(View.INVISIBLE);
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		mCtrlView.setVisibility(View.INVISIBLE);
		mLoadView.bringToFront();
		dismissPopupViews();
	}
	
	private void showBufferView() {
		if(isLoadViewShowing()) {
			return;
		}
		mBufferView.setVisibility(View.VISIBLE);
		mBufferView.bringToFront();
	}
	
	private void hideBufferView() {
		mBufferView.setVisibility(View.INVISIBLE);
	}
	
	private void showCtrlView(boolean showStatusBar) {
		if(isLoadViewShowing()) {
			return;
		}
		if(showStatusBar) {
			mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		} else {
			mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
		mCtrlView.setVisibility(View.VISIBLE);
		mBgView.setVisibility(View.VISIBLE);
		mLoadView.setVisibility(View.INVISIBLE);
		mBufferView.setVisibility(View.INVISIBLE);
		mCtrlView.bringToFront();
		dismissPopupViews();
		mHandler.removeCallbacks(mShowPlayViewRunnable);
		mHandler.postDelayed(mShowPlayViewRunnable, SHOW_NULL_VIEW_DELAY);
	}
	
	private void showSelectTvView() {
		showNullView();
		mHandler.removeCallbacks(mShowPlayViewRunnable);
		mBgView.setVisibility(View.VISIBLE);
		mCtrlSelectTv.show(mCtrlView, mCurTvInfo);
	}
	
	private void showForecastView() {
		showNullView();
		mHandler.removeCallbacks(mShowPlayViewRunnable);
		mBgView.setVisibility(View.VISIBLE);
		mCtrlForecast.show(mCtrlView, mCurTvInfo);
	}
	
	private void dismissPopupViews() {
//		mCtrlVolumeBrightness.dismiss();
		if(mCtrlSelectTv.isShowing()) {
			mCtrlSelectTv.dismiss();
		}
		if(mCtrlForecast.isShowing()) {
			mCtrlForecast.dismiss();
		}
	}
	
	private void showNullView() {
		mBgView.setVisibility(View.INVISIBLE);
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		mCtrlView.setVisibility(View.INVISIBLE);
		mLoadView.setVisibility(View.INVISIBLE);
		mBufferView.setVisibility(View.INVISIBLE);
		dismissPopupViews();
	}
	
	private boolean isCtrlViewShowing() {
		return mCtrlView.getVisibility() == View.VISIBLE;
	}
	
	private boolean isLoadViewShowing() {
		return mLoadView.getVisibility() == View.VISIBLE;
	}
	
	private boolean isPopViewsShowing() {
		return mCtrlForecast.isShowing() || mCtrlSelectTv.isShowing();
	}
	
    private void mergeExpiredTvInfo() {
    	if(mCurTvInfo == null) {
    		return;
    	}
		int tvId = mCurTvInfo.getChannelId();
		TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);;
		if(expiredTvInfo != null) {
			mCurTvInfo = expiredTvInfo;
		}
    }
	
	private void ctrlViewDoTouch() {
		if(isCtrlViewShowing()) {
			showNullView();
		} else {
			showCtrlView(true);
		}
	}
	
	private void setMenuItemScaleToDefault() {
		mMenuItemScaleScreen.setIcon(R.drawable.icon_scale_default);
		mMenuItemScaleScreen.setText(R.string.defaults);
	}
	
	private void setMenuItemScaleToFullScreen() {
		mMenuItemScaleScreen.setIcon(R.drawable.icon_scale_fullscreen);
		mMenuItemScaleScreen.setText(R.string.fullscreen);
	}
	
    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
			showNullView();
			showCtrlView(false);
			mStatisticInfo.playTime = System.currentTimeMillis() / 1000.0;
			if(mStartPlayStatistics != null){
				mStartPlayStatistics.onPrepared(isAdsPlaying);
			}
			DKLog.d(TAG, "on prepared isAdsPlaying:" + isAdsPlaying);
        }
    };

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener = new OnVideoSizeChangedListener(){
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
        }
    };

    private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        	if(isLoadViewShowing()) {
				refreshLoadPercent(percent);
			}
//            if(percent == 100){
//                TelevisionUtil.tvStaticInfo.loadingTime = System.currentTimeMillis() / 1000.0 - startTime;
//            } 
        }
    };

    private OnInfoListener mOnInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
//            if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//                System.out.println("MEDIA_INFO_BUFFERING_START");
//                mBufferingView.setVisibility(View.VISIBLE);
//            }else if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_UPDATE) {
//            }else if(what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_END){
//                System.out.println("MEDIA_INFO_BUFFERING_END");
//                mBufferingView.setVisibility(View.INVISIBLE);
//            }
//            return false;
			if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
				mStatisticInfo.bufferCount++;
				showBufferView();
			}else if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_UPDATE) {
			}else if(what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_END){
				hideBufferView();
			}
			return false;
        }		
    };	
    
    private OnVideoLoadingListener mOnVideoLoadingListener = new OnVideoLoadingListener() {
        @Override
        public void onVideoLoading(IVideoView videoView) {
        	Log.d(TAG, "onVideoLoading");
        	if(mLoadView == null || mLoadView.getVisibility() != View.VISIBLE){
        		mStatisticInfo.loadingStartTime = System.currentTimeMillis()/1000.0;
            }
        	showLoadView();
        }

		@Override
		public void onVideoHideLoading(IVideoView videoView) {
			// TODO Auto-generated method stub
			
		}
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
    		if(!AndroidUtils.isNetworkConncected(TvPlayerActivity.this)){
    			Toast.makeText(TvPlayerActivity.this, R.string.check_network, Toast.LENGTH_LONG).show();
    		}	
        }
    };

 
    private OnErrorListener mOnErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(IMediaPlayer mp, int what, int extra) {
            Log.e(TAG, "onError : what : " + what + ", extra : "+  extra);
			return false;
		}
	};
	
//	//UI callback
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		doTouch(event);
//		return true;
//	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//			mCtrlVolumeBrightness.doKeyDown(keyCode, event);
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	public void onBackPressed() {
		long curTime = System.currentTimeMillis();
		if(mLastClickTime != 0 && curTime - mLastClickTime < DOUBLE_CLICK_INTERVAL) {
			this.finish();
		}
		AlertMessage.show(R.string.key_back_down_toast);
		mLastClickTime = curTime;
	}
	
	private OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
		
		@Override
		public void onAudioFocusChange(int focusChange) {
			
		}
	};
	
//	private void initPlayer(SurfaceHolder holder){
//		if(mIplayer == null){
//			mIplayer = IplayerFactory.create(this, holder, mCurTvInfo.source);
//			mLastSource = mCurTvInfo.source;
//		}else{
//			mIplayer.setDisplay(holder);
//		}
//		prepareAndPlayTvChannel();
//	}
	
	private TvSelectedListener mTvSelectedListener = new TvSelectedListener() {
		
		@Override
		public void onTvSelected(TelevisionInfo tvInfo) {
			if(tvInfo != null){
				playTv(tvInfo);
				refreshCtrlTopView();
			}
//			if(tvInfo == null) {
//				return;
//			}
//			if(mCurTvInfo == null || mCurTvInfo.mediaid != tvInfo.mediaid) {
//				mCurTvInfo = tvInfo;
//				mTvEntry = SourceTagValueDef.PAD_TV_PLAYER_VALUE;
//				playCurTvInfo();
//				refreshCtrlTopView();
//			}
//			mCurTvInfo = tvInfo;
//			jiashi2yidong();
//			mIplayer.pause();
//			if(mCurTvInfo.source != mLastSource){
//				mIplayer.release();
//				mIplayer = null;
//				initPlayer(mSurfaceView.getHolder());
//			}else{				
//				prepareAndPlayTvChannel();
//			}

		}
	};
	
//	public void jiashi2yidong(){
//		switch (mCurTvInfo.mediaid) {
//		case 47:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10271938";
//			break;
//		case 32:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10271945";
//			break;
//		case 31:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10271946";
//			break;
//		case 36:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10271948";
//			break;
//		case 102:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10349739";
//			break;
//		case 104:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10301644";
//			break;
//		case 53:
//			mCurTvInfo.source = 1;
//			mCurTvInfo.cmccid = "10446848";
//			break;
//		default:
//			break;
//		}
//	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mCtrlTopBack) {
				TvPlayerActivity.this.finish();
			} else if(v == mLoadTopBackImg) {
				TvPlayerActivity.this.finish();
			} else if(v == mMenuItemSelectTv) {
				showSelectTvView();
			} else if(v == mMenuItemForecast) {
				showForecastView();
			} else if(v == mMenuItemScaleScreen) {
				if(mIsFullScreen) {
					setMenuItemScaleToFullScreen();
				} else {
					setMenuItemScaleToDefault();
				}
				mIsFullScreen = !mIsFullScreen;
				if(mVideoView != null){
					mVideoView.setForceFullScreen(mIsFullScreen);
				}
			}
		}
	};
	
	private OnDismissListener mOnDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			showNullView();
		}
	};
	
	//data callback
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			refreshCtrlTopView();
		}
	};
	
	//handler task
	private Runnable mShowPlayViewRunnable = new Runnable() {
		
		@Override
		public void run() {
			showNullView();
		}
	};
	
	//statistic
	private void uploadTvStaticInfo(String tvStatistic) {
		MediaInfoQuery q = new MediaInfoQuery();
		q.pageNo = 1;
		q.pageSize = -1;
		q.ids = new int[] { -1 };
		DKApi.getTelevisionShowInfo(q, tvStatistic, null);
	}
	
    private NotifyAdsPlayListener mAdsListener = new NotifyAdsPlayListener() {
        @Override
        public void onNotifyAdsStart() {
            mStatisticInfo.adStartTime = System.currentTimeMillis()/1000.0;
            isAdsPlaying = true;
        }
        
        @Override
        public void onNotifyAdsEnd() {
        	mStatisticInfo.adEndTime = System.currentTimeMillis()/1000.0;
        	isAdsPlaying = false;
        }
        
        @Override
        public void onAdsDuration(int duration) {
        	mStatisticInfo.adDuration = duration;
        }
    };
    
    private BrightnessPopupWindow getBrightnessWindow(){
        if(mBrightnessPopupWindow == null){
            mBrightnessPopupWindow = WindowFactory.createBrightnessWindow(this);
        }
        return mBrightnessPopupWindow;
    }
    
    private void hideBrightnessWindow(){
        if(mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing()){
            mBrightnessPopupWindow.dismiss();
        }
    }
    
    private VolumePopupWindow getVolumeWindow(){
        if(mVolumePopupWindow == null){
            mVolumePopupWindow = WindowFactory.createVolumeWindow(this);
        }
        return mVolumePopupWindow;
    }
    
    private void hideVolumeWindow(){
        if(mVolumePopupWindow != null && mVolumePopupWindow.isShowing()){
            mVolumePopupWindow.dismiss();
        }
    }
    
    private OnControlEventListener mGestureListener = new OnControlEventListener(){
        @Override
        public void onTouchMove(int region, float movementX, float movementY) {
            if(isCtrlViewShowing()){
                return; // do nothing.
            }
            if(region == REGION_LEFT){
                getBrightnessWindow().adjustBrightness(TvPlayerActivity.this, movementY).show(mControllerView, TvPlayerActivity.this);
            }else if(region == REGION_RIGHT){
                getVolumeWindow().adjustVolume(TvPlayerActivity.this, movementY).show(mControllerView, TvPlayerActivity.this);
            }else if(region == REGION_CENTER){
                // do nothing.
            }
        }

        @Override
        public void onTap(int region) {
            if(region == REGION_LEFT){
                if(!isCtrlViewShowing()){
                    getBrightnessWindow().show(mControllerView, TvPlayerActivity.this);
                }
            }else if(region == REGION_RIGHT){
                if(!isCtrlViewShowing()){
                    getVolumeWindow().show(mControllerView, TvPlayerActivity.this);
                }
            }else{
                if(mVideoView != null && !mVideoView.isAdsPlaying()){
                    hideBrightnessWindow();
                    hideVolumeWindow();
                    if(isPopViewsShowing()) {
                        dismissPopupViews();
                    } else {
                        ctrlViewDoTouch();
                    }
                }
            }
        }

        @Override
        public void onTouchUp(int region) {
        }
    };
}
