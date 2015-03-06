package com.miui.video.tv;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.duokan.MediaPlayer;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseActivity;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.statistic.TvStaticInfo;
import com.miui.video.statistic.TvStaticInfoList;
import com.miui.video.tv.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.tv.popup.TvCtrlForecastPopup;
import com.miui.video.tv.popup.TvCtrlSelectTvPopup;
import com.miui.video.tv.popup.TvCtrlSelectTvPopup.TvSelectedListener;
import com.miui.video.tv.popup.TvCtrlVolumeBrightness;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.widget.VpCtrlMenu;
import com.miui.videoplayer.widget.VpCtrlMenuItem;
import com.tvplayer.play.ITVPlayer.OnBufferingUpdateListener;
import com.tvplayer.play.ITVPlayer.OnInfoListener;
import com.tvplayer.play.ITVPlayer.OnPreparedListener;
import com.tvplayer.play.TVPlayer;

public class TvPlayerActivity extends BaseActivity {
	
	private static final String TAG = "TvPlayerActivity";
	
	public static final String KEY_TV_INFO = "key_tv_info";
	
	//UI
	private View mDecorView;
	
	private SurfaceView mSurfaceView;
	private TvMediaPlayer mMediaPlayer;
	private TVPlayer mTvPlayer;
	
	private View mLoadView;
	private TextView mLoadPercentView;
	private TextView mLoadTopTitle;
	private TextView mLoadTopSubTitle;
	private ImageView mLoadTopBackImg;
	
	private View mBgView;
	private View mBufferView;
	
	private View mCtrlView;
	private TextView mCtrlTopTitle;
	private TextView mCtrlTopSubTitle;
	private ImageView mCtrlTopBack;
	
	private TvCtrlVolumeBrightness mCtrlVolumeBrightness;
	private VpCtrlMenu mCtrlMenu;
	private TvCtrlSelectTvPopup mCtrlSelectTv;
	private TvCtrlForecastPopup mCtrlForecast;
	
	private VpCtrlMenuItem mMenuItemSelectTv;
	private VpCtrlMenuItem mMenuItemForecast;
	private VpCtrlMenuItem mMenuItemScaleScreen;
	
	private int mScreenWidth;
	private int mScreenHeight;
	private boolean mDownLeftRegion;
	private boolean mDownRightRegion;
	
	//data
	private TelevisionInfo mCurTvInfo;	
	
	//data supply
	private TvEpgManager mTvEpgManager;
	
