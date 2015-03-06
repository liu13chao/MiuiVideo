/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Controller.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-28
 */

package com.miui.videoplayer.fragment;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.statistic.UploadStatisticInfoManager;
import com.miui.videoplayer.PhoneStateBroadcastReceiver;
import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.controller.AirkanController;
import com.miui.videoplayer.controller.ControllerView;
import com.miui.videoplayer.controller.FullScreenVideoController;
import com.miui.videoplayer.controller.LifeCycleManager;
import com.miui.videoplayer.controller.LoadingCycle;
import com.miui.videoplayer.controller.OrientationUpdater;
import com.miui.videoplayer.controller.PortraitVideoController;
import com.miui.videoplayer.controller.Settings;
import com.miui.videoplayer.controller.VideoCycleManager;
import com.miui.videoplayer.dialog.OnErrorAlertDialog;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.popup.PopupWindowManager;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.IMediaPlayer;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.model.PlayerSettings;
import com.miui.videoplayer.model.UriLoader;
import com.miui.videoplayer.model.UriLoader.OnUriLoadedListener;
import com.miui.videoplayer.statistic.StartPlayStatistics;
import com.miui.videoplayer.statistic.Statistics;
import com.miui.videoplayer.videoview.IVideoView;
import com.miui.videoplayer.videoview.IVideoView.OnVideoLoadingListener;
import com.miui.videoplayer.videoview.VideoViewFactoryProvider;
import com.miui.videoplayer.widget.AdView;

/**
 * @author tianli
 *
 */
public class VideoFragment extends BaseFragment {

    final public static String TAG = "VideoFragment";

    /* Context */
    private Activity mActivity = null;
    private IVideoView mVideoView;
    private ControllerView mControllerView;

    private Toast mBackToast;
    Intent mUnhandledIntent = null;
    private boolean mPostedPlay = false;
    private Statistics mStatistics;
    private StartPlayStatistics mStartPlayStatistics;
    /* Play */
    private FragmentCreator mFragmentCreator;
    private CoreFragment mCoreFragment;
    public PlayHistoryManager mPlayMgr;
    private boolean mIsWindowStyle = false;
    private boolean mIsFullScreen = true;
    private VideoProxy mVideoProxy;

    /* Airkan */
    private AirkanController mAirkanController;
    private AirkanManager mAirkanManager;

    /* Uri */
    UriLoader mUriLoader;
    BaseUri mBaseUri;

    /* Controller */
//    private static ControllerCenter mControllerCenter = null;
    private LifeCycleManager mLifeCycleMgr = new LifeCycleManager();
    private VideoCycleManager mVideoCycleMgr =  new VideoCycleManager();
    
    private OrientationUpdater mOrientationUpdater;

