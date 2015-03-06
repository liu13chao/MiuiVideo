package com.miui.videoplayer.framework.views;

import java.util.List;
import java.util.Map;

import miui.app.AlertDialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.miui.videoplayer.Constants;
import com.miui.video.R;
import com.miui.video.model.MediaUrlForPlayerUtil;
import com.miui.video.model.MediaUrlForPlayerUtil.PlayUrlObserver;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.DuoKanCodecConstants;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.airkan.RemoteTVMediaPlayerControl;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.popup.VpCtrlAirKanDevicesPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlAirKanDevicesPopupWindowV5;
import com.miui.videoplayer.framework.popup.VpCtrlBottomPopupWindow;
import com.miui.videoplayer.framework.popup.ControlListPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlMenuPopupWindow;
import com.miui.videoplayer.framework.popup.IAirKanDevicesPopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlMediaInfoPopupWindow;
import com.miui.videoplayer.framework.popup.NextCiPopupWindow;
import com.miui.videoplayer.framework.popup.PauseFullScreenPopupWindow;
import com.miui.videoplayer.framework.popup.PopupWindowManager;
import com.miui.videoplayer.framework.popup.ProgressTimePopupWindow;
import com.miui.videoplayer.framework.popup.VpCtrlSelectCiPopupWindow;
import com.miui.videoplayer.framework.popup.TopStatusBarPopupWindow;
import com.miui.videoplayer.framework.popup.TopStatusBarPopupWindowV5;
import com.miui.videoplayer.framework.popup.VpCtrlSelectSourcePopupWindow;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.ui.DuoKanMediaController.ScreenOrientationListener;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.ui.MediaPlayerControl;
import com.miui.videoplayer.framework.utils.DKTimeFormatter;

public class OriginMediaController extends FrameLayout implements ScreenOrientationListener {
	private static final String TAG = OriginMediaController.class.getSimpleName();
	
	private MediaPlayerControl mPlayer;
	private Context mContext;
	private View mAnchor;

	private View mRoot;
	private SeekBar mProgress;
	private TextView mEndTime, mCurrentTime;
	private boolean mShowing;
	private boolean mDragging;

	private boolean mNextEpisodePreloaded = false;
	
	private static final int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int FADE_OUT_PROGRESS_TIME = 3;
	private static final int FFWD = 4;
	private static final int REW = 5;
	private static final int GET_PROGRESS_VALUE = 6;
	private static final int SHOW_FULL_SCREEN_PAUSE = 7;
	private static final int START_AIRKAN = 8;
	public static final int SHOW_SOURCE_SELECT = 9;
	public static final int SHOW_CI_SELECT = 10;
	public static final int NEXT_CI = 11;
	public static final int SELECT_CLARITY = 12;
	public static final int SELECT_CI = 13;
	public static final int DOWNLOAD_CI = 14;
	public static final int MENU_FUNCTION = 15;
	
	private static final int FFWD_REW_DELAY_TIME = 200;
	private static final int FFWD_REW_DUOKAN_CODEC_DELAY_TIME = 100;

//	private static ExecutorService executorService = Executors.newCachedThreadPool();
//	private static SparseArray<AsyncLoadUriTask> mAsyncLoadUriTaskArray = new SparseArray<OriginMediaController.AsyncLoadUriTask>();
	private AsyncLoadEpUriTask mAsyncLoadEpUriTask = null;
	
	private ImageView mPauseButton;
	private ImageView mNextButton;
	private ImageView mPrevButton;

	private VpCtrlBottomPopupWindow mBottomControllerPopupWindow;
	private TopStatusBarPopupWindow mTopStatusBarPopupWindow;
	private VpCtrlMenuPopupWindow mCtrlMenuPopupWindow;
	private NextCiPopupWindow mNextCiPopupWindow;
	private VpCtrlSelectSourcePopupWindow mSelectSourcePopupWindow;
	private VpCtrlSelectCiPopupWindow mSelectCiPopupWindow;

	private IAirKanDevicesPopupWindow mAirkanDevicePopupWindow;
	private PauseFullScreenPopupWindow mFullScreenPopupWindow;
	private ControlListPopupWindow mControlListPopupWindow;
	private ProgressTimePopupWindow mProgressTimePopupWindow;

	private ImageView mControlListButton;

	private AirkanManager mAirKanManager;
	private String[] mUriArray;
	private int mPlayingIndex;

	private DuoKanMediaController mDuoKanMediaController;
	private LocalMediaPlayerControl mLocalMediaPlayer;
	
	private MediaUrlForPlayerUtil mMediaUrlForPlayerUtil = null;
	private MediaUrlInfo mMediaUrlInfo;
	