	//manager
	private AudioManager mAudioManager;
	
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
	private boolean mIsFullScreen = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.tv_player);
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (mTvPlayer != null){
			mTvPlayer.resume();
	    }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		if (mTvPlayer != null){
			mTvPlayer.pause();
	    }
		mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");       
		if(mTvPlayer != null) {
			mTvPlayer.release();
		}
		if(mTvEpgManager != null) {
			mTvEpgManager.removeListeners(mTvUpdateInterface);
		}
		mStatisticInfo.endTime = System.currentTimeMillis() / 1000.0;
		uploadTvStaticInfo(mTvStatisticInfoList.formatToJson());
	}
	
	//init
	private void init() {
		initReceivedData();
		refreshFullscreenDimen();
		
		initUI();
		initSurfaceView();
		
		initManager();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		mTvEntry = intent.getStringExtra(KEY_TV_ENTRY);
		Log.d(TAG, "data tv entry is " + mTvEntry);
		Object obj = intent.getSerializableExtra(KEY_TV_INFO);
		if(obj instanceof TelevisionInfo) {
			mCurTvInfo = (TelevisionInfo) obj;
		}
	}
	
    private void initManager() {
    	mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvEpgManager.addListener(mTvUpdateInterface);
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    }
	
	private void initUI() {
		mDecorView = getWindow().getDecorView();
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
		mLoadTopBackImg = (ImageView) mLoadView.findViewById(R.id.vp_top_back_img);
		mLoadPercentView = (TextView) mLoadView.findViewById(R.id.vp_loading_percent);
		mLoadTopBackImg.setOnClickListener(mOnClickListener);
	}
	
	private void initCtrlView() {
		mCtrlView = findViewById(R.id.tv_ctrl_view);
		initCtrlTop();
		initCtrlMenu();
		initCtrlSelectTvForecast();
		initCtrlVolumeBrightness();
	}
	
	private void initCtrlTop() {
		if(mCurTvInfo != null) {
			mCtrlTopTitle = (TextView) mCtrlView.findViewById(R.id.vp_top_title);
			mCtrlTopSubTitle = (TextView) mCtrlView.findViewById(R.id.vp_top_sub_title);
			refreshCtrlTopView();
		}
		mCtrlTopBack = (ImageView) mCtrlView.findViewById(R.id.vp_top_back_img);
		mCtrlTopBack.setOnClickListener(mOnClickListener);
	}
	
	private void initCtrlMenu() {
		mCtrlMenu = (VpCtrlMenu) findViewById(R.id.tv_ctrl_menu);
		mMenuItemSelectTv = new VpCtrlMenuItem(this);
		mMenuItemSelectTv.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemSelectTv.setIcon(R.drawable.icon_select_tv);
		mMenuItemSelectTv.setText(R.string.select_tv);
		mMenuItemSelectTv.setOnClickListener(mOnClickListener);
		mCtrlMenu.addLeftMenu(mMenuItemSelectTv);
		mMenuItemForecast = new VpCtrlMenuItem(this);
		mMenuItemForecast.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemForecast.setIcon(R.drawable.icon_forecast);
		mMenuItemForecast.setText(R.string.forecast);
		mMenuItemForecast.setOnClickListener(mOnClickListener);
		mMenuItemForecast.setDividerVisibility(View.INVISIBLE);
		mCtrlMenu.addLeftMenu(mMenuItemForecast);
		
		mMenuItemScaleScreen = new VpCtrlMenuItem(this);
		mMenuItemScaleScreen.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemScaleScreen.setOnClickListener(mOnClickListener);
		mMenuItemScaleScreen.setDividerVisibility(View.INVISIBLE);
		mCtrlMenu.addRightMenu(mMenuItemScaleScreen);
		setMenuItemScaleToDefault();
	}
	
	private void initCtrlSelectTvForecast() {
		mCtrlSelectTv = new TvCtrlSelectTvPopup(this);
		mCtrlSelectTv.setTvSelectedListener(mTvSelectedListener);
		mCtrlSelectTv.setOnDismissListener(mOnDismissListener);
		
		mCtrlForecast = new TvCtrlForecastPopup(this);
		mCtrlForecast.setOnDismissListener(mOnDismissListener);
	}
	
	private void initCtrlVolumeBrightness() {
		mCtrlVolumeBrightness = new TvCtrlVolumeBrightness(this);
		mCtrlVolumeBrightness.setBrightness(true, true);
	}
	
	private void initSurfaceView() {
		mSurfaceView = (SurfaceView) findViewById(R.id.tvsurface);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setKeepScreenOn(true);
		holder.addCallback(mSurfaceHolderCallback);
	}
	
	private void initTvPlayer(SurfaceHolder holder) {
		mMediaPlayer = new TvMediaPlayer();
		if(mTvPlayer == null) {
			mTvPlayer = new TVPlayer(TvPlayerActivity.this, mMediaPlayer, holder);					
		} else{
			mTvPlayer.setDisplay(holder);
		}
		
		mTvPlayer.setOnPreparedListener(mOnPreparedListener);
		mTvPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
		mTvPlayer.setOnInfoListener(mOnInfoListener);
		playCurTvInfo();
	}
	
	private void playCurTvInfo() {
		if(mCurTvInfo == null) {
			return;
		}
		DKLog.d(TAG, "playId = " + mCurTvInfo.epgid);
		updateStatisticInfo();
		mTvPlayer.pause();
		showLoadView();
		mTvPlayer.play(mCurTvInfo.epgid + "");
		mTvEpgManager.addTelevisionInfo(mCurTvInfo);
	}
	
	private void updateStatisticInfo(){
		mStatisticInfo.endTime = System.currentTimeMillis() / 1000.0; 		// save last play.
		
		mStatisticInfo = new TvStaticInfo();
		mStatisticInfo.tvId = mCurTvInfo.mediaid;
		mStatisticInfo.entry = mTvEntry;
		mStatisticInfo.source = mCurTvInfo.source;
		mStatisticInfo.startTime = System.currentTimeMillis() / 1000.0;
		mTvStatisticInfoList.addTvStaticInfo(mStatisticInfo);
	}
	
	private void refreshFullscreenDimen(){
		DisplayInformationFetcher displayInformationFetcher = DisplayInformationFetcher.getInstance(this);
		mScreenHeight = displayInformationFetcher.getScreenHeight();
		mScreenWidth = displayInformationFetcher.getScreenWidth();
	}
	
	private void refreshCtrlTopView() {
		if(mCurTvInfo == null) {
			return;
		}
		mCtrlTopTitle.setText(mCurTvInfo.medianame);
		if(mCurTvInfo.getCurrentShow() != null) {
			mCtrlTopSubTitle.setText(mCurTvInfo.getCurrentShow().videoname);
		}
	}
	
	private void refreshLoadTopView() {
		if(mCurTvInfo != null) {
			mLoadTopTitle.setText(mCurTvInfo.medianame);
			if(mCurTvInfo.getCurrentShow() != null) {
				mLoadTopSubTitle.setText(mCurTvInfo.getCurrentShow().videoname);
			}
		}
	}
	
	private void refreshLoadPercent(int percent) {
		String str = TvPlayerActivity.this.getResources().getString(R.string.vp_loading);
//		str = String.format(str, percent);
		mLoadPercentView.setText(str);
	}
	
	//show view
	private void showLoadView() {
		if( mLoadView.getVisibility() != View.VISIBLE){
    		mStatisticInfo.loadingStartTime = System.currentTimeMillis()/1000.0;
        }
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
		mCtrlVolumeBrightness.dismiss();
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
    	
		int tvId = mCurTvInfo.mediaid;
		TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);;
		if(expiredTvInfo != null) {
			mCurTvInfo = expiredTvInfo;
		}
    }
	
	//do touch
	private void doTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			int x = (int) event.getX();
			int y = (int) event.getY();
			if (x <= mScreenWidth / 6 && (y > mScreenHeight / 6 && y < 5 * mScreenHeight / 6)) {
				mDownLeftRegion = true;
			} else if (x >= 5 * mScreenWidth / 6 && (y > mScreenHeight / 6 && y < 5 * mScreenHeight / 6)) {
				mDownRightRegion = true;  
			} 
			else{
				mDownLeftRegion = false;
				mDownRightRegion = false; 
			}
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			if(!mDownLeftRegion && !mDownRightRegion) {
				if(isPopViewsShowing()) {
					dismissPopupViews();
				} else {
					ctrlViewDoTouch(event);
				}
			}
		}
		
		if(!isLoadViewShowing() && !isCtrlViewShowing()) {
			mCtrlVolumeBrightness.doTouch(event);
		}
	}
	
	private void ctrlViewDoTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if(isCtrlViewShowing()) {
				showNullView();
			} else {
				showCtrlView(true);
			}
		}
	}
	
	private void setScreenToDefault(){
		int videoWidth = mTvPlayer.getVideoWidth();
		int videoHeight = mTvPlayer.getVideoHeight();
		if(videoWidth <= 0 || videoHeight <= 0) {
			return;
		}
		
		float factory1 = Math.max(mScreenWidth, mScreenHeight) / Math.max(videoWidth, videoHeight);
		float factory2 = Math.min(mScreenWidth, mScreenHeight) / Math.min(videoWidth, videoHeight);
		ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
		lp.width = (int) (videoWidth * Math.min(factory1, factory2));
		lp.height = (int) (videoHeight * Math.min(factory1, factory2));
		mSurfaceView.setLayoutParams(lp);
	}
	
	private void setScreenToFullScreen(){
		ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
		lp.width = mScreenWidth;
		lp.height = mScreenHeight;
		mSurfaceView.setLayoutParams(lp);
	}
	
	private void setMenuItemScaleToDefault() {
		mMenuItemScaleScreen.setIcon(R.drawable.icon_scale_default);
		mMenuItemScaleScreen.setText(R.string.defaults);
	}
	
	private void setMenuItemScaleToFullScreen() {
		mMenuItemScaleScreen.setIcon(R.drawable.icon_scale_fullscreen);
		mMenuItemScaleScreen.setText(R.string.fullscreen);
	}
	
	//media player callback
	private Callback mSurfaceHolderCallback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			initTvPlayer(holder);
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			
		}
	};
	
    private OnPreparedListener mOnPreparedListener = new OnPreparedListener(){
    	
		@Override
		public void onPrepared() {
			showNullView();
			showCtrlView(false);
			mStatisticInfo.playTime = System.currentTimeMillis() / 1000.0;
			DKLog.d(TAG, "on prepared");
		}
    	
    };
    
    private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {
		
		@Override
		public void onBufferingUpdate(int percent) {
			if(isLoadViewShowing()) {
				refreshLoadPercent(percent);
			}
		}
	};
	
	private OnInfoListener mOnInfoListener = new OnInfoListener() {
		
		@Override
		public boolean onInfo(int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				mStatisticInfo.bufferCount++;
				showBufferView();
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				hideBufferView();
				break;
			default:
				break;
			}
			return false;
		}
	};
	
	//UI callback
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		doTouch(event);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mCtrlVolumeBrightness.doKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	
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
	
	private TvSelectedListener mTvSelectedListener = new TvSelectedListener() {
		
		@Override
		public void onTvSelected(TelevisionInfo tvInfo) {
			if(tvInfo == null) {
				return;
			}
			if(mCurTvInfo == null || mCurTvInfo.mediaid != tvInfo.mediaid) {
				mCurTvInfo = tvInfo;
				mTvEntry = SourceTagValueDef.PAD_TV_PLAYER_VALUE;
				playCurTvInfo();
				refreshCtrlTopView();
			}
		}
	};
	
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
					setScreenToDefault();
					setMenuItemScaleToFullScreen();
				} else {
					setScreenToFullScreen();
					setMenuItemScaleToDefault();
				}
				mIsFullScreen = !mIsFullScreen;
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshFullscreenDimen();
	}
}
