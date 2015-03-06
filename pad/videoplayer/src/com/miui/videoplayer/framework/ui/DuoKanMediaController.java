package com.miui.videoplayer.framework.ui;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.duokan.TimedText;
import com.miui.video.R;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.DuoKanCodecConstants;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanChangedEvent;
import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanExistDeviceInfo;
import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanOnChangedListener;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.popup.BrightnessPopupWindow;
import com.miui.videoplayer.framework.popup.ControlListPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlMediaInfoPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlFunctionPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlScaleScreenPopupWindow;
import com.miui.videoplayer.framework.popup.PauseFullScreenPopupWindow;
import com.miui.videoplayer.framework.popup.PopupWindowManager;
import com.miui.videoplayer.framework.popup.ProgressBarOnlyPopupWindow;
import com.miui.videoplayer.framework.popup.ProgressTimePopupWindow;
import com.miui.videoplayer.framework.popup.VolumePopupWindow;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.framework.utils.DKTimeFormatter;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.utils.DuoKanUtils;
import com.miui.videoplayer.framework.views.OriginMediaController;
import com.miui.videoplayer.framework.views.OriginMediaControllerV5;

import com.duokan.MediaPlayer.MediaInfo;

public class DuoKanMediaController extends FrameLayout implements AirkanOnChangedListener, OnPreparedListener, OnCompletionListener, OnSeekCompleteListener, OnInfoListener, OnBufferingUpdateListener, OnVideoSizeChangedListener, OnErrorListener
, com.duokan.MediaPlayer.OnPreparedListener, com.duokan.MediaPlayer.OnCompletionListener, com.duokan.MediaPlayer.OnSeekCompleteListener, com.duokan.MediaPlayer.OnInfoListener, com.duokan.MediaPlayer.OnVideoSizeChangedListener, com.duokan.MediaPlayer.OnErrorListener, com.duokan.MediaPlayer.OnBufferingUpdateListener, com.duokan.MediaPlayer.OnTimedTextListener{
	private static final String TAG = DuoKanMediaController.class.getSimpleName();

	private static final int FADE_OUT_VOLUME_MESSAGE_WHAT = 0;
	private static final int FADE_OUT_BRIGHTNESS_MESSAGE_WHAT = 1;
	private static final int CONTINUE_PLAY_SEEK_TO_WHAT = 2;
	private static final int CONTINUE_PLAY_START_WHAT = 3;
	private static final int FADE_OUT_ERROR_DIALOG_WHAT = 4;
	private static final int QUERY_PROGRESS_AFTER_PREPARED = 5;
	public static final int MEDIASETINFO_UPDATED = 6;
	
	
	private static final int BRIGHTNESS_VOLUME_DISMISS_TIME = 3000;
	
	private ProgressTimePopupWindow mProgressTimePopupWindow;
	private VpCtrlFunctionPopupWindow mOptionMenuPopupWindow;
	private BrightnessPopupWindow mBrightnessPopupWindow;
	private VolumePopupWindow mVolumePopupWindow;
	private ProgressBarOnlyPopupWindow mProgressOnlyPopupWindow;
	private VpCtrlScaleScreenPopupWindow mOptionSubMenuPopupWindow;
	
	private Activity mActivity;
	private View mDecorView;

	private LocalMediaPlayerControl mLocalMediaPlayer;
	private LocalVideoPlaySizeAdjustable mVideoSizeAdjustable;
	private OriginMediaController mMediaController;
	private AirkanManager mAirkanManager;
	
	private LayoutInflater mLayoutInflater;
	
	private View mAirkanBackgroundView;
	private View mLoadingBackgroundView;
	
	private AudioManager mAudioManager;
	private AirkanExistDeviceInfo  mAirkanExistDeviceInfo;

	private Toast mBackToast;
	private Toast mContinuePlayToast;
	
	private int mSwitchMediaOrientation = 0;
	
	private String[] mUris;

	private DisplayMetrics mDisplayMetrics; 
	private float mTriggerAdjustPositionTolerance;
	private float mAdjustPositionStep;
	
	private DisplayInformationFetcher mDisplayInformationFetcher;
	private BufferingProgressDialog mBufferingProgressDialog;
	private Uri mUri;
	
	private AlertDialog mAlertDialog;
	private Map<String, PlayHistoryEntry> mMetaDataMap;
	
	private int mBufferTimes = 0;
	private int mTotalPausedTime = 0;
	private long mTotalBufferedTime = 0;
	private long mStartPlayingTimeStamp = 0L;
	private long mBufferedStartTimeStamp = 0L;
	
	private boolean mPrepared = false;
	private boolean mPauseFullScreen = false;
	
	public void setLocalMediaPlayerControl(LocalMediaPlayerControl localMediaPlayerControl) {
		this.mLocalMediaPlayer = localMediaPlayerControl;
		this.mMediaController.setLocalMediaPlayerControl(localMediaPlayerControl);
		
		//origin codec listener
		localMediaPlayerControl.setOnPreparedListener((android.media.MediaPlayer.OnPreparedListener)this);
		localMediaPlayerControl.setOnCompletionListener((android.media.MediaPlayer.OnCompletionListener)this);
		localMediaPlayerControl.setOnSeekCompleteListener((android.media.MediaPlayer.OnSeekCompleteListener)this);
		localMediaPlayerControl.setOnInfoListener((android.media.MediaPlayer.OnInfoListener)this);
		localMediaPlayerControl.setOnErrorListener((android.media.MediaPlayer.OnErrorListener)this);
		localMediaPlayerControl.setOnBufferingUpdateListener((android.media.MediaPlayer.OnBufferingUpdateListener)this);
		localMediaPlayerControl.setOnVideoSizeChangedListener((android.media.MediaPlayer.OnVideoSizeChangedListener)this);
		//duokan codec listener
		
		localMediaPlayerControl.setOnPreparedListener((com.duokan.MediaPlayer.OnPreparedListener)this);
		localMediaPlayerControl.setOnCompletionListener((com.duokan.MediaPlayer.OnCompletionListener)this);
		localMediaPlayerControl.setOnSeekCompleteListener((com.duokan.MediaPlayer.OnSeekCompleteListener)this);
		localMediaPlayerControl.setOnInfoListener((com.duokan.MediaPlayer.OnInfoListener)this);
		localMediaPlayerControl.setOnErrorListener((com.duokan.MediaPlayer.OnErrorListener)this);
		localMediaPlayerControl.setOnBufferingUpdateListener((com.duokan.MediaPlayer.OnBufferingUpdateListener)this);
		localMediaPlayerControl.setOnVideoSizeChangedListener((com.duokan.MediaPlayer.OnVideoSizeChangedListener)this);
		localMediaPlayerControl.setOnTimedTextListener(this);
//		localMediaPlayerControl.setOutOfBandTextSource(new String("/mnt/sdcard/test.srt"));
		
		if (mAirkanExistDeviceInfo != null && mAirkanExistDeviceInfo.getExistDeviceName() != null) {
			mAirkanManager = new AirkanManager(localMediaPlayerControl, mMediaController, mActivity, mAirkanExistDeviceInfo);
		} else {
			mAirkanManager = new AirkanManager(localMediaPlayerControl, mMediaController, mActivity);
		}
		mAirkanManager.setAirkanOnChangedListener(this);
		mAirkanManager.setDuokanMediaController(this);
		mMediaController.setAirKanManager(mAirkanManager);
		mMediaController.setMediaPlayer(localMediaPlayerControl);
		mMediaController.setDuoKanMediaController(this);
		
	}
	
	public DuoKanMediaController(Context context) {
		super(context);
		
		setupWindows(context);
		init(context);
		
	}
	

	public DuoKanMediaController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setupWindows(context);
		init(context);
	}

	public DuoKanMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setupWindows(context);
		init(context);
	}
	
	public void exitPlayer() {
		mActivity.finish();
	}

	private void setupWindows(Context context) {
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setBackgroundColor(context.getResources().getColor(R.color.full_translucent));
		
//		this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		mLayoutInflater = LayoutInflater.from(context);
		
		mDisplayInformationFetcher = DisplayInformationFetcher.getInstance(context);
		mDisplayMetrics = mDisplayInformationFetcher.getDisplayMetrics();
		updateAdjustPositionStep();
		
		mTriggerAdjustPositionTolerance = (mDisplayMetrics.densityDpi / 10) * mDisplayMetrics.density;
		this.requestFocus();
	}

	private void updateAdjustPositionStep() {
		int screenWidth = mDisplayInformationFetcher.getScreenWidth();
		mAdjustPositionStep = screenWidth / 120f;
	}

	private void init(Context context) {
		if (DuoKanConstants.ENABLE_V5_UI) {
			mMediaController = new OriginMediaControllerV5(context);
		} else {
			mMediaController = new OriginMediaController(context);
		}
//		mMediaController = new OriginMediaController(context);
		mMediaController.setAnchorView(this);
	}
	
	public void onActivityCreate() {
		if (DuoKanConstants.ENABLE_AIRKAN) {
			mAirkanManager.onActivityCreate();
		}
	}
	
	public void onActivityStart() {
		this.requestFocus();
		if (DuoKanConstants.ENABLE_AIRKAN) {
			mAirkanManager.onActivityStart();
		}
		loadSettings();
	}

	public void onActivityPause() {
		if (mAirkanManager.isPlayingInLocal() && !VideoPlayerActivity.isScreenSaver) {
			Log.i(TAG, "on Activity pause, pos: " + VideoPlayerActivity.videoPausedPosition);
			int duration = mLocalMediaPlayer.getDuration();
			savePlayHistory(VideoPlayerActivity.videoPausedPosition, duration);
		}
	}

	public void onActivityStop() {
		if (DuoKanConstants.ENABLE_AIRKAN) {
			mAirkanManager.onActivityStop();
		}
		saveSettings();
		if (mAirkanManager.isPlayingInLocal()) {
			//Log.i(TAG, "on Activity Stop, pos: " + VideoPlayerActivity.VIDEO_TIME_BRFORE_PAUSE);
			//savePlayHistory(VideoPlayerActivity.VIDEO_TIME_BRFORE_PAUSE, 0);
			if (mBufferingProgressDialog != null && mBufferingProgressDialog.isShowing()) {
				mBufferingProgressDialog.dismiss();
			}
			hideLoadingView();
			if (!VpCtrlMediaInfoPopupWindow.isNull()) {
				VpCtrlMediaInfoPopupWindow.setNull();
			}
			if (DuoKanConstants.ENABLE_V5_UI) {
				mMediaController.removeShowFullScreenPauseMessage();
			}
			PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
		}
		
		if (mMediaController != null) {
			mMediaController.hide();
		}
	}
	
	public void onActivityDestroy() {
		if (DuoKanConstants.ENABLE_AIRKAN) {
			mAirkanManager.onActivityDestroy();
		}
		mMediaController.onActivityDestroy();
	}
	
	public boolean onMenuClick() {
		if (mAirkanManager != null && !mAirkanManager.isPlayingInLocal()) {
			return true;
		}
		
		if (mOptionMenuPopupWindow == null) {
			MediaInfo mediaInfo = mLocalMediaPlayer.getMediaInfo();
			if (mediaInfo != null && (mediaInfo.videoCodecName != null && mediaInfo.videoCodecName.equals("H.264")) && mediaInfo.fpaType == 3){
				VpCtrlFunctionPopupWindow.IS_3D_VIDEO_SUPPORTED = true;
				VpCtrlFunctionPopupWindow.IS_3D_ENABLED = get3dMode();
			} else {
				VpCtrlFunctionPopupWindow.IS_3D_VIDEO_SUPPORTED = false;
				VpCtrlFunctionPopupWindow.IS_3D_ENABLED = false;
			}

			mOptionMenuPopupWindow = new VpCtrlFunctionPopupWindow(mActivity, this);
			mOptionSubMenuPopupWindow = new VpCtrlScaleScreenPopupWindow(mActivity, mVideoSizeAdjustable);
			mOptionMenuPopupWindow.setOptionSubMenuPopupWindow(mOptionSubMenuPopupWindow);
		}
		
		mOptionMenuPopupWindow.setDuoKanMediaController(this);
		mOptionMenuPopupWindow.setMediaController(mMediaController);
		mOptionMenuPopupWindow.setLocalMediaPlayerControl(mLocalMediaPlayer);
		mOptionMenuPopupWindow.setSizeAdjustable(mVideoSizeAdjustable);
		ControlListPopupWindow controlListPopupWindows = mMediaController.getControlListPopupWindow();
		boolean isControlListPopupShowing = controlListPopupWindows != null && controlListPopupWindows.isShowing();
		if (DuoKanConstants.ENABLE_V5_UI) {
			mMediaController.removeShowFullScreenPauseMessage();
		}
		if (mOptionMenuPopupWindow.isShowing()) {
			return false;
		} else {
			if (mMediaController != null) {
				mMediaController.hideFullScreenPausePopup();
			}
			if (!isControlListPopupShowing) {
				mOptionMenuPopupWindow.show(this);
			}
			return true;
		} 
	}
	
	public void showLoadingView(int curCi) {
		if (mLoadingBackgroundView == null) {
			if (DuoKanConstants.ENABLE_V5_UI) {
				mLoadingBackgroundView = inflateLoadingBackgroundViewV5();
			} else {
				mLoadingBackgroundView = inflateLoadingBackgroundView();
			}
		} 
		refreshLoadTopTitle(curCi);
		refreshLoadPercent(0);
		
		Log.i(TAG, "show loading view: " + mLoadingBackgroundView);
		mLoadingBackgroundView.setVisibility(View.VISIBLE);
		if (mMediaController != null && mMediaController.isShowing()) {
			mMediaController.hide();
		}
		mLoadingBackgroundView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	public void hideLoadingView() {
		if (mLoadingBackgroundView != null) {
			Log.i(TAG, "hide loading view: " + mLoadingBackgroundView);
			mLoadingBackgroundView.setVisibility(View.INVISIBLE);
		}
		this.bringToFront();
	}
	
	public void hideStatusBar() {
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
	
	private void showStatusBar() {
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}
	
	private void refreshLoadTopTitle(int curCi) {
		if(mLoadingBackgroundView == null) {
			return;
		}
		
		ImageButton topBack = (ImageButton) mLoadingBackgroundView.findViewById(R.id.vp_top_back_img);
		topBack.setOnClickListener(mOnClickListener);
		
		TextView topTitle = (TextView) mLoadingBackgroundView.findViewById(R.id.vp_top_title);
		if (mUri != null) {
			PlayHistoryEntry entry  = this.getMetaDataEntry(mUri.toString());
			if (entry != null) {
				if (entry.getVideoName() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(entry.getVideoName());
					if(curCi > 0 && VideoPlayerActivity.mediaCount > 1) {
						String str = mContext.getResources().getString(R.string.episode_suffix);
						str = String.format(str, curCi);
						sb.append(" ");
						sb.append(str);
					}
					topTitle.setText(sb.toString());
				}
			}
		}
		
		TextView topSubTitle = (TextView) mLoadingBackgroundView.findViewById(R.id.vp_top_sub_title);
		if (VideoPlayerActivity.mediaSubTitle != null) {
			topSubTitle.setText(VideoPlayerActivity.mediaSubTitle);
		} else {
			topSubTitle.setText(mContext.getResources().getString(R.string.top_status_local_media));
		}
		if (mUri != null && mUri.getScheme() != null) {
			String scheme = mUri.getScheme();
			if (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp")) {
				topSubTitle.setText(mContext.getResources().getString(R.string.top_status_online_media));
			}
		}
	}
	
	private void refreshLoadPercent(int percent) {
		if(mLoadingBackgroundView == null) {
			return;
		}
		
		TextView loadPercent = (TextView) mLoadingBackgroundView.findViewById(R.id.vp_loading_percent);
		String str = mContext.getResources().getString(R.string.vp_loading);
//		str = String.format(str, percent);
		loadPercent.setText(str);
	}

	private void loadSettings() {
		SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
		float activityBrightness = sp.getFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, -1f);
		if (activityBrightness > 0) {
			int newValue = (int) (activityBrightness * 255);
			AndroidUtils.setActivityBrightness(mActivity, newValue);
		}
	}

	private void saveSettings() {
		SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
		float activityBrightness = AndroidUtils.getActivityBrightness(mActivity);
		if (activityBrightness > 0) {
			Editor editor = sp.edit();
			editor.putFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, activityBrightness);
			editor.commit();
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.vp_top_back_img) {
				BackKeyEvent backKeyEvent = new BackKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
				onKeyDown(KeyEvent.KEYCODE_BACK, backKeyEvent);
			}
		}
	};

	private float mX = 0;
	private float mY = 0;
	private static final float X_MIN_TOLERANCE = 20;
	private static final float X_MAX_TOLERANCE = 20;
	private static final float Y_MIN_TOLERANCE = 40;
	private static final float Y_MAX_TOLERANCE = 40;
	private boolean moved = false;

	private boolean downLeftRegion = false;
	private boolean downRightRegion = false;

	private boolean movedBrightness = false;
	private boolean movedVolume = false;
	private boolean movedPosition = false;
	
	private boolean mTriggerAdjustPosition = false;
	
	private void touchStart(float x, float y) {
		int screenWidth = mDisplayInformationFetcher.getScreenWidth();
		mX = x;
		mY = y;
		
		if (!mMediaController.isShowing()) {
			if (mX <= screenWidth / 6) {
				downLeftRegion = true;
			} else if (mX >= screenWidth - screenWidth/ 6) {
				downRightRegion = true;
			}
		}
		PauseFullScreenPopupWindow pauseFullScreenPopupWindow = mMediaController.getPauseFullScreenPopupWindow();
		mPauseFullScreen = (pauseFullScreenPopupWindow != null && pauseFullScreenPopupWindow.isShowing());
	}

	private void touchMove(float x, float y) {
		float distanceX = x - mX;
		float distanceY = y - mY;
		float dx = Math.abs(distanceX);
		float dy = Math.abs(distanceY);
		
		if (dx >= mTriggerAdjustPositionTolerance && dy <= Y_MAX_TOLERANCE && !mTriggerAdjustPosition && !mPauseFullScreen) {
			mTriggerAdjustPosition = true;
		}
		
		
		if (mTriggerAdjustPosition && dx >= mAdjustPositionStep && dy < Y_MAX_TOLERANCE) {
			if (mAirkanManager.isPlayingInLocal()) {
				movedPosition = true;
				if (mLocalMediaPlayer.isPlaying()) {
					mLocalMediaPlayer.pause();
					mMediaController.updatePlayingState(true);
				}
				adjustPlayPosition(distanceX);
			} else if (!mActivity.isFinishing() && mAirkanExistDeviceInfo != null) {
	//			mActivity.finish();
	//			Log.i("AIR KAN: do horizontal move event!!: ", distanceX + "");
			}
			moved = true;
			mX = x;
			mY = y;
		}
		
		if (downLeftRegion && dx <= X_MAX_TOLERANCE && dy >= Y_MIN_TOLERANCE) {
			if (mAirkanManager.isPlayingInLocal()) {
				adjustBrightness(distanceY);
				movedBrightness = true;
			}
			moved = true;
			mX = x;
			mY = y;
		}

		if (downRightRegion && dx <= X_MAX_TOLERANCE && dy >= Y_MIN_TOLERANCE) {
			if (mAirkanManager.isPlayingInLocal()) {
				adjustVolume(distanceY);
				movedVolume = true;
			}
			moved = true;
			mX = x;
			mY = y;
		}
	}

	private void touchUp(float x, float y) {
		if (!moved) {
			if (mAirkanManager.isPlayingInLocal()) {
				if (downLeftRegion) {
					showBrightnessPopupWindow();
					delayFadeOutBrightnessPopupWindows();
				} else if (downRightRegion) {
					showVolumePopupWindow();
					delayFadeOutVolumePopupWindow();
				} else if (mMediaController != null && !mPauseFullScreen) {
					toggleMediaControlsVisiblity();
				}
			} else {
				if (!downLeftRegion && !downRightRegion
						&& mMediaController != null && !mPauseFullScreen) {
					toggleMediaControlsVisiblity();
				}
			}
		} else {
			if (movedBrightness) {
				if (mBrightnessPopupWindow != null) {
					mBrightnessPopupWindow.getSeekbar().setPressed(false);
					delayFadeOutBrightnessPopupWindows();
				}
				movedBrightness = false;
			}
			if (movedVolume) {
				if (mVolumePopupWindow != null) {
					mVolumePopupWindow.getSeekbar().setPressed(false);
					delayFadeOutVolumePopupWindow();
				}
				movedVolume = false;
			}
			if (movedPosition) {
				if (mSwitchMediaOrientation == 0) {
					if (!mLocalMediaPlayer.isPlaying()) {
						mLocalMediaPlayer.start();
						mMediaController.updatePlayingState(true);
					}
				} else if (mSwitchMediaOrientation > 0) {
					mMediaController.playNextMedia();
				} else if (mSwitchMediaOrientation < 0) {
					mMediaController.playPreviousMedia();
				}
				
				movedPosition = false;
			}
			
			if (mTriggerAdjustPosition) {
				mTriggerAdjustPosition = false;
			}
			
			dismissUnusedPopupWindows();
		}
		moved = false;
		downLeftRegion = false;
		downRightRegion = false;
	}
	
	
	private void showBrightnessPopupWindow() {
		if (mBrightnessPopupWindow == null) {
			mBrightnessPopupWindow = new BrightnessPopupWindow(mActivity);
			if (DuoKanConstants.ENABLE_V5_UI) {
				mBrightnessPopupWindow = new BrightnessPopupWindow(mActivity, R.layout.vp_popup_left_vertical_seebar_group_v5);
			}
		}
		int currentValue = (int) (AndroidUtils.getActivityBrightness(mActivity) * 255);
		if (currentValue < 0) {
			currentValue = AndroidUtils.getSystemBrightness(mActivity);
		}
		
//		mBrightnessPopupWindow.setMaxSeekbarValue(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
		mBrightnessPopupWindow.updateSeekbarValue(currentValue / DuoKanConstants.BRIGHTNESS_STEP);
		if (!mBrightnessPopupWindow.isShowing()) {
			mBrightnessPopupWindow.show(this, mActivity);
			mBrightnessPopupWindow.getSeekbar().setPressed(false);
		}
	}

	private void showVolumePopupWindow() {
		if (mVolumePopupWindow == null) {
			mVolumePopupWindow = new VolumePopupWindow(mActivity);
			if (DuoKanConstants.ENABLE_V5_UI) {
				mVolumePopupWindow = new VolumePopupWindow(mActivity, R.layout.vp_popup_right_vertical_seekbar_group_v5);
			}
		}
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		}
		
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		mVolumePopupWindow.setMaxSeekbarValue(maxVolume);
		mVolumePopupWindow.updateSeekbarValue(currentVolume);
		if (!mVolumePopupWindow.isShowing()) {
			mVolumePopupWindow.show(this, mActivity);
			mVolumePopupWindow.getSeekbar().setPressed(false);
		}
	}

	private void adjustBrightness(float distanceY) {
		// Log.e(TAG, "adjust brightness y: " + distanceY);
		int currentValue = (int) (AndroidUtils.getActivityBrightness(mActivity) * 255);
		if (currentValue < 0) {
			currentValue = AndroidUtils.getSystemBrightness(mActivity);
		}
		showBrightnessPopupWindow();
		int newValue = getNewBrightnessValue(distanceY, currentValue);
		AndroidUtils.setActivityBrightness(mActivity, newValue);
		mBrightnessPopupWindow.updateSeekbarValue(newValue / DuoKanConstants.BRIGHTNESS_STEP);
	}

	private int getNewBrightnessValue(float distanceY, int currentValue) {
		int newValue = 0;
		if (distanceY > 0) {
			newValue = currentValue - DuoKanConstants.BRIGHTNESS_STEP;
		} else {
			newValue = currentValue + DuoKanConstants.BRIGHTNESS_STEP;
		}
		if (newValue > 255) {
			newValue = 255;
		}
		if (newValue < 2) {
			newValue = 2;
		}
		return newValue;
	}

	private void adjustVolume(final float distanceY) {
//		Log.e(TAG, "adjust volume y: " + distanceY);
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		}
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		showVolumePopupWindow();
		
		int newValue = getNewVolumeValue(distanceY, maxVolume, currentVolume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mVolumePopupWindow.updateSeekbarValue(newValue);
	}

	private int getNewVolumeValue(final float distanceY, final int maxVolume, final int currentVolume) {
		int newValue = 0;
		if (distanceY > 0) {
			newValue = currentVolume - 1;
		} else {
			newValue = currentVolume + 1;
		}
		if (newValue > maxVolume) {
			newValue = maxVolume;
		}
		if (newValue < 0) {
			newValue = 0;
		}
		return newValue;
	}

	private void adjustPlayPosition(float distanceX) {
		if (mMediaController != null && mMediaController.isShowing() && !DuoKanConstants.ENABLE_V5_UI) {
			mMediaController.hide();
		}
		if (mProgressTimePopupWindow == null) {
			mProgressTimePopupWindow = new ProgressTimePopupWindow(mActivity, false);
			mProgressTimePopupWindow.setTopStatusBarPopupWindow(mMediaController.getTopStatusBarPopupWindow());
			mProgressTimePopupWindow.setCtrlMenuPopupWindow(mMediaController.getCtrlMenuPopupWindow());
		}

		if (!mProgressTimePopupWindow.isShowing()) {
			int marginTop = (int) mActivity.getResources().getDimension(R.dimen.popup_center_progress_time_margin_top_big);
			mProgressTimePopupWindow.showAtLocation(this, Gravity.TOP, 0, marginTop);
		}
		
		int videoViewWidth = getWidth();
		int currentPosition = mLocalMediaPlayer.getCurrentPosition();
		int duration = mLocalMediaPlayer.getDuration();
		
		if (mProgressOnlyPopupWindow == null) {
			mProgressOnlyPopupWindow = new ProgressBarOnlyPopupWindow(mActivity);
		}
		if (!mProgressOnlyPopupWindow.isShowing() && !DuoKanConstants.ENABLE_V5_UI) {
			mProgressOnlyPopupWindow.setDuration(duration);
			mProgressOnlyPopupWindow.show(this);
		}
	
		mProgressTimePopupWindow.updatePosition(currentPosition);
		mProgressTimePopupWindow.setOrientation(distanceX > 0);

		int seekToPosition = getSeekToPosition(distanceX, videoViewWidth, currentPosition, duration);
		if (currentPosition == seekToPosition) {
			if (seekToPosition == duration) {
				mSwitchMediaOrientation = 1;
				mProgressTimePopupWindow.updatePosition(ProgressTimePopupWindow.NEXT_MEDIA_NOTICE_POSITION);
			} else if (seekToPosition == 0) {
				mSwitchMediaOrientation = -1;
				mProgressTimePopupWindow.updatePosition(ProgressTimePopupWindow.PREV_MEDIA_NOTICE_POSITION);
			}  
		} else {
			mSwitchMediaOrientation = 0;
			mLocalMediaPlayer.seekTo(seekToPosition);
			mProgressTimePopupWindow.updatePosition(seekToPosition);
			mProgressOnlyPopupWindow.updatePosition(seekToPosition);
		}
		
		if (DuoKanConstants.ENABLE_V5_UI) {
			mMediaController.show();
			mProgressOnlyPopupWindow.dismiss();
			if (mMediaController.getTopStatusBarPopupWindow() != null && mMediaController.getTopStatusBarPopupWindow().isShowing()) {
				mMediaController.getTopStatusBarPopupWindow().dismiss();
			}
			mMediaController.removeShowFullScreenPauseMessage();
		}
		
	}

	private int getSeekToPosition(float distanceX, int videoViewWidth, int currentPosition, int duration) {
		int seekToPosition = 0;
		int stepPosition = ((int) (Math.abs(distanceX) / mAdjustPositionStep)) * 1000;
		if (distanceX < 0) {
			seekToPosition = currentPosition - stepPosition;
			if (seekToPosition < 0) {
				seekToPosition = 0;
			}
		} else {
			seekToPosition = currentPosition + stepPosition;
			if (seekToPosition > duration) {
				seekToPosition = duration;
			}
		}
		return seekToPosition;
	}

	private void delayFadeOutBrightnessPopupWindows() {
		mHandler.removeMessages(FADE_OUT_BRIGHTNESS_MESSAGE_WHAT);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_BRIGHTNESS_MESSAGE_WHAT, BRIGHTNESS_VOLUME_DISMISS_TIME);
	}
	
	private void delayFadeOutVolumePopupWindow() {
		mHandler.removeMessages(FADE_OUT_VOLUME_MESSAGE_WHAT);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_VOLUME_MESSAGE_WHAT, BRIGHTNESS_VOLUME_DISMISS_TIME);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case FADE_OUT_VOLUME_MESSAGE_WHAT:
				if (mVolumePopupWindow != null && mVolumePopupWindow.isShowing()  && !mVolumePopupWindow.isAlwaysShowing()) {
					mVolumePopupWindow.dismiss();
				}				
				break;
			case FADE_OUT_BRIGHTNESS_MESSAGE_WHAT:
				if (mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing() && !mBrightnessPopupWindow.isAlwaysShowing()) {
					mBrightnessPopupWindow.dismiss();
				}
				break;
			case CONTINUE_PLAY_SEEK_TO_WHAT: {
					int lastPosition = msg.arg1;
	//				Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "Last postion :" + lastPosition);
	//				Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "Duration :" + mLocalMediaPlayer.getDuration());
					if (lastPosition != 0 && (mLocalMediaPlayer.getDuration() - lastPosition) >= 1000) {
						String fromString = mActivity.getResources().getString(R.string.toast_message_continue_play_from);
						String hourString = mActivity.getResources().getString(R.string.toast_message_continue_play_hour);
						String minuteString = mActivity.getResources().getString(R.string.toast_message_continue_play_minute);
						String secondString = mActivity.getResources().getString(R.string.toast_message_continue_play_second_and_play);
						DKTimeFormatter dkf = DKTimeFormatter.getInstance();
					StringBuilder sBuilder = new StringBuilder();
					sBuilder.append(fromString);
					if (dkf.getHoursForTime(lastPosition) != 0) {
						sBuilder.append(dkf.getHoursForTime(lastPosition)).append(hourString);
					}
					sBuilder.append(dkf.getMinutesForTime(lastPosition)).append(minuteString);
					sBuilder.append(dkf.getSecondsForTime(lastPosition)).append(secondString);
							
					mContinuePlayToast = Toast.makeText(mActivity, sBuilder.toString(), Toast.LENGTH_SHORT);
						mContinuePlayToast.show();
	//					Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "SEEK TO :" + lastPosition);
						mLocalMediaPlayer.seekTo(lastPosition);
					}
				}
				break;
			case CONTINUE_PLAY_START_WHAT:
				if (!VideoPlayerActivity.isVideoPaused && !VideoPlayerActivity.isActivityPaused) {
					mLocalMediaPlayer.start();
				}
				break;
			case FADE_OUT_ERROR_DIALOG_WHAT:
				if (mAlertDialog != null && mAlertDialog.isShowing()) {
					mAlertDialog.cancel();
					onCompletionCalled();
				}
				break;
			case QUERY_PROGRESS_AFTER_PREPARED:
				if (mMediaController != null && mMediaController.isShowing() && mLocalMediaPlayer != null) {
					if (!mLocalMediaPlayer.isPlaying()) {
						int delay = 500;
						Log.i(TAG, "Mediaplayer is not playing, query after" +  delay + "ms");
						mHandler.sendEmptyMessageDelayed(QUERY_PROGRESS_AFTER_PREPARED, delay);
					} else {
						Log.i(TAG, "Mediaplayer is playing, show progress");
						mMediaController.setUpdateProgressEnable(false);
						mMediaController.setUpdateProgressEnable(true);

						if ( VideoPlayerActivity.isVideoPaused ){
					        mLocalMediaPlayer.pause();
					        //mLocalMediaPlayer.seekTo(VideoPlayerActivity.VIDEO_TIME_BRFORE_PAUSE);
					        //Log.i(TAG, "video is seekto:"+VideoPlayerActivity.VIDEO_TIME_BRFORE_PAUSE);
							mMediaController.updatePlayingState(false);
							if (mBufferingProgressDialog != null && !mBufferingProgressDialog.isShowing()) {
								mMediaController.showPauseFullScreenPopupWindow();
							}
							VideoPlayerActivity.isVideoPaused = false;
							VideoPlayerActivity.videoPausedPosition = 0;							
						}
					}
				}
				break;
			case MEDIASETINFO_UPDATED:
				mMediaController.updateViews();
				break;
			default:
				break;
			}
		}
	};
	
	private void dismissUnusedPopupWindows() {
		if (mProgressTimePopupWindow != null && mProgressTimePopupWindow.isShowing()) {
			mProgressTimePopupWindow.dismiss();
		}
		if (mProgressOnlyPopupWindow != null && mProgressOnlyPopupWindow.isShowing()) {
			mProgressOnlyPopupWindow.dismiss();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (VideoPlayerActivity.isScreenSaver) {
			cleanOnBackPressed();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			mActivity.startActivity(intent);
			mActivity.finish();
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchStart(event.getX(), event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			touchMove(event.getX(), event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			touchUp(event.getX(), event.getY());
		}
		return true;
	}

	private void toggleMediaControlsVisiblity() {
		if (this.mActivity != null && this.mActivity.isFinishing()) {
			return;
		}

		this.bringToFront();
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			if (mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing()) {
				mBrightnessPopupWindow.dismiss();
			}
			if (mVolumePopupWindow != null && mVolumePopupWindow.isShowing()) {
				mVolumePopupWindow.dismiss();
			}
			showStatusBar();
			mMediaController.show();
			//VideoPlayerActivity.LoadMediaSetInfos(mContext, mHandler);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "KEY DOWN EVENT!!!!");
		
		if (VideoPlayerActivity.isScreenSaver && 
				(event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_DOWN &&
				 event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_UP)) {
			cleanOnBackPressed();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			mActivity.startActivity(intent);
			mActivity.finish();
			return false;
		}

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Log.i(TAG, "BACK DOWN!!!!");
			if (event instanceof BackKeyEvent) {
				cleanOnBackPressed();
				mActivity.finish();
				return false;
			}
			if (mBackToast == null) {
			   mBackToast = Toast.makeText(mActivity, R.string.toast_back_key_pressed_notice, Toast.LENGTH_SHORT);
			}
			if (mBackToast.getView() == null) {
				return false;
			}
			if (mBackToast.getView().isShown()) {
				mBackToast.cancel();
				cleanOnBackPressed();
			} else {
				mBackToast.show();
				return true;
			}
		}
		
		if (mLoadingBackgroundView != null && (mLoadingBackgroundView.getVisibility() == View.VISIBLE)) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				return super.onKeyDown(keyCode, event);
			} else {
				return true;
			}
		}
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			return onMenuClick();
		}
		
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Log.i(TAG, "VOLUME DOWN!!!!");
			if (mAirkanManager.onVolumeKeyEvent(event)) {
				return true;
			}
			adjustVolume(1);
			delayFadeOutVolumePopupWindow();
			return true;
		}

		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			 Log.i(TAG, "VOLUME UP");
			if (mAirkanManager.onVolumeKeyEvent(event)) {
				return true;
			}
			adjustVolume(-1);
			delayFadeOutVolumePopupWindow();
			return true;
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE || event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK ){
			Log.i(TAG, "MEDIA PAUSE OR PLAY");
			mMediaController.pauseClickListener();
			return true;
		}
        if(event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS )
        	return true;
		if (DuoKanCodecConstants.IS_X3) {
	        if(mOptionMenuPopupWindow != null && mOptionMenuPopupWindow.getToast() != null){
	        	Toast toast = mOptionMenuPopupWindow.getToast();
	        	Log.i("EffectDiracSound", "onAudioEffectClicked,toast");
	        	if (toast.getView() != null && toast.getView().isShown())
	        		toast.cancel();
	        }
		}
		return super.onKeyDown(keyCode, event);
	}

	private void savePlayPosition(final int position, final int duration) {
		if (mUri == null) {
			return;
		}
		PlayHistoryEntry oldEntry = null;

		if (VideoPlayerActivity.mediaId > 0) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryById(String.valueOf(VideoPlayerActivity.mediaId));
		} else if (VideoPlayerActivity.curMediaHtml5Url != null) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryByHtml5Page(VideoPlayerActivity.curMediaHtml5Url);
		} else {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistory(mUri);
		}

		if (oldEntry != null) {
			if (position == duration) {
				oldEntry.setPosition(0);
			} else {
				oldEntry.setPosition(position);
			}
		}
	}

	private void savePlayHistory(final int position, final int duration) {
		if (mUri == null) {
			return;
		}

		PlayHistoryEntry oldEntry = null;
		if (VideoPlayerActivity.mediaId > 0) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryById(String.valueOf(VideoPlayerActivity.mediaId));
		} else if (VideoPlayerActivity.curMediaHtml5Url != null) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryByHtml5Page(VideoPlayerActivity.curMediaHtml5Url);
		} else {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistory(mUri);
		}

		if (oldEntry != null) {
			if (!VideoPlayerActivity.isVideoComplete && mPrepared) {
				if (position == duration) {
					oldEntry.setPosition(0);
				} else {
					oldEntry.setPosition(position);
				}
			}
			oldEntry.setDuration(duration);
			VideoPlayerActivity.PLAY_HISTORY_MANAGER.save();
		}

		if (AndroidUtils.isOnlineVideo(mUri) && mPrepared) {
			long totalTime = (System.currentTimeMillis() - this.mStartPlayingTimeStamp);
			final long playingTime = (totalTime - mLocalMediaPlayer.getPausedTotalTime() - this.mTotalBufferedTime) / 1000;
			final String dayDate = DKTimeFormatter.getInstance().longToDayDate(System.currentTimeMillis());
			savePlayInfoFile(playingTime, dayDate, oldEntry);
		}
	}

	private void savePlayInfoFile(long playingTime, String dayDate, PlayHistoryEntry historyEntry) {
		if (historyEntry == null || mUri == null) {
			return;
		}
		String scheme = mUri.getScheme();
		if(scheme == null || scheme.equals("rtsp")){
			return;
		}
		
		PlayHistoryManager playInfoManager = new PlayHistoryManager(mActivity, PlayHistoryManager.PLAY_INFO_FILE);
		playInfoManager.load();
		PlayHistoryEntry oldEntry = playInfoManager.findPlayHistory(mUri);
		if (oldEntry == null) {
			oldEntry = new PlayHistoryEntry(mUri.toString());
			oldEntry.setVideoName(VideoPlayerActivity.mediaTitle);
			playInfoManager.addToHistory(oldEntry);
		}
		String mediaId = historyEntry.getMediaId();
		String mediaCi = historyEntry.getMediaCi();
		if (mediaId != null) {
			oldEntry.setMediaId(mediaId);
		}
		if (mediaCi != null) {
			oldEntry.setMediaCi(mediaCi);
		}
		updatePlayTimeInfoJSONObject(playingTime, dayDate, oldEntry);
//		Log.e(TAG, "save to file!!!!!!!!!!!!" + " playing time: " + playingTime + " dayDate: " + dayDate);
		playInfoManager.save();
	}
	
	private void updatePlayTimeInfoJSONObject(final long playingTime, final String dayDate, PlayHistoryEntry oldEntry) {
		JSONObject jsonObject = oldEntry.getDatePlayInfoJsonObject();
		JSONArray jsonArray = null;
		if (jsonObject == null) {
			jsonObject = new JSONObject();
			jsonArray = new JSONArray();
			try {
				jsonObject.put(PlayHistoryManager.JSON_PLAY_INFO_ROOT_NAME, jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			jsonArray = jsonObject.getJSONArray(PlayHistoryManager.JSON_PLAY_INFO_ROOT_NAME);
			
			JSONObject itemObject = findDateItemObject(dayDate, jsonArray);
			if (itemObject == null) {
				itemObject = new JSONObject();
				itemObject.put(PlayHistoryManager.JSON_PLAY_INFO_ITEM_DATE_NAME, dayDate);
				itemObject.put(PlayHistoryManager.JSON_PLAY_INFO_ITEM_PLAYING_TIME, 0L);
				itemObject.put(PlayHistoryManager.JSON_PLAY_INFO_ITEM_BUFFER_TIME_NAME, 0);
				jsonArray.put(itemObject);
			} else {
//				Log.e(TAG, "found date item: " + dayDate);
			}
			
			long oldPlayingTime = itemObject.getLong(PlayHistoryManager.JSON_PLAY_INFO_ITEM_PLAYING_TIME);
			int oldBufferTimes = itemObject.getInt(PlayHistoryManager.JSON_PLAY_INFO_ITEM_BUFFER_TIME_NAME);
			
//			Log.e(TAG, "oldPlayingTime: " + oldPlayingTime);
//			Log.e(TAG, "oldBufferTimes: " + oldBufferTimes);
			
			itemObject.put(PlayHistoryManager.JSON_PLAY_INFO_ITEM_PLAYING_TIME, playingTime + oldPlayingTime);
			itemObject.put(PlayHistoryManager.JSON_PLAY_INFO_ITEM_BUFFER_TIME_NAME, mBufferTimes + oldBufferTimes);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		oldEntry.setDatePlayInfoJsonObject(jsonObject);
	}
	
	private JSONObject findDateItemObject(String dayDate, JSONArray jsonArray) throws JSONException {
		for(int i=0; i < jsonArray.length(); i++) {
			Object object = jsonArray.get(i);
			if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) object;
				String date = jsonObject.getString(PlayHistoryManager.JSON_PLAY_INFO_ITEM_DATE_NAME);
				if (date.equals(dayDate)) {
					return jsonObject;
				}
			}
		}
		return null;
	}

	private void cleanOnBackPressed() {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		if (mAirkanManager != null) {
			if (mAirkanManager.isPlayingInLocal()) {

			} else {
				mAirkanManager.stopRemotePlay();
				if (!VideoPlayerActivity.isScreenSaver) {
					savePlayPosition(mAirkanManager.getCurrentPosition(), mAirkanManager.getDuration());
				}
			}
		}
		PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
		if (mBufferingProgressDialog != null && mBufferingProgressDialog.isShowing()) {
			mBufferingProgressDialog.dismissAndFinishActivity(mActivity);
		}
	}
	
	public void setInput(String[] uris, int playIndex, Activity activity) {
		if (uris == null || uris.length == 0 || activity == null 
				|| playIndex < 0 || playIndex > uris.length-1) {
			throw new IllegalArgumentException();
		}
		this.mUris = uris;
		this.mActivity = activity;
		mDecorView = mActivity.getWindow().getDecorView();
		hideStatusBar();
		mMediaController.setUriArray(uris, playIndex);
		
		mUri = Uri.parse(uris[playIndex]);
	}

//	private boolean isOnlineVideo(Uri uri) {
//		String scheme = uri.getScheme();
//		if (scheme != null && (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp"))) {
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public void onAirKanChanged(AirkanChangedEvent event) {
		
		if (mAirkanBackgroundView == null) {
			mAirkanBackgroundView = inflateAirkanBackgroundView();
		}
		if (event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_PLAY_TO_DEVICE) {
			Log.i(TAG, "on airkan changed: " + "Play to device");
			mAirkanBackgroundView.setVisibility(View.VISIBLE);
			updateAirkanDeviceName();
			if (!VpCtrlMediaInfoPopupWindow.isNull() && VpCtrlMediaInfoPopupWindow.getInstance(mActivity).isShowing()) {
				VpCtrlMediaInfoPopupWindow.getInstance(mActivity).dismiss();
			}
		} else if (event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_BACK_TO_PHONE) {
			Log.i(TAG, "on airkan changed: " + "Back to phone");
			mAirkanBackgroundView.setVisibility(View.GONE);
		} else if (event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_PLAY_STOPED) {
			Log.i(TAG, "on airkan changed: " + "Play stoped");
			if (mMediaController != null) {
				mMediaController.hide();
			}
			PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
			mActivity.finish();
		}
		this.requestLayout();
	}

	private void updateAirkanDeviceName() {
		TextView textView = (TextView) mAirkanBackgroundView.findViewById(R.id.play_in_other_device_textview);
		String playInString = mActivity.getString(R.string.airkan_video_playing_in);
		String playString = " ";//mActivity.getString(R.string.airkan_video_playing);
		textView.setText(playInString + playString + mAirkanManager.getPlayingDeviceName() );
	}

	private View inflateAirkanBackgroundView() {
		View result = mLayoutInflater.inflate(R.layout.vp_stub_media_play_in_other_device_desc, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(result, lp);
		result.setVisibility(View.INVISIBLE);
		return result;
	}
	
	private View inflateLoadingBackgroundView() {
		View result = null;
        try {
            result = mLayoutInflater.inflate(R.layout.vp_stub_loading_video_for_netplaying, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
            System.runFinalization();
            System.gc();
            result = mLayoutInflater.inflate(R.layout.vp_stub_loading_video_for_netplaying, null);
        }

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(result, lp);
		result.setVisibility(View.INVISIBLE);
		Log.i("test","inflateLoadingBackgroundView" );
		return result;
	}
	
	private View inflateLoadingBackgroundViewV5() {
		View result = mLayoutInflater.inflate(R.layout.vp_stub_loading_video_for_netplaying_v5, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(result, lp);
		result.setVisibility(View.INVISIBLE);
	
//		View progressGroupView = result.findViewById(R.id.loading_video_bottom_progressbar_group);
//		progressGroupView.setBackgroundColor(mActivity.getResources().getColor(R.color.vp_black));
//		
//		ProgressBar progressBar = (ProgressBar) progressGroupView.findViewById(R.id.mediacontroller_progress);
//		progressBar.setEnabled(false);
//		
//		TextView positionView = (TextView) progressGroupView.findViewById(R.id.time_current);
//		positionView.setText(DKTimeFormatter.getInstance().stringForTime(0));
//		TextView timeView = (TextView) progressGroupView.findViewById(R.id.time);
//		timeView.setText(DKTimeFormatter.getInstance().stringForTime(0));
		return result;
	}
	
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		onPreparedCalled();
	}

	public void updatePlayingState(boolean state) {
		if (mMediaController != null) {
			mMediaController.updatePlayingState(state);
		}
	}

	private void onPreparedCalled() {
		Log.i(TAG, "mediaplayer " +  " onPrepared");
		removeSeekToMessage();

		if (!VideoPlayerActivity.isScreenSaver) {
			checkPlayHistory();
		} else {
			Message m = Message.obtain();
			m.what = CONTINUE_PLAY_START_WHAT;
			mHandler.sendMessage(m);
		}

		SharedPreferences sp  = mActivity.getPreferences(Context.MODE_PRIVATE);
		boolean isFixedOrientation = !sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR, false);
		if (isFixedOrientation && mVideoSizeAdjustable != null) {
			int videoWidth = mVideoSizeAdjustable.getVideoWidth();
			int videoHeight = mVideoSizeAdjustable.getVideoHeight();

			if (videoWidth < videoHeight) {
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else {
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
		}
		if (VpCtrlMediaInfoPopupWindow.getInstance(mActivity).isShowing()) {
			VpCtrlMediaInfoPopupWindow.getInstance(mActivity).updateValues(mLocalMediaPlayer.getMediaInfo());
		}
		if (this.mActivity != null && this.mActivity.isFinishing()) {
			return;
		}
		hideLoadingView();
		if (mMediaController != null) {
			mMediaController.updatePlayingState(true);
		}
		if (mOptionSubMenuPopupWindow != null) {
			mOptionSubMenuPopupWindow.updateVideoPlayerSize();
		}
		mHandler.sendEmptyMessageDelayed(QUERY_PROGRESS_AFTER_PREPARED, 200);
		mPrepared = true;
		resetPlayInfo();
		this.bringToFront();
	}

	private void resetPlayInfo() {
		mStartPlayingTimeStamp = System.currentTimeMillis();
		mBufferTimes = 0;
		mTotalPausedTime = 0;
		mTotalBufferedTime = 0;
	}

	private void removeSeekToMessage() {
		mHandler.removeMessages(CONTINUE_PLAY_SEEK_TO_WHAT);
		if (mContinuePlayToast != null) {
			mContinuePlayToast.cancel();
		}
	}

	public void checkPlayHistory() {
		if (mUri == null) {
			return;
		}
	
		PlayHistoryEntry oldEntry = null;
		if (VideoPlayerActivity.mediaId > 0) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryById(String.valueOf(VideoPlayerActivity.mediaId));
			if (oldEntry != null) {
				String oldMediaCi = oldEntry.getMediaCi();
				if (oldMediaCi != null) {
					try {
						int episodeIdx = Integer.parseInt(oldMediaCi);
						if (episodeIdx != VideoPlayerActivity.curCi && VideoPlayerActivity.curCi > 0) {
							oldEntry.setPosition(0);
						}
					} catch (NumberFormatException e) {
						//ignore
					}
				}
			}
		} else if (VideoPlayerActivity.curMediaHtml5Url != null) {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistoryByHtml5Page(VideoPlayerActivity.curMediaHtml5Url);
			if (oldEntry != null) {
				String oldMediaCi = oldEntry.getMediaCi();
				if (oldMediaCi != null) {
					try {
						int episodeIdx = Integer.parseInt(oldMediaCi);
						if (episodeIdx != VideoPlayerActivity.curCi && VideoPlayerActivity.curCi > 0) {
							oldEntry.setPosition(0);
						}
					} catch (NumberFormatException e) {
						//ignore
					}
				}
			}
		} else {
			oldEntry = VideoPlayerActivity.PLAY_HISTORY_MANAGER.findPlayHistory(mUri);
		}
		if (oldEntry != null) {
			int lastPosition = oldEntry.getPosition();
			if (lastPosition != 0) {
				Log.i(TAG, "last postion: " + lastPosition);
				Message msg = Message.obtain();
				msg.what = CONTINUE_PLAY_SEEK_TO_WHAT;
				msg.arg1 = lastPosition;
				mHandler.sendMessage(msg);
			}
			oldEntry.setUri(mUri.toString());
			oldEntry.setHtml5Page(VideoPlayerActivity.curMediaHtml5Url);
			oldEntry.setMediaId(String.valueOf(VideoPlayerActivity.mediaId));
			oldEntry.setMediaCi(String.valueOf(VideoPlayerActivity.curCi));
			VideoPlayerActivity.PLAY_HISTORY_MANAGER.moveToFirst(oldEntry);
			Log.i(TAG, "mUri: " + mUri + " mHtml5Uris: " + VideoPlayerActivity.curMediaHtml5Url + " pos:" + oldEntry.getPosition());
		} else {
			PlayHistoryEntry entry = new PlayHistoryEntry(mUri.toString());
			entry.setTimeStamp(System.currentTimeMillis());
			entry.setUri(mUri.toString());
			entry.setHtml5Page(VideoPlayerActivity.curMediaHtml5Url);
			entry.setMediaId(String.valueOf(VideoPlayerActivity.mediaId));
			entry.setMediaCi(String.valueOf(VideoPlayerActivity.curCi));
			entry.setVideoName(VideoPlayerActivity.mediaTitle);
			VideoPlayerActivity.PLAY_HISTORY_MANAGER.addToHistory(entry);
			Log.i(TAG, "mUri: " + mUri + " mHtml5Uris: " + VideoPlayerActivity.curMediaHtml5Url + " pos:" + entry.getPosition());
		}
	 
		Message m = Message.obtain();
		m.what = CONTINUE_PLAY_START_WHAT;
		mHandler.sendMessage(m);
	}
	
	private PlayHistoryEntry getMetaDataEntry(String uri) {
//		Log.e(TAG, "Map : " + mMetaDataMap);
//		Log.e(TAG, "get url: " + uri);
		if (mMetaDataMap != null) {
			return mMetaDataMap.get(uri);
		}
		return null;
	}
	
	@Override 
	public void onCompletion(MediaPlayer mp) {
		onCompletionCalled();
	}

	private void onCompletionCalled() {
		VideoPlayerActivity.isVideoComplete = true;
		if (mLocalMediaPlayer != null && !VideoPlayerActivity.isScreenSaver) {
			savePlayPosition(mLocalMediaPlayer.getDuration(), mLocalMediaPlayer.getDuration());
		}
		if (VideoPlayerActivity.curMediaIndex >= 0) {
			if (mMediaController != null) {
				if (!(mMediaController.playNextEpisode())) {
					mMediaController.hide();
					mActivity.finish();
				}
			}
		} else if (mUris.length != 1 || VideoPlayerActivity.isScreenSaver) {
			if (mMediaController != null) {
				mMediaController.playNextMedia();
			}
		} else {
			if (mActivity != null && !mActivity.isFinishing()) {
				PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
				if (mMediaController != null) {
					mMediaController.hide();
				}
				mActivity.finish();
			}
		}
	}

	public void setVideoSizeAdjustable(LocalVideoPlaySizeAdjustable videoSizeAdjustable) {
		this.mVideoSizeAdjustable = videoSizeAdjustable;
	}
	
	public String getPlayingUri() {
		return mMediaController.getPlayingUri();
	}

//	@Override
//	public void onTimedText(com.duokan.MediaPlayer mp, TimedText timedText) {
////		Log.i("time: ", timedText + "");
//		String content = DKTimedTextDecoder.getInstance().getContent(timedText);
////		Log.i("content: ", content);
//	}
	
	public void setAirkanExistDeviceInfo(AirkanExistDeviceInfo airkanExistDeviceInfo) {
		this.mAirkanExistDeviceInfo = airkanExistDeviceInfo;
	}

	public void setDirectAirkanUri(Uri uri) {
		mAirkanManager.setDirectAirkanUri(uri);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
//		Log.e("SEEK COMPLETE", "SEEK COMPLETE");
	}

	private void onScreenOrientationChanged(int orientation) {
		if (orientation == Configuration.ORIENTATION_PORTRAIT || orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				Log.i(TAG, "Orientation changed to PORTRAIT ");
			} else {
				Log.i(TAG, "Orientation changed to LANDSCAPE ");
			}
			mMediaController.setAnchorView(this);
			mMediaController.onScreenOrientationChanged(orientation);
			
			if (!mAirkanManager.isPlayingInLocal() && mAirkanBackgroundView != null) {
				this.removeView(mAirkanBackgroundView);
				mAirkanBackgroundView = null;
				mAirkanBackgroundView = inflateAirkanBackgroundView();
				updateAirkanDeviceName();
				mAirkanBackgroundView.setVisibility(View.VISIBLE);
			}
			
			updateAdjustPositionStep();
			
			if (mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing()) {
				mBrightnessPopupWindow.dismiss();
			}
			mBrightnessPopupWindow = null;
			
			if (mVolumePopupWindow != null && mVolumePopupWindow.isShowing()) {
				mVolumePopupWindow.dismiss();
			}
			mVolumePopupWindow = null;
			
			if (mOptionSubMenuPopupWindow != null && mOptionSubMenuPopupWindow.isShowing()) {
				mOptionSubMenuPopupWindow.dismiss();
				mOptionSubMenuPopupWindow.show(this);
			}
			
			if (mOptionSubMenuPopupWindow != null) {
				mOptionSubMenuPopupWindow.updateVideoPlayerSize();
			}
			
			if (mOptionMenuPopupWindow != null && mOptionMenuPopupWindow.isShowing()) {
				mOptionMenuPopupWindow.dismiss();
				mOptionMenuPopupWindow.show(this);
			}
			
			if (mProgressTimePopupWindow != null && mProgressTimePopupWindow.isShowing()) {
				mProgressTimePopupWindow.dismiss();
			}
			mProgressTimePopupWindow = null;
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			Log.i(TAG, "Buffering start");
			bufferingStart();
		}
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			Log.i(TAG, "Buffering end");
			bufferingEnd();
		}
		return true;
	}

	private void bufferingStart() {
		if (mMediaController instanceof OriginMediaControllerV5) {
			if (mLoadingBackgroundView != null && mLoadingBackgroundView.isShown()) {
				hideLoadingView();
			}
			((OriginMediaControllerV5) mMediaController).bufferingStart();
		} else {
			if (mBufferingProgressDialog == null) {
				mBufferingProgressDialog = new BufferingProgressDialog(mActivity);
				mBufferingProgressDialog.setOnKeyDownListener(this);
			}
			mBufferingProgressDialog.show();
		}
		mBufferedStartTimeStamp = System.currentTimeMillis();
		mBufferTimes++;
	}
	
	private void bufferingEnd() {
		this.mTotalBufferedTime = mTotalBufferedTime + (System.currentTimeMillis() - mBufferedStartTimeStamp);
		if (mMediaController instanceof OriginMediaControllerV5) {
			((OriginMediaControllerV5) mMediaController).bufferingEnd();
		} else {
			if (mBufferingProgressDialog != null) {
				mBufferingProgressDialog.dismiss();
			}
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		onBufferUpdatingCalled(percent);
	}

	@Override
	public boolean onInfo(com.duokan.MediaPlayer mp, int what, int extra) {
		Log.i("on info", "on info");
		if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			Log.i(TAG, "Buffering start");
			bufferingStart();
		}
		
		if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_UPDATE) {
			Log.i(TAG, "Buffered : " + extra + " %");
			if (mMediaController != null) {
				if (VideoPlayerActivity.curMediaIndex >= 0 
						&& (VideoPlayerActivity.curMediaIndex < VideoPlayerActivity.mediaCount - 1)) {
					mMediaController.preloadNextEpisodeUri();
				}
			}
			if (mMediaController instanceof OriginMediaControllerV5) {
				((OriginMediaControllerV5) mMediaController).bufferUpdating(extra);
			}
		}
		
		if (what == com.duokan.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			Log.i(TAG, "Buffering end");
			bufferingEnd();
		}
		return true;
	}

	@Override
	public void onSeekComplete(com.duokan.MediaPlayer mp) {
	}

	@Override
	public void onCompletion(com.duokan.MediaPlayer mp) {
		onCompletionCalled();
	}

	@Override
	public void onPrepared(com.duokan.MediaPlayer mp) {
		onPreparedCalled();
	}
	
	@Override
	public void onBufferingUpdate(com.duokan.MediaPlayer mp, int percent) {
		onBufferUpdatingCalled(percent);
	}

	private void onBufferUpdatingCalled(int percent) {
		Log.i("on buffered updating: ", percent + "");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.i(TAG, "onAttachedToWindow");
	}
	
	public void checkNetwork(Uri uri) {
		if (uri == null) {
			return;
		}
		mPrepared = false;
		String scheme = uri.getScheme();
		boolean isNetworkConnected = AndroidUtils.isNetworkConncected(mActivity);
		Log.i(TAG, "check network");
		Log.i(TAG, "scheme: " + scheme);
		Log.i(TAG, "network connected: " + isNetworkConnected);
		if (AndroidUtils.isOnlineVideo(uri) && mAirkanManager.isPlayingInLocal()) {
			if (!isNetworkConnected) {
				return;
			}
			showLoadingView(VideoPlayerActivity.curCi);
		}
		checkValidMedia(uri);
	}

	@Override
	public boolean onError(com.duokan.MediaPlayer mp, int what, int extra) {
		onErrorCalled(what, extra);
		return false;
	}

	private void onErrorCalled(int what, int extra) {
		Log.e(TAG, "On error called!!!");
		hideLoadingView();
		
		showErrorDialog(what);
	}

	private void showErrorDialog(int what) {
        int messageId;
        if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
        	  messageId = R.string.vp_VideoView_error_text_invalid_progressive_playback;
        } else {
        	  messageId = R.string.vp_VideoView_error_text_unknown;
        } 
        if (AndroidUtils.isOnlineVideo(mUri) && !AndroidUtils.isNetworkConncected(mActivity)) {
        	messageId = R.string.vp_VideoView_error_network_not_available;
        }
        
        mHandler.sendEmptyMessageDelayed(FADE_OUT_ERROR_DIALOG_WHAT, 5000);
		mAlertDialog = new AlertDialog.Builder(mActivity)
    	.setTitle(R.string.vp_VideoView_error_title)
    	.setMessage(messageId)
    	.setPositiveButton(R.string.vp_VideoView_error_button,
    			new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            			/* If we get here, there is no onError listener, so
            			 * at least inform them that the video is over.
            			 */
            			mHandler.removeMessages(FADE_OUT_ERROR_DIALOG_WHAT);
            			//onCompletionCalled();
            			mActivity.finish();
            }
        })
        .setCancelable(false)
	    .show();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		onErrorCalled(what, extra);
		return false;
	}

	@Override
	public void onVideoSizeChanged(com.duokan.MediaPlayer mp, int width, int height) {
		if (VpCtrlMediaInfoPopupWindow.getInstance(mActivity).isShowing()) {
			VpCtrlMediaInfoPopupWindow.getInstance(mActivity).updateValues(mLocalMediaPlayer.getMediaInfo());
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	}

	public void setUri(Uri uri) {
		this.mUri = uri;
	}

	@Override
	public void onTimedText(com.duokan.MediaPlayer mp, TimedText text) {
		
	}

	public void setTitleMap(Map<String, PlayHistoryEntry> map) {
		mMetaDataMap = map;
		mMediaController.setTitleMap(map);
	}
	
	public void onNewIntent() {
		if (!mAirkanManager.isPlayingInLocal()) {
			mAirkanManager.takebackToPhone();
			if (mMediaController != null) {
				mMediaController.hide();
			}
			PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
			hideLoadingView();
		}
		mAirkanManager.closeDeviceManager();
	}
	
	public void checkValidMedia(Uri uri) {
		if (uri == null) {
			return;
		}
		if (!DuoKanUtils.isValidFormatVideo(uri)) {
			if (mLocalMediaPlayer != null) {
				mLocalMediaPlayer.stopLocalPlayForMediaSwitch();
			}
			onErrorCalled(0, 0);
		}
	}
	
	public boolean isShowing() {
		return mMediaController.isShowing();
	}
	
	public void show() {
		mMediaController.show();
	}
	
	public void hide() {
		mMediaController.hide();
	}

	public void bindAirkanService(){ 
		mAirkanManager.openDeviceManager();
	}
	
	public void unbindAirkanService() {
		mAirkanManager.closeDeviceManager();
	}
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
//		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
////			Log.e("config: ", "PORT!!!!!!!!!!!!!");
////			mVideoView.onScreenOrientationChanged(newConfig.orientation);
//		}
//		
//		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
////			Log.e("config: ", "LANDSCAPE!!!!!!!!!!!!!");
////			mVideoView.onScreenOrientationChanged(newConfig.orientation);
//		}
		onScreenOrientationChanged(newConfig.orientation);
		super.onConfigurationChanged(newConfig);
	}
	
	public static interface ScreenOrientationListener {
		 void onScreenOrientationChanged(int orientation);
	}
	
	public static class BackKeyEvent extends KeyEvent {

		public BackKeyEvent(int action, int code) {
			super(action, code);
		}

	}
	
	private static class BufferingProgressDialog extends Dialog {
		private Context mContext;
		private View mOnKeyDownListener;
		
		public BufferingProgressDialog(Context context) {
			super(context, R.style.buffer_dialog_style);
			this.mContext = context;
			
			setupViews();
		}

		private void setupViews() {
			this.setContentView(R.layout.vp_dialog_buffering);
			this.setCancelable(false);
			this.setCanceledOnTouchOutside(false);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

		public void setOnKeyDownListener(View onKeyDownListener) {
			this.mOnKeyDownListener = onKeyDownListener;
		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			if (mOnKeyDownListener != null) {
				mOnKeyDownListener.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			}
		}

		public void dismissAndFinishActivity(Activity activity) {
			this.dismiss();
			if (activity != null && !activity.isFinishing()) {
				activity.finish();
			}
		}
	}

	public void set3dMode(boolean mode) {
		mLocalMediaPlayer.set3dMode(mode);
	}
	
	public boolean get3dMode() {
		return mLocalMediaPlayer.get3dMode();
	}

}