    /* View */
    private AdView mAdView;
    private PortraitVideoController mPortraitController;
    private FullScreenVideoController mFullScreenController;
	private PhoneStateBroadcastReceiver mPhoneStateBroadcastReceiver;
	private UUID mUUID;
    public VideoFragment(){
        mVideoProxy = new VideoProxy(this);
        mUUID = UUID.randomUUID();
    }

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        initUI(container.getContext());
        return mControllerView;
    }
    
    private boolean isFullScreen(Context context){
        if(context == null){
            return false;
        }
        int orientation = context.getResources().getConfiguration().orientation;
        if(mIsWindowStyle && orientation == Configuration.ORIENTATION_PORTRAIT){
            return false;
        }else{
            return true;
        }
    }

    private void initUI(Context context){
        if(mControllerView == null){
            mControllerView = (ControllerView)View.inflate(context, R.layout.vp_controller_view_new,
                    null);
            initFullScreenController();
            mVideoCycleMgr.add(new LoadingCycle(mControllerView));
        }
    }

    public boolean isWindowStyle(){
        return mIsWindowStyle;
    }

    private void enterWindowMode(){
        mIsFullScreen = false;
        initPortraitController();
        if(mFullScreenController != null){
            mFullScreenController.hideController();
            mVideoCycleMgr.remove(mFullScreenController);
        }
        if(mPortraitController != null){
            mPortraitController.setVisibility(View.VISIBLE);
            mPortraitController.attachMediaPlayer(mVideoView);
            mVideoCycleMgr.add(mPortraitController);
        }
        if(mControllerView != null){
            mControllerView.setGestureListener(mPortraitController);
        }
    }

    private void enterFullScreenMode(boolean showController){
        mIsFullScreen = true;
        initFullScreenController();
        if(mPortraitController != null){
            mPortraitController.setVisibility(View.GONE);
            mVideoCycleMgr.remove(mPortraitController);
        }
        if(mControllerView != null){
            mControllerView.setGestureListener(mFullScreenController);
        }
        if(mFullScreenController != null){
            mFullScreenController.attachMediaPlayer(mCoreFragment, mVideoView);
            if(mVideoView != null){
                mVideoView.requestVideoLayout();
            }
            mVideoCycleMgr.add(mFullScreenController);
            if(showController){
                mFullScreenController.showController();
            }
        }
    }

    private void initPortraitController(){
        if(mPortraitController == null && mControllerView != null){
            mPortraitController = (PortraitVideoController)LayoutInflater.from(mControllerView.
                    getContext()).inflate(R.layout.vp_video_portrait_layout, mControllerView, false);
            mControllerView.addView(mPortraitController);
            mPortraitController.attachActivity(getActivity(), mControllerView, mOrientationUpdater);
            mControllerView.setGestureListener(mPortraitController);
        }
    }

    private void initFullScreenController(){
        if(mFullScreenController == null){
            mFullScreenController = (FullScreenVideoController)LayoutInflater.from(mControllerView.
                    getContext()).inflate(R.layout.vp_video_fullscreen_layout, mControllerView, false);
            mControllerView.addView(mFullScreenController);
            Log.d(TAG, "mOrientationUpdater " + mOrientationUpdater);
            mFullScreenController.attachActivity(getActivity(), mControllerView, mOrientationUpdater);
            mFullScreenController.attachVideoProxy(mVideoProxy);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        mActivity = activity;
    }

    private void play(BaseUri uri){
        Log.d(TAG, "uri:" + uri.toString());
        mStatistics = new Statistics(mActivity, uri);
        mStartPlayStatistics = new StartPlayStatistics(mActivity, uri, mUUID);
        //	mCoreFragment.setOnPauseOrStartListener(mStatistics);
        mBaseUri = uri;
        initVideoView(uri);
        mVideoView.setPlayInfo(mBaseUri);
        mCoreFragment.play(mVideoView, uri);
        if(mAirkanController != null){
            mAirkanController.attachMilinkFragment(mCoreFragment.newMilinkFragment());
        }
        if(mIsFullScreen){
            if(mFullScreenController != null){
                mFullScreenController.attachMediaPlayer(mCoreFragment, mVideoView);
            }
            if(mCoreFragment instanceof OnlineVideoFragment){
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }else{
            if(mPortraitController != null){
                mPortraitController.attachMediaPlayer(mVideoView);
            }
        }
        if(DKApp.getSingleton(PlayerSettings.class).isForceFullScreen()){
            mVideoView.setForceFullScreen(true);
        }
        // TODO: workaround to avoid page transaction issue
        mVideoView.asView().setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mVideoView != null){
                    mVideoView.asView().setVisibility(View.VISIBLE);
                }
            }
        }, 500);
    }

	public void retryplay(int pos){
		play(mCoreFragment.getUri());
		mVideoView.seekTo(pos);
	}
	
	public IVideoView  getVideoView(){
		return mVideoView;
	}

    private void initVideoView(BaseUri uri){
        clearVideoView();
        mVideoView = VideoViewFactoryProvider.getFactory(uri).create(mActivity);
        mVideoView.setOnPreparedListener(mOnPreapredListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnVideoLoadingListener(mOnVideoLoadingListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferUpdateListener);
        LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mVideoView.asView().setLayoutParams(params);
        mControllerView.addView(mVideoView.asView(), 0);
        initAdView();
        mAirkanManager.setLocalMediaControl(mVideoView);
    }

    private void initAdView(){
        if(mAdView != null){
            mControllerView.removeView(mAdView);
            mAdView = null;
        }
        mAdView = new AdView(mActivity);
        mAdView.setNotifyAdsPlayListener(mStatistics);
        mAdView.setVisibility(View.GONE);
        mVideoView.setAdsPlayListener(mOnAdsPlayListener);
        mVideoView.attachAdView(mAdView);
        mControllerView.addView(mAdView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void initAirkan(){
        Log.d(TAG, "initAirkan");
        if(mAirkanManager == null){
            Log.d(TAG, "new airkan manager.");
            mAirkanManager = new AirkanManager(mActivity);
            try{
                mAirkanManager.openDeviceManager();
            }catch (Exception e) {
            }
        }
    }

    private void clearVideoView(){
        if(mVideoView != null && mControllerView != null){
            mVideoView.setOnPreparedListener(null);
            mVideoView.setOnInfoListener(null);
            mVideoView.setOnErrorListener(null);
            mVideoView.setOnSeekCompleteListener(null);
            mVideoView.setOnVideoSizeChangedListener(null);
            mVideoView.setOnVideoLoadingListener(null);
            mControllerView.removeView(mVideoView.asView());
            mVideoView.close();
        }
    }

    public void reset(){
        if(mAirkanController != null){
            mAirkanController.reset();
        }
        if(mAirkanManager != null){
            mAirkanManager.closeDeviceManager();
            mAirkanManager = null;
        }
    }

    private void showErrorDialog(int what) {
        try {
            if(!isAdded()){
                return;
            }
            OnErrorAlertDialog.build(mActivity, mBaseUri != null ? mBaseUri.getUri()
                    : null, what).show();	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWindowStyle(boolean mIsWindowStyle) {
        this.mIsWindowStyle = mIsWindowStyle;
    }

    public void playByFragment(FragmentCreator fragmentCreator){
        initPlay(fragmentCreator, null);
    }
    
    public void playByIntent(Intent intent){
        initPlay(null, intent);
    }
    
    private void initPlay(FragmentCreator fragmentCreator, Intent intent){
        mFragmentCreator = fragmentCreator;
        if(mActivity == null){
            Log.d(TAG, "post playByIntent.");
            mUnhandledIntent = intent;
            return;
        }
        reset();
        initAirkan();
        Log.d(TAG, "handleIntent.");
        mPlayMgr = new PlayHistoryManager(mActivity.getApplicationContext());
        mPlayMgr.load();
        initUI(mActivity);
        if(fragmentCreator != null){
            mCoreFragment = fragmentCreator.create(mActivity, mControllerView, intent);
        }else{
            mCoreFragment = FragmentCreator.createFragment(mActivity, mControllerView, 
                    intent);
        }
        mCoreFragment.attachVideoProxy(mVideoProxy);
        mCoreFragment.attachAirkanManager(mAirkanManager);
        mAirkanController = new AirkanController(mAirkanManager, mFullScreenController);
        mAirkanController.setActivity(mActivity, mControllerView);
        play(mCoreFragment.getUri());
        if(isFullScreen(mActivity)){
            enterFullScreenMode(false);
        }else{
            enterWindowMode();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        initLifeCycles(getActivity());
		mPhoneStateBroadcastReceiver = new PhoneStateBroadcastReceiver(this);
        if(savedInstanceState != null){
            String statistics = savedInstanceState.getString(Constants.STATISTIC_All);
            if(!TextUtils.isEmpty(statistics)){
                Log.d(TAG, "onCreate send  data in savedInstanceState");
                Statistics.sendStatistic(mActivity, statistics);
            }
        }
        initAirkan();
        if(mPostedPlay){
            initPlay(mFragmentCreator, mUnhandledIntent);
            mPostedPlay = false;
        }
        mLifeCycleMgr.onCreate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        if(mStatistics != null){
            String statistics = mStatistics.generateStatistics();
            outState.putString(Constants.STATISTIC_All, statistics);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifeCycleMgr.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PHONE_STATE");
		getActivity().registerReceiver(mPhoneStateBroadcastReceiver, intentFilter);
		mPhoneStateBroadcastReceiver.onEnterForeground();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLifeCycleMgr.onStop();
		if (mPhoneStateBroadcastReceiver != null) {
			getActivity().unregisterReceiver(mPhoneStateBroadcastReceiver);
		}
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(mVideoView != null){
            mVideoView.onActivityResume();
            if(mAirkanManager.isPlayingInLocal()){
                mVideoView.start();
            }
        }
        if(mStatistics != null){
            mStatistics.onResume();
        }
        mLifeCycleMgr.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if(mCoreFragment != null){
            mCoreFragment.onSavePlayHistory(mPlayMgr);
        }
        if(mVideoView != null){
            mVideoView.pause();
            mVideoView.onActivityPause();
        }
        if(mStatistics != null){
            mStatistics.onPause();
        }
        mLifeCycleMgr.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        clearVideoView();
        PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
        if(mAirkanManager != null){
            if(!mAirkanManager.isPlayingInLocal()){
                mAirkanManager.stopRemotePlay();
            }
            mAirkanManager.closeDeviceManager();
        }
        if(mCoreFragment != null && mCoreFragment.getUriLoader() != null){
            mCoreFragment.getUriLoader().cancel();
        }
        if(mStatistics != null){
            mStatistics.sendStatistics();
        }
        mLifeCycleMgr.onDestroy();
    }

    private void initLifeCycles(Activity activity){
        mOrientationUpdater = new OrientationUpdater(activity);
        mLifeCycleMgr.add(mOrientationUpdater);
        mLifeCycleMgr.add(new Settings(activity));
    }

    public void finish(){
        if(mActivity != null){
            mActivity.finish();
        }
    }

    private void bufferingStart() {
        mStatistics.bufferStart();
        mVideoCycleMgr.onBufferingStart(mVideoView);
    }

    private void bufferingEnd() {
        mStatistics.bufferEnd();
        mVideoCycleMgr.onBufferingEnd(mVideoView);
    }

    private void bufferingPercent(int percent) {
        Log.i(TAG, "Buffered : " + percent + " %");
        if (percent == 100) {
            return;
        }
        mVideoCycleMgr.onBufferingPercent(mVideoView, percent);
    }

    private void continuePlay(){
        if(!mVideoView.isAdsPlaying()){
            PlayHistoryEntry entry = mCoreFragment.onLoadPlayHistory(mPlayMgr);
            mVideoView.continuePlay(entry);
        }
    }

    /*  VideoProxy start. */
    public void playCi(int ci){
        if(mCoreFragment != null){
            if(mVideoView != null){
                mVideoView.pause();
            }
            mVideoCycleMgr.onEpLoadingStart();
            mCoreFragment.getUriLoader().loadEpisode(ci, mUriLoadListener);
        }
    }

    public void playNext(){
        if(mVideoView != null){
            mVideoView.pause();
        }
        mVideoCycleMgr.onEpLoadingStart();
        if(mCoreFragment != null && mCoreFragment.getUriLoader() != null){
            mCoreFragment.getUriLoader().next(mUriLoadListener);
        }
    }

    public void playSource(int source, int resolution) {
        if(mCoreFragment != null){
            mCoreFragment.onSavePlayHistory(mPlayMgr);
            if (mBaseUri != null && mCoreFragment.getUriLoader() instanceof OnlineLoader) {
                if(mVideoView != null){
                    mVideoView.pause();
                }
                mVideoCycleMgr.onEpLoadingStart();
                OnlineLoader loader = (OnlineLoader) mCoreFragment.getUriLoader();
                loader.loadEpisode(mBaseUri.getCi(), source, resolution, mUriLoadListener);
            }
        }
    }
    
    public void hideController(){
        if(mFullScreenController != null){
            mFullScreenController.hideController();
        }
    }
    /*  Video Proxy end. */

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
			if(mPhoneStateBroadcastReceiver.handleOnCompletion()){
				return;
			}
            if(!mVideoView.isAdsPlaying()){
                if(mCoreFragment.getUriLoader() != null && 
                        mCoreFragment.getUriLoader().hasNext()){
                    playNext();
                }else{
                    mActivity.finish();
                }
            }
            mVideoCycleMgr.onCompletion(mVideoView);
        }
    };

    private OnBufferingUpdateListener mOnBufferUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mCoreFragment.onBufferingUpdate(percent);
        }
    };

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height) {
        }
    };

    private OnPreparedListener mOnPreapredListener = new OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Log.d(TAG, "onPrepared");
            continuePlay();
            if(mVideoView.isAdsPlaying()){
                mStatistics.onPrepared(true);
                mStartPlayStatistics.onPrepared(true);
            }else{
                mStatistics.onPrepared(false);
                mStartPlayStatistics.onPrepared(false);
                mStatistics.onDuration(mVideoView.getDuration());
            }
            mVideoCycleMgr.onPrepared(mVideoView);
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                bufferingStart();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                bufferingEnd();
                break;
            case com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_UPDATE:
                bufferingPercent(extra);
                break;
            default:
                break;
            }
            return false;
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
			if(!mPhoneStateBroadcastReceiver.handleOnCompletion()){
				showErrorDialog(what);
			}
            return false;
        }
    };

    private OnVideoLoadingListener mOnVideoLoadingListener = new OnVideoLoadingListener() {
        @Override
        public void onVideoLoading(IVideoView videoView) {
            mVideoCycleMgr.onVideoLoadingStart(videoView);
        }

		@Override
		public void onVideoHideLoading(IVideoView videoView) {
		}
    };
    
    private AdsPlayListener mOnAdsPlayListener = new AdsPlayListener() {
        @Override
        public void onAdsTimeUpdate(int leftSeconds) {
            if(mAdView != null){
                mAdView.onAdsTimeUpdate(leftSeconds);
            }
        }
        
        @Override
        public void onAdsPlayStart() {
            if(mAdView != null){
                mAdView.onAdsPlayStart();
            }
        }
        
        @Override
        public void onAdsPlayEnd() {
            if(mAdView != null){
                mAdView.onAdsPlayEnd();
            }
            if(mVideoView != null && !mVideoView.hasLoadingAfterAd()){
                continuePlay();
            }
        }
        
        @Override
        public void onAdsDuration(int duration) {
            if(mAdView != null){
                mAdView.onAdsDuration(duration);
            }
        }
    };
    
    //    private OnControlEventListener mGestureListener = new OnControlEventListener(){
    //        @Override
    //        public void onTouchMove(int region, float movementX, float movementY) {
    //            //			if(mIsScreenLocked){
    //            //				return;
    //            //			}
    //            if (mAirkanManager.isPlayingInLocal()) {
    //                //				if(region == REGION_LEFT && !mCoreFragment.isShowing()){
    //                //					getBrightnessWindow().adjustBrightness(mActivity, movementY).show(mControllerView, mActivity);
    //                //				}else if(region == REGION_RIGHT && !mCoreFragment.isShowing()){
    //                //					getVolumeWindow().adjustVolume(mActivity, movementY).show(mControllerView, mActivity);
    //                //				}else 
    //                if(region == REGION_CENTER){
    //                    //					if(!isLoadingShowing() && mVideoView.canSeekBackward() && mVideoView.canSeekForward()){
    //                    //						int stepPosition = (int) (Math.abs(movementX) / mControllerView.
    //                    //								getAdjustPositionStep() * 1000) ;
    //                    //						if(movementX < 0){
    //                    //							stepPosition = -stepPosition;
    //                    //						}
    //                    //						mCoreFragment.show(MediaStyle.SEEK);
    //                    //						if(mCoreFragment.getMediaController() != null){
    //                    //							mCoreFragment.getMediaController().seekStepStart(stepPosition);
    //                    //						}
    //                    //					}
    //                }
    //            }
    //        }
    //
    //        @Override
    //        public void onTap(int region) {
    //            //			if(mIsScreenLocked){
    //            //				return;
    //            //			}
    //            if (mAirkanManager.isPlayingInLocal()) {
    //                //				if(region == REGION_LEFT && !mCoreFragment.isShowing()){
    //                //					getBrightnessWindow().show(mControllerView, mActivity);
    //                //				}else if(region == REGION_RIGHT && !mCoreFragment.isShowing()){
    //                //					getVolumeWindow().show(mControllerView, mActivity);
    //                //				}else{
    //                //					if(!isLoadingShowing() && mVideoView.canPause()){
    //                //						toggleCoreFragment();
    //                //					}
    //                //				}
    //            }
    //        }
    //
    //        @Override
    //        public void onTouchUp(int region) {
    //            if (mAirkanManager.isPlayingInLocal()) {
    //                if(region == REGION_CENTER){
    //                    //		            if(mCoreFragment.getMediaController() != null){
    //                    //		                mCoreFragment.getMediaController().seekStepEnd();
    //                    //		            }
    //                }
    //            }
    //        }
    //    };

    public static class BackKeyEvent extends KeyEvent {
        public BackKeyEvent(int action, int code) {
            super(action, code);
        }
    }

    private long mLastBackKeyTime = 0;
    private long mBackInterval = 2500;

    public boolean onKeyDown(KeyEvent event) {
        if(getActivity() == null){
            return false;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "BACK DOWN");
            if(mIsWindowStyle){
                return handleWindowModeBack();
            }else{
                return handleFullScreenBack();
            }
        }
        return false;
    }
    
    private boolean handleWindowModeBack(){
        if(getActivity() != null  && isFullScreen(getActivity())){
            if(mOrientationUpdater != null){
                mOrientationUpdater.requestPortrait();
                return true;
            }
        }
        return false;
    }
    
    private boolean handleFullScreenBack(){
        long ts = System.currentTimeMillis();
        if(ts - mLastBackKeyTime <= mBackInterval){
            return false;
        }
        mLastBackKeyTime = System.currentTimeMillis();
        if (mBackToast == null) {
            mBackToast = Toast.makeText(mActivity, R.string.toast_back_key_pressed_notice, Toast.LENGTH_SHORT);
        }
        if (mBackToast.getView() == null) {
            return false;
        }
        if (mBackToast.getView().isShown()) {
            mBackToast.cancel();
        }else{
            mBackToast.show();
            return true;
        }
        return false;
    }

    private OnUriLoadedListener mUriLoadListener = new OnUriLoadedListener(){
        @Override
        public void onUriLoaded(int episode, BaseUri uri) {
            Log.d(TAG, "onUriLoaded " + uri.toString());
            if(mStatistics != null){
                mStatistics.pushLastRecord();
                mStatistics.sendStatistics();
            }
            
            if(uri != null && uri instanceof OnlineUri){
            	OnlineUri onlineUri = (OnlineUri)uri;
            	UploadStatisticInfoManager.uploadChangeEpisodeStatistic(onlineUri.getMediaId(), onlineUri.getCi(), onlineUri.getSource(), 
            			onlineUri.getResolution(), onlineUri.getVideoType(), onlineUri.getPlayType(), "");
            }
            play(uri);
        }

        @Override
        public void onUriLoadError(int errorCode) {
            showErrorDialog(errorCode);
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mOrientationUpdater != null){
            mOrientationUpdater.onConfigurationChanged(newConfig);
        }
        if(mIsWindowStyle){
            if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ){
                enterWindowMode();
            }else{
                if(mOrientationUpdater != null){
                    mOrientationUpdater.requestLandscape();
                }
                enterFullScreenMode(true);
            }
        }
        if(mVideoView != null){
            mVideoView.requestVideoLayout();
        }
    }
}