	protected DuoKanMediaController getDuoKanMediaController() {
		return mDuoKanMediaController;
	}
	private Map<String, PlayHistoryEntry> mUriTitleMap;

	
	public OriginMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = this;
		mContext = context;
	}
	
	public VpCtrlMenuPopupWindow getCtrlMenuPopupWindow() {
		return mCtrlMenuPopupWindow;
	}
	
	public void setLocalMediaPlayerControl(LocalMediaPlayerControl localMediaPlayerControl) {
		this.mLocalMediaPlayer = localMediaPlayerControl;
	}

	@Override
	public void onFinishInflate() {
		if (mRoot != null) {
			initControllerView(mRoot);
		}
	}

	public OriginMediaController(Context context, boolean useFastForward) {
		super(context);
		mContext = context;
		initFloatingWindow();
	}

	public OriginMediaController(Context context) {
		this(context, true);
	}

	private void initFloatingWindow() {
		mBottomControllerPopupWindow = new VpCtrlBottomPopupWindow(mContext, this);
		if (DuoKanConstants.ENABLE_V5_UI) {
			mTopStatusBarPopupWindow = new TopStatusBarPopupWindowV5(mContext);
//			mBottomControllerPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
//			int bottomControllerHeightV5 = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_height_v5);
//			mBottomControllerPopupWindow.setHeight(bottomControllerHeightV5);
//			mBottomControllerPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.vp_lightblue));
//			mTopStatusBarPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.vp_lightblue));
//			mBottomControllerPopupWindow.setHeight(wrap);
//			mTopStatusBarPopupWindow.setHeight(462);
		} else {
			mTopStatusBarPopupWindow = new TopStatusBarPopupWindow(mContext);
		}
		mCtrlMenuPopupWindow = new VpCtrlMenuPopupWindow(mContext, mHandler);
		mNextCiPopupWindow = new NextCiPopupWindow(mContext, mHandler);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		requestFocus();
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}
	
	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 * 
	 * @param view
	 *            The view to which to anchor the controller when it is visible.
	 */
	public void setAnchorView(View view) {
		mAnchor = view;
		FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		removeAllViews();
		View v = makeControllerView();
		addView(v, frameParams);
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * 
	 * @return The controller view.
	 * @hide This doesn't work as advertised
	 */
	protected View makeControllerView() {
		LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflate.inflate(getLayoutId(), null);

		initControllerView(mRoot);

		return mRoot;
	}

	protected int getLayoutId() {
		return R.layout.vp_popup_ctrl_bottom;
	}
	
	private void initControllerView(View v) {

		mControlListButton = (ImageView) v.findViewById(R.id.control_list);
		if (mControlListButton != null) {
			mControlListButton.setOnClickListener(mControlListListener);
			if (mUriArray != null && mUriArray.length == 1) {
				mControlListButton.setVisibility(View.INVISIBLE);
			}
		}
		
		mPauseButton = (ImageView) v.findViewById(R.id.pause);
		if (mPauseButton != null) {
			mPauseButton.setOnClickListener(mPauseListener);
		}

		mNextButton = (ImageView) v.findViewById(R.id.next);
		if (mNextButton != null) {
			mNextButton.setOnLongClickListener(mNextLongClickListener);
			mNextButton.setOnTouchListener(mNextTouchClickListener);
		}
		
		mPrevButton = (ImageView) v.findViewById(R.id.prev);
		if (mPrevButton != null) {
			mPrevButton.setOnLongClickListener(mPrevLongClickListener);
			mPrevButton.setOnTouchListener(mPrevTouchClickListener);
		}
		
		mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
		if (mProgress != null) {
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
			}
			mProgress.setMax(1000);
		}

		mEndTime = (TextView) v.findViewById(R.id.time);
		mCurrentTime = (TextView) v.findViewById(R.id.time_current);
	}

	/**
	 * Show the controller on screen. It will go away automatically after 3
	 * seconds of inactivity.
	 */
	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		 Log.i(TAG, "show (timeout): " + timeout);
