package com.miui.video.widget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.popup.BrightnessPopupWindow;
import com.miui.videoplayer.framework.popup.VolumePopupWindow;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

/**
 *@author tangfuling
 *
 */

public class MiuiVideoMediaController extends FrameLayout{

	private static final String TV_BRIGHTNESS_FILE = "tvBrightness";
	private DisplayMetrics mDisplayMetrics; 
	private float mTriggerAdjustPositionTolerance;
	private DisplayInformationFetcher mDisplayInformationFetcher;
	private float mAdjustPositionStep;
    private BrightnessPopupWindow mBrightnessPopupWindow;
	private VolumePopupWindow mVolumePopupWindow;
	private Context mContext;
	private Activity mActivity;
	private AudioManager mAudioManager;
	
	private static final int FADE_OUT_VOLUME_MESSAGE_WHAT = 0;
	private static final int FADE_OUT_BRIGHTNESS_MESSAGE_WHAT = 1;
	private static final int BRIGHTNESS_VOLUME_DISMISS_TIME = 3000;
	
	public MiuiVideoMediaController(Context context, Activity activity) {
		super(context);
		mContext = context;
		mActivity = activity;
		setupWindows(context);
	}
	
	public void DkMediaCtrlOnTouch(MotionEvent event){
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchStart(event.getX(), event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			touchMove(event.getX(), event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			touchUp(event.getX(), event.getY());
		}
	}
	
	public void DkMediaCtrlDimiss(){
		mHandler.removeMessages(FADE_OUT_VOLUME_MESSAGE_WHAT);
		mHandler.removeMessages(FADE_OUT_BRIGHTNESS_MESSAGE_WHAT);
		if( mBrightnessPopupWindow != null) {
			mBrightnessPopupWindow.dismiss();
		}
		if( mVolumePopupWindow != null){
			mVolumePopupWindow.dismiss();
		}
	}
	
	private void setupWindows(Context context) {
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setBackgroundColor(context.getResources().getColor(R.color.full_translucent));
		
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
	
	private void showBrightnessPopupWindow() {
		if (mBrightnessPopupWindow == null) {
			mBrightnessPopupWindow = new BrightnessPopupWindow(mContext);
			if (DuoKanConstants.ENABLE_V5_UI) {
				mBrightnessPopupWindow = new BrightnessPopupWindow(mContext, R.layout.vp_popup_left_vertical_seebar_group_v5);
			}
		}
	}
	private void showVolumePopupWindow() {
		if (mVolumePopupWindow == null) {
			mVolumePopupWindow = new VolumePopupWindow(mContext);
			if (DuoKanConstants.ENABLE_V5_UI) {
				mVolumePopupWindow = new VolumePopupWindow(mContext, R.layout.vp_popup_right_vertical_seekbar_group_v5);
			}
		}
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		}
		
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		mVolumePopupWindow.setMaxSeekbarValue(maxVolume);
		mVolumePopupWindow.updateSeekbarValue(currentVolume);
		if (!mVolumePopupWindow.isShowing()) {
			mVolumePopupWindow.show(this, mContext);
			mVolumePopupWindow.getSeekbar().setPressed(false);
		}
	}
	
	
	private float mX = 0;
	private float mY = 0;
	private static final float X_MIN_TOLERANCE = 20;
	private static final float X_MAX_TOLERANCE = 20;
	private static final float Y_MIN_TOLERANCE = 20;
	private static final float Y_MAX_TOLERANCE = 20;
	private boolean moved = false;

	private boolean downLeftRegion = false;
	private boolean downRightRegion = false;

	private boolean movedBrightness = false;
	private boolean movedVolume = false;
	
	private boolean mTriggerAdjustPosition = false;
	
	private void touchStart(float x, float y) {
		int screenWidth = mDisplayInformationFetcher.getScreenWidth();
		int screenHeight = mDisplayInformationFetcher.getScreenHeight();

		mX = x;
		mY = y;

		if (mX <= screenWidth / 6 && (mY > screenHeight / 6 && mY < 5 * screenHeight / 6)) {
			downLeftRegion = true;
		} else if (mX >= (5 * screenWidth) / 6 && (mY > screenHeight / 6 && mY < 5 * screenHeight / 6)) {
			downRightRegion = true;
		}
	}

	private void touchMove(float x, float y) {
		float distanceX = x - mX;
		float distanceY = y - mY;
		float dx = Math.abs(distanceX);
		float dy = Math.abs(distanceY);
		
		if (dx >= mTriggerAdjustPositionTolerance && dy <= Y_MAX_TOLERANCE && !mTriggerAdjustPosition) {
			mTriggerAdjustPosition = true;
		}
		if (mTriggerAdjustPosition && dx >= mAdjustPositionStep && dy < Y_MAX_TOLERANCE) {
			moved = true;
			mX = x;
			mY = y;
		}
		
		if (downLeftRegion && dx <= X_MAX_TOLERANCE && dy >= Y_MIN_TOLERANCE) {
			
			adjustBrightness(distanceY);
			movedBrightness = true;		
			moved = true;
			mX = x;
			mY = y;
		}

		if (downRightRegion && dx <= X_MAX_TOLERANCE && dy >= Y_MIN_TOLERANCE) {
			
			adjustVolume(distanceY);
			movedVolume = true;
			moved = true;
			mX = x;
			mY = y;
		}
	}

	private void touchUp(float x, float y) {
		if(mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing() && !downLeftRegion){
			mBrightnessPopupWindow.dismiss();
		}
		else{
			showPopupWindow();
		}
		if(mVolumePopupWindow != null && mVolumePopupWindow.isShowing() && !downRightRegion){
			mVolumePopupWindow.dismiss();
		}
		else{
			showPopupWindow();
		}
		
		moved = false;
		downLeftRegion = false;
		downRightRegion = false;
	}
	
	private void showPopupWindow(){
		if (!moved) {
			if (downLeftRegion) {
				showBrightnessPopupWindow();
				delayFadeOutBrightnessPopupWindows();
			} else if (downRightRegion) {
				showVolumePopupWindow();
				delayFadeOutVolumePopupWindow();
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
			if (mTriggerAdjustPosition) {
				mTriggerAdjustPosition = false;
			}	
		}
	}
	
	private void adjustBrightness(float distanceY) {
		int currentValue = (int)(AndroidUtils.getActivityBrightness(mActivity) * 255);
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

	public void adjustVolume(final float distanceY) {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
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
	private void delayFadeOutBrightnessPopupWindows() {
		mHandler.removeMessages(FADE_OUT_BRIGHTNESS_MESSAGE_WHAT);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_BRIGHTNESS_MESSAGE_WHAT, BRIGHTNESS_VOLUME_DISMISS_TIME);
	}
	
	public void delayFadeOutVolumePopupWindow() {
		mHandler.removeMessages(FADE_OUT_VOLUME_MESSAGE_WHAT);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_VOLUME_MESSAGE_WHAT, BRIGHTNESS_VOLUME_DISMISS_TIME);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			adjustVolume(1);
			delayFadeOutVolumePopupWindow();
			return true;
		}

		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			adjustVolume(-1);
			delayFadeOutVolumePopupWindow();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FADE_OUT_VOLUME_MESSAGE_WHAT) {
				if (mVolumePopupWindow != null && mVolumePopupWindow.isShowing()  && !mVolumePopupWindow.isAlwaysShowing()) {
					mVolumePopupWindow.dismiss();
				}
			}
			
			if (msg.what == FADE_OUT_BRIGHTNESS_MESSAGE_WHAT) {
				if (mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing() && !mBrightnessPopupWindow.isAlwaysShowing()) {
					mBrightnessPopupWindow.dismiss();
				}
			}			
		}

	};
	
	public void setBrightness(boolean bCreated, boolean bAutoBrightness) {
		SharedPreferences sp = mContext.getSharedPreferences(TV_BRIGHTNESS_FILE, Context.MODE_PRIVATE);
    	boolean bInitialSet = sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_INITIAL, true);
		if( bInitialSet) {
			Editor editor = sp.edit();
			editor.putBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_INITIAL, false);
			editor.putBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS_AUTO, AndroidUtils.isAutoAdjustBrightness(mContext));
			editor.commit();
		} else {	
			if( bAutoBrightness) {
				float curSystemBrightness = AndroidUtils.getSystemBrightness(mContext) / (float) 255;
				float tvBrightness = sp.getFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, curSystemBrightness);
				if( mBrightnessPopupWindow != null) {
					mBrightnessPopupWindow.updateSeekbarValue((int)(tvBrightness * 255 / DuoKanConstants.BRIGHTNESS_STEP ));
				}
			} else {
				float tvBrightness = 0.0f;
				if( bCreated) {
					float curSystemBrightness = AndroidUtils.getSystemBrightness(mContext) / (float) 255;
					boolean bLastAuto = sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS_AUTO, true);
					if( bLastAuto) {
						tvBrightness = AndroidUtils.getSystemBrightness(mContext) / (float) 255;
					} else {
						tvBrightness = sp.getFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, curSystemBrightness);
					}
				} else {
					tvBrightness = AndroidUtils.getSystemBrightness(mContext) / (float) 255;
				}
				
				if(mBrightnessPopupWindow != null) {
					mBrightnessPopupWindow.updateSeekbarValue((int)(tvBrightness * 255 / DuoKanConstants.BRIGHTNESS_STEP));
				}
			}
		}
	}

	public void saveBrightness() {
		SharedPreferences sp = mContext.getSharedPreferences(TV_BRIGHTNESS_FILE, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS_AUTO, AndroidUtils.isAutoAdjustBrightness(mContext));
		editor.commit();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mBrightnessPopupWindow != null && mBrightnessPopupWindow.isShowing()){
			mBrightnessPopupWindow.dismiss();
		}
		if(mVolumePopupWindow != null && mVolumePopupWindow.isShowing()){
			mVolumePopupWindow.dismiss();
		}
	}
}