//		 Log.e("isAirkan show ", !mAirKanManager.isPlayingInLocal() + "");
		if (timeout == -1) {
			timeout = 0;
			mHandler.removeMessages(FADE_OUT);
		}
		if (!mAirKanManager.isPlayingInLocal()) {
			timeout = 0;
			mHandler.removeMessages(FADE_OUT);
		}
		if (!mShowing && mAnchor != null) {
			if (mSelectCiPopupWindow != null && mSelectCiPopupWindow.isShowing()) {
				mSelectCiPopupWindow.dismiss();
				return;
			}
			
			setProgress();
			if (mPauseButton != null) {
				mPauseButton.requestFocus();
			}
			String mediaTitle = null;
			int mediaCi = -1;
			PlayHistoryEntry entry = getMediaTitle(mUriArray[mPlayingIndex]);
			if (entry != null) {
				mediaTitle = entry.getVideoName();
				if (entry.getMediaCi() != null) {
					try {
						mediaCi = Integer.parseInt(entry.getMediaCi());
					} catch (NumberFormatException e) {
						//ignore
					}
				}
			}
			//
			mCtrlMenuPopupWindow.show(mAnchor);
//			mNextCiPopupWindow.show(mAnchor);
			if (mTopStatusBarPopupWindow instanceof TopStatusBarPopupWindowV5) {
				TopStatusBarPopupWindowV5 topStatusPopupWindows = (TopStatusBarPopupWindowV5) mTopStatusBarPopupWindow;
				topStatusPopupWindows.show(mAnchor, Uri.parse(mUriArray[mPlayingIndex]), mediaTitle, mediaCi);
			} else {
				mTopStatusBarPopupWindow.show(mAnchor, Uri.parse(mUriArray[mPlayingIndex]), mediaTitle);
			}
			mBottomControllerPopupWindow.show(mAnchor);
			mShowing = true;
		}
		updatePausePlay();

		// cause the progress bar to be updated even if mShowing
		// was already true. This happens, for example, if we're
		// paused with the progress bar showing the user hits play.
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public PlayHistoryEntry getMediaTitle(String uriString) {
//		Log.i(TAG, "Uri Title map : " + mUriTitleMap);
		if (mUriTitleMap != null) {
			return mUriTitleMap.get(uriString);
		}
		return null;
	}

	public boolean isShowing() {
		return mShowing;
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		Log.i(TAG, "hide");
		if (mAnchor == null)
			return;
		
	    mDuoKanMediaController.hideStatusBar();
		if (mShowing) {
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
			} catch (IllegalArgumentException ex) {
				Log.w("MediaController", "already removed");
			}
			try {
				mBottomControllerPopupWindow.dismiss();
				mTopStatusBarPopupWindow.dismiss();
				if (mCtrlMenuPopupWindow != null && mCtrlMenuPopupWindow.isShowing()) {
					mCtrlMenuPopupWindow.dismiss();
				}
//				if (mNextCiPopupWindow != null && mNextCiPopupWindow.isShowing()) {
//					mNextCiPopupWindow.dismiss();				
//				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			mShowing = false;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				 Log.i(TAG, "hide progress");
				mHandler.removeMessages(GET_PROGRESS_VALUE);
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
//				 Log.i(TAG, "show progress:" + "  Player isplaying: " + mPlayer.isPlaying() + "  mDragging:"
//				 + mDragging + "  mshowing:" + mShowing + " pos: " + pos);
				if (!mDragging && mShowing && mPlayer.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			case FADE_OUT_PROGRESS_TIME:
				if (mProgressTimePopupWindow != null && mProgressTimePopupWindow.isShowing()) {
					mProgressTimePopupWindow.dismiss();
				}
				break;
			case FFWD:
				mPlayer.pause();
				if (mAirKanManager.isPlayingInLocal() && DuoKanCodecConstants.sUseDuokanCodec) {
					if (mPlayer instanceof LocalMediaPlayerControl) {
						((LocalMediaPlayerControl) mPlayer).enableMultiSpeedPlayback(1000 / FFWD_REW_DUOKAN_CODEC_DELAY_TIME, true);
						msg = obtainMessage(GET_PROGRESS_VALUE);
						sendMessageDelayed(msg, FFWD_REW_DUOKAN_CODEC_DELAY_TIME);
					}
				} else {
					ffwd();
					mHandler.sendEmptyMessageDelayed(FFWD, FFWD_REW_DELAY_TIME);
				}
				break;
			case REW:
				mPlayer.pause();
				if (mAirKanManager.isPlayingInLocal() && DuoKanCodecConstants.sUseDuokanCodec) {
					if (mPlayer instanceof LocalMediaPlayerControl) {
						((LocalMediaPlayerControl) mPlayer).enableMultiSpeedPlayback(1000 / FFWD_REW_DUOKAN_CODEC_DELAY_TIME, false);
						msg = obtainMessage(GET_PROGRESS_VALUE);
						sendMessageDelayed(msg, FFWD_REW_DUOKAN_CODEC_DELAY_TIME);
					}
				} else {
					rew();
					mHandler.sendEmptyMessageDelayed(REW, FFWD_REW_DELAY_TIME);
				}
				break;
			case GET_PROGRESS_VALUE:
				setProgress();
				msg = obtainMessage(GET_PROGRESS_VALUE);
				if (mPrevButtonLongPressed || mNextButtonLongPressed) {
					sendMessageDelayed(msg, FFWD_REW_DUOKAN_CODEC_DELAY_TIME);
				}
				break;
			case SHOW_FULL_SCREEN_PAUSE:
				showPauseFullScreenPopupWindow();
				break;
			case START_AIRKAN:
				startAirkan((List<String>) msg.obj);
				break;
			case SHOW_SOURCE_SELECT:
				showSelectSourcePopupWindow();
				break;
			case SHOW_CI_SELECT:
				showSelectCiPopupWindow();
				break;
			case NEXT_CI:
				mHandler.removeMessages(NEXT_CI);
				playNextEpisode();
				break;
			case SELECT_CLARITY:
				MediaUrlInfo mediaUrlInfo = (MediaUrlInfo) msg.obj;
				switchClarity(mediaUrlInfo);
				break;
			case SELECT_CI:
				switchMedia(msg.arg1);
				break;
			case DOWNLOAD_CI:
				sendOfflineBroadcast(msg.arg1, msg.arg2);
				break;
			case MENU_FUNCTION:
				hide();	
				mDuoKanMediaController.onMenuClick();
				break;
			}
		}
	};
	
	private void showSelectSourcePopupWindow() {
		hide();
		if (mSelectSourcePopupWindow == null) {
			mSelectSourcePopupWindow = new VpCtrlSelectSourcePopupWindow(mContext, mHandler);
			mSelectSourcePopupWindow.setLocalMediaPlayerControl(mLocalMediaPlayer);
		}
		if (!mSelectSourcePopupWindow.isShowing()) {
			mSelectSourcePopupWindow.show(mAnchor);
		}
	}
	
	private void showSelectCiPopupWindow() {
		hide();		
		if (mSelectCiPopupWindow == null) {
			mSelectCiPopupWindow = new VpCtrlSelectCiPopupWindow(mContext, mHandler);
			mSelectCiPopupWindow.setLocalMediaPlayerControl(mLocalMediaPlayer);
		}
		if (!mSelectCiPopupWindow.isShowing()) {
			mSelectCiPopupWindow.show(mAnchor);
		}
		/*
		 * TestPopupWindow test = new TestPopupWindow(mContext);
		test.show(mAnchor);*/
	}
	
	private void startAirkan(List<String> deviceNameList) {
		if (mAirkanDevicePopupWindow == null) {				
			if (DuoKanConstants.ENABLE_V5_UI) {
				mAirkanDevicePopupWindow = new VpCtrlAirKanDevicesPopupWindowV5(mContext, mAirKanManager);
			} else {
				mAirkanDevicePopupWindow = new VpCtrlAirKanDevicesPopupWindow(mContext, mAirKanManager);
			}
		}
		mAirkanDevicePopupWindow.setVideoUri(Uri.parse(mUriArray[mPlayingIndex]));
		if (!mAirkanDevicePopupWindow.isShowing()) {
			mAirkanDevicePopupWindow.show(mAnchor, mPlayer);
		}
		
		if (DuoKanConstants.ENABLE_V5_UI) {
			mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
		}
		boolean tipOff = true;
		if (mContext instanceof Activity) {
			Activity activity = (Activity) mContext;
			SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_AIREKAN_USER_USED, tipOff);
			editor.commit();
		}
		
		mHandler.sendEmptyMessage(FADE_OUT);
	}
	
	private int setProgress() {
		// Log.e("begin progress!!!!!!!!!!!", "mPlayer: " + mPlayer +
		// "mDragging:" + mDragging);
		if (mPlayer == null || mDragging) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mPlayer instanceof RemoteTVMediaPlayerControl) {
//			Log.e(TAG, "postion : " + mPlayer.getCurrentPosition() + " duration: " + mPlayer.getDuration());
			updatePausePlay();
		}
		// Log.e("progress:", "mPlayer: " + mPlayer +" position:" +
		// mPlayer.getCurrentPosition() + "duration:" + mPlayer.getDuration());
//		Log.e(TAG, "setProgress" + " position: " + mPlayer.getCurrentPosition() + " duration:" + mPlayer.getDuration() + "isPlaying: " + mPlayer.isPlaying());
//		if (mPlayer instanceof RemoteTVMediaPlayerControl && duration == -1) {
//			setProgressBarAndPauseButtonEnable(false);
//			return 0;
//		} else {
//			setProgressBarAndPauseButtonEnable(true);
//		}
		
		if (mPlayer instanceof RemoteTVMediaPlayerControl && duration == -1) {
			return 0;
		}

		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
		}
		if (duration == 0) {
			return position;
		}
		if (mEndTime != null)
			mEndTime.setText(DKTimeFormatter.getInstance().stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(DKTimeFormatter.getInstance().stringForTime(position));
		// Log.e("set progress!!!!!!!!!!!", "progress: " + position);

		return position;
	}
/*
	private void setProgressBarAndPauseButtonEnable(boolean enabled) {
		if (mProgress != null && (mProgress.isEnabled() != enabled)) {
			mProgress.setEnabled(enabled);
		} 
		if (mPauseButton != null && (mPauseButton.isEnabled()) != enabled) {
			mPauseButton.setEnabled(enabled);
		}
	}
*/	
	private boolean mNextButtonLongPressed = false;
	private View.OnTouchListener mNextTouchClickListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mNextButtonLongPressed) {
					mNextButtonLongPressed = false;
					mPlayer.start();
					mHandler.removeMessages(FFWD);
					if (mAirKanManager.isPlayingInLocal()) {
						Message msg = mHandler.obtainMessage(FADE_OUT);
						mHandler.removeMessages(FADE_OUT);
						mHandler.sendMessageDelayed(msg, sDefaultTimeout);
					}

				} else if (mUriArray.length != 1) {
					switchMedia(mPlayingIndex + 1);
				}
			}

			return false;
		}
	};

	private View.OnLongClickListener mNextLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (!mAirKanManager.isPlayingInLocal()) {
				return false;
			}
			mNextButtonLongPressed = true;
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendEmptyMessage(FFWD);
			return true;
		}

	};

	private boolean mPrevButtonLongPressed = false;
	private View.OnTouchListener mPrevTouchClickListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mPrevButtonLongPressed) {
					mPrevButtonLongPressed = false;
					mPlayer.start();
					mHandler.removeMessages(REW);
					if (mAirKanManager.isPlayingInLocal()) {
						Message msg = mHandler.obtainMessage(FADE_OUT);
						mHandler.removeMessages(FADE_OUT);
						mHandler.sendMessageDelayed(msg, sDefaultTimeout);
					}
				} else if (mUriArray.length != 1) {
					switchMedia(mPlayingIndex - 1);
				}
			}

			return false;
		}
	};

	private View.OnLongClickListener mPrevLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (!mAirKanManager.isPlayingInLocal()) {
				return false;
			}
			mPrevButtonLongPressed = true;
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendEmptyMessage(REW);
			return true;
		}

	};

	private void showLoading() {
		PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
		mPlayer.pause();
	}
	
	public boolean switchMedia(int index) {
		if (index == VideoPlayerActivity.curMediaIndex) {
			return false;
		}
		int length = mUriArray.length;
		if (index < 0 || index >= length) {
            if (VideoPlayerActivity.isScreenSaver) {
			    index = 0;
            } else {
                return false;
            }
		}
		
		int nextCi = VideoPlayerActivity.getCi(index);
		mDuoKanMediaController.showLoadingView(nextCi);
		if (Util.isEmpty(mUriArray[index])) {
			loadEpisodeUri(index, true);
		} else {
			VideoPlayerActivity.nextMediaIndex = index;
			VideoPlayerActivity.nextCi = nextCi;
			playNextEp();
		}		
		mNextEpisodePreloaded = false;
		showLoading();
		return true;
	}
	
    private void loadEpisodeUri(int index, boolean isPlay) {
		Log.i(TAG, "loadEpisodeUri");
		if (mAsyncLoadEpUriTask != null) {
			mAsyncLoadEpUriTask.cancel(true);
		}
		mAsyncLoadEpUriTask = new AsyncLoadEpUriTask(index, isPlay);
		mAsyncLoadEpUriTask.execute();
    }
    
	private void switchClarity(MediaUrlInfo mediaUrlInfo) {
		if(mediaUrlInfo == null) {
			return;
		}
		
		this.mMediaUrlInfo = mediaUrlInfo;
		if(mediaUrlInfo.isHtml()) {
			if(!Util.isEmpty(mediaUrlInfo.mediaUrl)) {
				mDuoKanMediaController.showLoadingView(VideoPlayerActivity.curCi);
				showLoading();
				loadClarityUri(mediaUrlInfo.mediaUrl);
			}
		} else {
			VideoPlayerActivity.curMediaSource = mediaUrlInfo.mediaSource;
			VideoPlayerActivity.curClarity = mediaUrlInfo.clarity;
			mUriArray[VideoPlayerActivity.curMediaIndex] = mediaUrlInfo.mediaUrl;
			playCurEp();
		}
	}
	
	private void loadClarityUri(String mediaUrl) {
		if (mMediaUrlForPlayerUtil == null) {
			mMediaUrlForPlayerUtil = new MediaUrlForPlayerUtil(mContext);
			mMediaUrlForPlayerUtil.setObserver(mClarityPlayUrlObserver);
		}
		mMediaUrlForPlayerUtil.getMediaUrlForPlayer(mediaUrl);
	}
    
	private void playNextEp() {
		VideoPlayerActivity.curMediaIndex = VideoPlayerActivity.nextMediaIndex;
		VideoPlayerActivity.curCi = VideoPlayerActivity.nextCi;
		playCurEp();
	}
	
	private void playCurEp() {
		mPlayingIndex = VideoPlayerActivity.curMediaIndex;
		String targetUri = mUriArray[mPlayingIndex];
		String mediaTitle = VideoPlayerActivity.mediaTitle;
		if (VideoPlayerActivity.curCi > 0) {
			Log.i(TAG, "media title :" + mediaTitle);
			PlayHistoryEntry entry = new PlayHistoryEntry(targetUri);
			entry.setVideoName(mediaTitle);
			entry.setPosition(0);
			mUriTitleMap.put(targetUri.toString(), entry);
			mDuoKanMediaController.setUri(Uri.parse(targetUri));
			Log.i(TAG, "put url: " + targetUri + " put html5: " + VideoPlayerActivity.curMediaHtml5Url);
		}

		// Log.e("mPlayer: " , mPlayer + "");
		if (mPlayer instanceof LocalMediaPlayerControl) {
			LocalMediaPlayerControl localMediaPlayerControl = (LocalMediaPlayerControl) mPlayer;
			int position = localMediaPlayerControl.getCurrentPosition();
			Log.i(TAG, "last position : " + position);
			//if (position != 0) {
				//recordLastPosition(position);
			//}
			localMediaPlayerControl.stopLocalPlayForMediaSwitch();
			localMediaPlayerControl.startLocalPlayForMediaSwitch(Uri.parse(targetUri));

			if (mContext instanceof Activity) {
				Activity activity = (Activity) mContext;
				if (!VpCtrlMediaInfoPopupWindow.isNull() && VpCtrlMediaInfoPopupWindow.getInstance(activity).isShowing()) {
					VpCtrlMediaInfoPopupWindow.getInstance(activity).dismiss();
					VpCtrlMediaInfoPopupWindow.getInstance(activity).updateValues(localMediaPlayerControl.getMediaInfo());
					VpCtrlMediaInfoPopupWindow.getInstance(activity).show(mAnchor);
				}
			}
			mDuoKanMediaController.checkNetwork(Uri.parse(targetUri));
//			mDuoKanMediaController.checkValidMedia(Uri.parse(targetUri));
		}
		if (mPlayer instanceof RemoteTVMediaPlayerControl) {
			RemoteTVMediaPlayerControl remoteTVMediaController = (RemoteTVMediaPlayerControl) mPlayer;
			remoteTVMediaController.setVideoURI(targetUri, "", 0);
			remoteTVMediaController.playTo(remoteTVMediaController.getDeviceName());
		}
		mDuoKanMediaController.setUri(Uri.parse(targetUri));
		if (mControlListPopupWindow != null) {
			mControlListPopupWindow.setPlayingIndex(mPlayingIndex);
		}

		if (mTopStatusBarPopupWindow != null && mTopStatusBarPopupWindow.isShowing()) {
			mTopStatusBarPopupWindow.updateMediaName(Uri.parse(targetUri));
		}
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {/*
//			Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$", "click!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
			doPauseResume();
			if (mAirKanManager.isPlayingInLocal()) {
				if (mPlayer.isPlaying()) {
					if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
						mFullScreenPopupWindow.dismiss();
					}
				} else {
					if (DuoKanConstants.ENABLE_V5_UI) {
						Message msg = mHandler.obtainMessage(FADE_OUT);
						mHandler.removeMessages(FADE_OUT);
						mHandler.sendEmptyMessageDelayed(SHOW_FULL_SCREEN_PAUSE, 3000);
						mHandler.sendMessageDelayed(msg, sDefaultTimeout);
					} else {
						showPauseFullScreenPopupWindow();
					}
				}
			}  else {
				setUpdateProgressEnable(true);
			}
		*/
			pauseClickListener();
		}
	};
	
	public void pauseClickListener(){

//		Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$", "click!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
		doPauseResume();
		if (mAirKanManager.isPlayingInLocal()) {
			if (mPlayer.isPlaying()) {
				if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
					mFullScreenPopupWindow.dismiss();
				}
			}
		}  else {
			setUpdateProgressEnable(true);
		}
	}
    
	public void showPauseFullScreenPopupWindow() {
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new PauseFullScreenPopupWindow(mContext);
			mFullScreenPopupWindow.setDuoKanMediaController(mDuoKanMediaController);
		}	
		mFullScreenPopupWindow.setOnImageViewClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPlayer.start();
				VideoPlayerActivity.isVideoPaused = false;
				mFullScreenPopupWindow.dismiss();
			}
		});
		mFullScreenPopupWindow.show(mAnchor);
	}

	private void updatePausePlay() {
		if (mRoot == null || mPauseButton == null) {
			return;			
		}
		updatePlayingState(mPlayer.isPlaying());
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
            VideoPlayerActivity.isVideoPaused = true;
		} else {
			mPlayer.start();
            VideoPlayerActivity.isVideoPaused = false;
		}
		updatePausePlay();
	}

	// There are two scenarios that can trigger the seekbar listener to trigger:
	//
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);
             
			
			//Log.i("test", "start to seek !");
			mPlayer.pause();
			updatePausePlay();
			mDragging = true;

			// By removing these pending progress messages we make sure
			// that a) we won't update the progress while the user adjusts
			// the seekbar and b) once the user is done dragging the thumb
			// we will post one of these messages to the queue again and
			// this ensures that there will be exactly one message queued up.
			mHandler.removeMessages(SHOW_PROGRESS);
			mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}

			long currentPosition = mPlayer.getCurrentPosition();
			long duration = mPlayer.getDuration();
			long newposition = (duration * progress) / 1000L;
			// Log.e("currentPosition", currentPosition + "");
			// Log.e("duration", duration + "");
			// Log.e("newposition", newposition + "");
			mPlayer.seekTo((int) newposition);
			if (mCurrentTime != null)
				mCurrentTime.setText(DKTimeFormatter.getInstance().stringForTime((int) newposition));

			// custom
			if (mProgressTimePopupWindow == null) {
				mProgressTimePopupWindow = new ProgressTimePopupWindow(mContext, true);
				mProgressTimePopupWindow.setTopStatusBarPopupWindow(mTopStatusBarPopupWindow);
				mProgressTimePopupWindow.setCtrlMenuPopupWindow(mCtrlMenuPopupWindow);
			}
			if (!mProgressTimePopupWindow.isShowing() && mAirKanManager.isPlayingInLocal()) {
				int marginTop = (int) mContext.getResources().getDimension(R.dimen.popup_center_progress_time_margin_top_small);
				mProgressTimePopupWindow.showAtLocation(mAnchor, Gravity.TOP, 0, marginTop);
			}
			mProgressTimePopupWindow.setOrientation(newposition > currentPosition);
			mProgressTimePopupWindow.updatePosition((int) newposition);

			mHandler.removeMessages(FADE_OUT_PROGRESS_TIME);
			mHandler.sendEmptyMessageDelayed(FADE_OUT_PROGRESS_TIME, 1000);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			
			//Log.i("test", "stop to seek!");
			mPlayer.start();
			mDragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);

			// Ensure that progress is properly updated in the future,
			// the call to show() does not guarantee this because it is a
			// no-op if we are already showing.
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	private void ffwd() {
		// Log.i("ffwd: ", "ffwd:" + (System.currentTimeMillis() - ct));
		int pos = mPlayer.getCurrentPosition();
		pos += FFWD_REW_DELAY_TIME * 5; // milliseconds
		mPlayer.seekTo(pos);
		setProgress();
	}

	private void rew() {
		int pos = mPlayer.getCurrentPosition();
		pos -= FFWD_REW_DELAY_TIME * 5; // milliseconds
		mPlayer.seekTo(pos);
		setProgress();
	}

	private View.OnClickListener mControlListListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mControlListPopupWindow == null) {
				mControlListPopupWindow = new ControlListPopupWindow(mContext, mUriArray, OriginMediaController.this);
				mControlListPopupWindow.setPlayingIndex(mPlayingIndex);
			}
			mControlListPopupWindow.show(mAnchor, mPlayer);
		}
	};
	
	public void setAirKanManager(AirkanManager airKanManager) {
		this.mAirKanManager = airKanManager;
		mCtrlMenuPopupWindow.setAirKanManager(airKanManager);
	}

	public void setUpdateProgressEnable(boolean enable) {
		if (enable) {
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		} else {
			mHandler.removeMessages(SHOW_PROGRESS);
		}
	}

	public void changeToAirkanSize() {
		Drawable thumbDrawable = mContext.getResources().getDrawable(R.drawable.vp_seekbar_thumb_big);
		mProgress.setThumb(thumbDrawable);

		mControlListButton.setImageResource(R.drawable.vp_control_list_big_imageview);
		
		
		int controlListButtonLeftMargin = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_controllist_big_imageview_margin_left);
		((android.widget.RelativeLayout.LayoutParams) mControlListButton.getLayoutParams()).leftMargin = controlListButtonLeftMargin;
		
		int centerButtonLeftMargin = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_center_big_imageview_margin_left);
		mPauseButton.setImageResource(R.drawable.vp_control_pause_big_imageview);
		mNextButton.setImageResource(R.drawable.vp_control_next_big_imageview);
		mPrevButton.setImageResource(R.drawable.vp_control_previous_big_imageview);
		((android.widget.LinearLayout.LayoutParams) mPauseButton.getLayoutParams()).leftMargin = centerButtonLeftMargin;
		((android.widget.LinearLayout.LayoutParams) mNextButton.getLayoutParams()).leftMargin = centerButtonLeftMargin;
		
		//
		mBottomControllerPopupWindow.dismiss();
		mBottomControllerPopupWindow.updateHeight(true);
		mBottomControllerPopupWindow.show(mAnchor);

//		mControlListButton.getParent().requestLayout();
		// this.requestLayout();
	}

	public void changeToLocalPlaySize() {
		Drawable thumbDrawable = mContext.getResources().getDrawable(R.drawable.vp_seekbar_thumb);
		mProgress.setThumb(thumbDrawable);
		
		int controlListButtonLeftMargin = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_controllist_imageview_margin_left);
		mControlListButton.setImageResource(R.drawable.vp_control_list_imageview);
		((android.widget.RelativeLayout.LayoutParams) mControlListButton.getLayoutParams()).leftMargin = controlListButtonLeftMargin;
//		lp.leftMargin = controlListButtonLeftMargin;
		
		int centerButtonLeftMargin = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_center_imageview_margin_left);
		mPauseButton.setImageResource(R.drawable.vp_control_pause_imageview);
		mNextButton.setImageResource(R.drawable.vp_control_next_imageview);
		mPrevButton.setImageResource(R.drawable.vp_control_previous_imageview);
		((android.widget.LinearLayout.LayoutParams) mPauseButton.getLayoutParams()).leftMargin = centerButtonLeftMargin;
		((android.widget.LinearLayout.LayoutParams) mNextButton.getLayoutParams()).leftMargin = centerButtonLeftMargin;
		
		
		mBottomControllerPopupWindow.dismiss();
		mBottomControllerPopupWindow.updateHeight(false);
		mBottomControllerPopupWindow.show(mAnchor);
	}

	public void setUriArray(String[] uriArray, int playingIndex) {
		this.mUriArray = uriArray;
		this.mPlayingIndex = playingIndex;

		if (mUriArray != null && mUriArray.length == 1) {
			mControlListButton.setVisibility(View.INVISIBLE);
		}
	}

	public View getAnchor() {
		return mAnchor;
	}

	public void playNextMedia() {
		switchMedia(mPlayingIndex + 1);
	}

	public void playPreviousMedia() {
		switchMedia(mPlayingIndex - 1);
	}
	
    public void preloadNextEpisodeUri() {
		long currentPosition = mPlayer.getCurrentPosition();
		long duration = mPlayer.getDuration();
		if (!mNextEpisodePreloaded && (currentPosition >= 0 && (currentPosition <= duration) &&
				(((duration - currentPosition) < 600000) || (((currentPosition * 100) / duration) > 50)))) {
			if (mAsyncLoadEpUriTask == null) {
				loadEpisodeUri(VideoPlayerActivity.curMediaIndex + 1, false);
			}
		}
    }
    
	public boolean playNextEpisode() {
		return switchMedia(mPlayingIndex + 1);
	}

	public String getPlayingUri() {
		Log.i(TAG, "mPlayingIndex:" + mPlayingIndex);
		return mUriArray[mPlayingIndex];
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mDuoKanMediaController != null) {
			mDuoKanMediaController.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setDuoKanMediaController(DuoKanMediaController duoKanMediaController) {
		this.mDuoKanMediaController = duoKanMediaController;
	}

	public ControlListPopupWindow getControlListPopupWindow() {
		return mControlListPopupWindow;
	}

	@Override
	public void onScreenOrientationChanged(int orientation) {
		if (mControlListPopupWindow != null && mControlListPopupWindow.isShowing()) {
			mControlListPopupWindow.dismiss();
			mControlListPopupWindow.show(mAnchor, mPlayer);
		}
		if (!mAirKanManager.isPlayingInLocal()) {
			this.changeToAirkanSize();
			this.setUpdateProgressEnable(true);
			if (mAirkanDevicePopupWindow != null && mAirkanDevicePopupWindow.isShowing()) {
				mAirkanDevicePopupWindow.dismiss();
				mAirkanDevicePopupWindow.show(mAnchor, mPlayer);
			}
		}
		if (mAirkanDevicePopupWindow instanceof VpCtrlAirKanDevicesPopupWindowV5 && mAirkanDevicePopupWindow.isShowing()) {
			mAirkanDevicePopupWindow.dismiss();
			mAirkanDevicePopupWindow.show(mAnchor, mPlayer);
		}
		
		if (mProgressTimePopupWindow != null && mProgressTimePopupWindow.isShowing()) {
			mProgressTimePopupWindow.dismiss();
		}
		//Log.i(TAG, "progress time popup window:"+mProgressTimePopupWindow);
		mProgressTimePopupWindow = null;
		if (mTopStatusBarPopupWindow != null && mTopStatusBarPopupWindow.isShowing()) {
			mTopStatusBarPopupWindow.dismiss();
		}
		//Log.i(TAG, "progress time popup window:"+mProgressTimePopupWindow);

		if (mCtrlMenuPopupWindow != null && mCtrlMenuPopupWindow.isShowing()) {
			mCtrlMenuPopupWindow.dismiss();
		}

//		if (mNextCiPopupWindow != null && mNextCiPopupWindow.isShowing()) {
//			mNextCiPopupWindow.dismiss();
//		}
		
		if (mSelectCiPopupWindow != null && mSelectCiPopupWindow.isShowing()) {
			mSelectCiPopupWindow.dismiss();
		}
		
		if (mSelectSourcePopupWindow != null && mSelectSourcePopupWindow.isShowing()) {
			mSelectSourcePopupWindow.dismiss();
		}
		
		if (DuoKanConstants.ENABLE_V5_UI) {
			mTopStatusBarPopupWindow = new TopStatusBarPopupWindowV5(mContext);
		} else {
			mTopStatusBarPopupWindow = new TopStatusBarPopupWindow(mContext);
		}
	}

	public void updatePlayingState(boolean playing) {
		if (mAirKanManager.isPlayingInLocal()) {
			if (playing) {
//				mPauseButton.setImageResource(R.drawable.vp_control_pause_imageview);
				mPauseButton.setImageResource(getPauseImageResId());
			} else {
//				mPauseButton.setImageResource(R.drawable.vp_control_play_imageview);
				mPauseButton.setImageResource(getPlayImageResId());
			}
		} else {
			if (playing) {
				mPauseButton.setImageResource(getBigPauseImageResId());
			} else {
				mPauseButton.setImageResource(getBigPlayImageResId());
			}
		}
	}

	protected int getBigPauseImageResId() {
		return R.drawable.vp_control_pause_big_imageview;
	}
	
	protected int getBigPlayImageResId() {
		return R.drawable.vp_control_play_big_imageview;
	}
	
	protected int getPauseImageResId() {
		return R.drawable.vp_control_pause_imageview;
	}
	
	protected int getPlayImageResId() {
		return R.drawable.vp_control_play_imageview;
	}
	
	
	public void hideFullScreenPausePopup() {
		if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
			mFullScreenPopupWindow.dismiss();
		}
	}

	public void setTitleMap(Map<String, PlayHistoryEntry> map) {
		mUriTitleMap = map;
	}

	public TopStatusBarPopupWindow getTopStatusBarPopupWindow() {
		return mTopStatusBarPopupWindow;
	}
     
	public PauseFullScreenPopupWindow getPauseFullScreenPopupWindow(){
		return mFullScreenPopupWindow;
	}
	public void onActivityDestroy() {
		mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
	}

	public void removeShowFullScreenPauseMessage() {
		mHandler.removeMessages(SHOW_FULL_SCREEN_PAUSE);
	}

	public void set3dMode(boolean mode) {
		mDuoKanMediaController.set3dMode(mode);
	}
	
	public boolean get3dMode() {
		return mDuoKanMediaController.get3dMode();
	}
    
    private class AsyncLoadEpUriTask extends AsyncTask<Void, Void, Void> {
    	private int index;
    	private int ci;
    	private boolean isPlay;
    	
		public AsyncLoadEpUriTask(int index, boolean isPlay) {
			Log.i(TAG, "AsyncLoadEpUriTask begin");
			this.index = index;
			this.isPlay = isPlay;
		}		
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.i(TAG, "AsyncLoadEpUriTask doInBackground");
			ci = VideoPlayerActivity.getCi(index);
			mUriArray[index] = null;
			VideoPlayerActivity.curMediaHtml5Url = null;
			ContentResolver cr = mContext.getContentResolver();
			Cursor cursor = null;
			try {
				String contentUriStr = Constants.CONTENT_MEDIAURL_URI.toString() + "?"
									+ Constants.MEDIA_ID + "=" + VideoPlayerActivity.mediaId + "&"
									+ Constants.CURRENT_EPISODE + "=" + ci + "&"
									+ Constants.MEDIA_SOURCE + "=" + VideoPlayerActivity.curMediaSource +"&"
									+ Constants.MEDIA_CLARITY + "=" + VideoPlayerActivity.curClarity;
				Uri contentUri = Uri.parse(contentUriStr);				
				Log.i(TAG, contentUri.toString());				
				cursor = cr.query(contentUri, null, Constants.MEDIA_ID + "=? and " + Constants.CURRENT_EPISODE + "=?",
						new String[]{String.valueOf(VideoPlayerActivity.mediaId), String.valueOf(ci)}, null);

				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					mUriArray[index]  = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_URL));
					VideoPlayerActivity.curMediaHtml5Url = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_HTML5_URL));
					Log.i(TAG, "url: " + mUriArray[index] + " Html5Url: " + VideoPlayerActivity.curMediaHtml5Url);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					try {
						cursor.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.i(TAG, "AsyncLoadEpUriTask onPostExecute");
			if (Util.isEmpty(mUriArray[index])) {
				Log.i(TAG, "fail to get episode uri!!!!");
				if(isPlay) {
					showGeturlFailDialog();
				}
			} else {
				VideoPlayerActivity.nextMediaIndex = index;
				VideoPlayerActivity.nextCi = ci;
				if (isPlay) {
					playNextEp();
					mNextEpisodePreloaded = false;
				} else {
					mNextEpisodePreloaded = true;					
				}
			}
		}
    }
    
    private void showGeturlFailDialog() {
    	AlertDialog alertDialog = new AlertDialog.Builder(mContext)
    	.setTitle(R.string.vp_VideoView_error_title)
    	.setMessage(R.string.vp_get_url_error)
    	.setCancelable(false)
    	.setPositiveButton(R.string.vp_exit, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			mDuoKanMediaController.exitPlayer();
            }
        })
        .setNegativeButton(R.string.vp_back, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			mDuoKanMediaController.hideLoadingView();
	    			mPlayer.start();
            }
        })
        .create();
    	
    	try {
    		alertDialog.show();
		} catch (Exception e) {
		}
    }
    
    //data callback
    private PlayUrlObserver mClarityPlayUrlObserver = new PlayUrlObserver() {
		
		@Override
		public void onUrlUpdate(int mediaId, int ci, String playUrl, String html5Url) {
			Log.d(TAG, "on clarity play url update");
			if(!Util.isEmpty(playUrl)) {
				if(mMediaUrlInfo != null) {
					VideoPlayerActivity.curMediaSource = mMediaUrlInfo.mediaSource;
					VideoPlayerActivity.curClarity = mMediaUrlInfo.clarity;
				}
				mUriArray[VideoPlayerActivity.curMediaIndex] = playUrl;
				playCurEp();
			}
		}
		
		@Override
		public void onReleaseLock() {
			
		}
		
		@Override
		public void onError() {
			Log.e(TAG, "on clarity play url error");
			mDuoKanMediaController.hideLoadingView();
			mPlayer.start();
			AlertMessage.show(R.string.vp_error_get_play_url_back_to_continue);
		}
	};
    
	private void sendOfflineBroadcast(int ci, int operation) {
		Intent intent = new Intent();
		intent.setAction(Constants.OFFLINE_BROADCAST_ACTION);
		intent.putExtra(Constants.MEDIA_ID, VideoPlayerActivity.mediaId);
		intent.putExtra(Constants.INTENT_KEY_STRING_MEDIA_TITLE, VideoPlayerActivity.mediaTitle);
		intent.putExtra(Constants.OFFLINE_OPERATION, operation);
		intent.putExtra(Constants.CURRENT_EPISODE, ci);
		
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		mContext.sendBroadcast(intent);
	}
	
	public void updateViews() {
		if (mSelectCiPopupWindow != null && mSelectCiPopupWindow.isShowing()) {
			mSelectCiPopupWindow.refresh();
		} else {
			if (mCtrlMenuPopupWindow != null && mCtrlMenuPopupWindow.isShowing()) {
				mCtrlMenuPopupWindow.show(mAnchor);
			}
//			if (mNextCiPopupWindow != null && mNextCiPopupWindow.isShowing()) {
//				mNextCiPopupWindow.show(mAnchor);
//			}
		}
	}
}
