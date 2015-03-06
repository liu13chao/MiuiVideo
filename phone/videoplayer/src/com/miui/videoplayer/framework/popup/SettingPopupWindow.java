package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.miui.video.R;
import com.miui.videoplayer.adapter.SettingAdapter;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DuoKanCodecConstants;
import com.miui.videoplayer.common.DuoKanConstants;
import com.miui.videoplayer.fragment.UIConfig;
import com.miui.videoplayer.model.SoundEffect;

public class SettingPopupWindow extends BasePopupWindow {
	public static final String TAG = SettingPopupWindow.class.getName();
	private SettingAdapter mSettingAdapter;
	
	private SeekBar mAudioSeekBar, mBrightNessSeekBar;
	
	private String[] mDuokanCodecValues;
	private String[] mOriginCodecValues;
	
	private Drawable[] mDuokanCodecDrawables;
	private Drawable[] mOriginCodecDrawables;
	
	public static boolean IS_AUDIO_EFFECT_ENHANCE = true;
	public static boolean IS_FULL_SCREEN = false;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	AudioManager audioManager;
	final int maxVolume;
	
	public SettingPopupWindow(Context context, View anchor) {
		super(context, anchor, context.getResources().getDimensionPixelSize(R.dimen.vp_common_setting_popup_item_width));
		audioManager = (AudioManager) ((Activity) mContext).getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		init();
	}
	
	@Override
	public void show() {
		super.show();
		updateAudioAndBrightness();
		refreshListView();
	}
	
	private void updateAudioAndBrightness(){
		final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioSeekBar.setMax(maxVolume);
		mAudioSeekBar.setProgress(currentVolume);
		int currentValue = (int) (AndroidUtils.getActivityBrightness((Activity) mContext) * 255);
		if (currentValue < 0) {
			currentValue = AndroidUtils.getSystemBrightness(mContext);
		}
		mBrightNessSeekBar.setMax(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
		mBrightNessSeekBar.setProgress(currentValue / DuoKanConstants.BRIGHTNESS_STEP);
	}
	
	//init
	private void init() {
		setTitle(R.string.vp_select_function);
		View header = View.inflate(mContext, R.layout.vp_menu_setting_header, null);
		mAudioSeekBar = (SeekBar) header.findViewById(R.id.audio_seek_bar);
		mAudioSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		});
		mBrightNessSeekBar = (SeekBar) header.findViewById(R.id.brightness_seek_bar);
		mBrightNessSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				AndroidUtils.setActivityBrightness((Activity) mContext, progress * DuoKanConstants.BRIGHTNESS_STEP);
			}
		});
		updateAudioAndBrightness();
		addListHeader(header);
		mSettingAdapter = new SettingAdapter(mContext);
		mSettingAdapter.setOnPerformCheckedChangelistener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int id = buttonView.getId();
				if (DuoKanCodecConstants.sUseDuokanCodec) {
					if (id == 0) {
						onSwitchVideoWhClicked();
					} else if (id == 1) {
					} else if (id == 2) {
						if (DuoKanCodecConstants.USE_DIRAC_SOUND) {
							onAudioEffectClicked();
						}
					}
				} else {
					if (id == 0) {
						onSwitchVideoWhClicked();
					}
				}
			}
		});
		setAdapter(mSettingAdapter);
	}
	
	//private method
	private void refreshListView() {
		mSettingAdapter.setGroup(getAdapterValues(), getAdapterDrawables(), getSettingSelected());
	}
	
	private String[] getAdapterValues() {
		setupValues();
		if (DuoKanCodecConstants.sUseDuokanCodec) {
			return mDuokanCodecValues;
		} else {
			return mOriginCodecValues;
		}
	}
	
	private Drawable[] getAdapterDrawables(){
		setupValues();
		if (DuoKanCodecConstants.sUseDuokanCodec) {
			return mDuokanCodecDrawables;
		} else {
			return mOriginCodecDrawables;
		}
	}
	
	private boolean[] getSettingSelected(){
		if (DuoKanCodecConstants.sUseDuokanCodec && DuoKanCodecConstants.USE_DIRAC_SOUND) {
			return new boolean[] {IS_FULL_SCREEN, IS_AUDIO_EFFECT_ENHANCE};
		}else if(DuoKanCodecConstants.sUseDuokanCodec){
			return new boolean[] {IS_FULL_SCREEN};
		}else{
			return new boolean[] {IS_FULL_SCREEN};
		}
	}
	
	private void setupValues() {
		if (DuoKanCodecConstants.USE_DIRAC_SOUND) {
			mDuokanCodecValues = new String[]{mContext.getResources().getString(R.string.vp_scale_screen),
//					mContext.getResources().getString(R.string.vp_skip),
					mContext.getResources().getString(R.string.menu_item_enable_audio_enhance)
			};
			mDuokanCodecDrawables = new Drawable[]{mContext.getResources().getDrawable(R.drawable.play_full_icon),
//					mContext.getResources().getDrawable(R.drawable.play_skip_icon),
					mContext.getResources().getDrawable(R.drawable.play_sound_icon)
			};
        } else {
			mDuokanCodecValues = new String[]{mContext.getResources().getString(R.string.vp_scale_screen),
//				mContext.getResources().getString(R.string.vp_skip),
            };
			mDuokanCodecDrawables = new Drawable[]{mContext.getResources().getDrawable(R.drawable.play_full_icon),
//					mContext.getResources().getDrawable(R.drawable.play_skip_icon)
			};
		}

		mOriginCodecValues =  new String[]{
				mContext.getResources().getString(R.string.vp_scale_screen)
		};
		mOriginCodecDrawables = new Drawable[]{mContext.getResources().getDrawable(R.drawable.play_full_icon)
		};
	}
	
	//UI callback action
//	private void onMediaInfoClick(View view) {
//		mCanStartVideo = true;
//		mCanHideFullscreenBg = true;
//		
////		dismiss();
//		if(mMediaInfoShowing) {
//			mMediaInfoShowing = false;
//			Controller.sendMessage(UIConfig.MSG_WHAT_HIDE_MEDIA_INFO);
//			
//		} else {
//			mMediaInfoShowing = true;
//			Controller.sendMessage(UIConfig.MSG_WHAT_SHOW_MEDIA_INFO);
//		}
//	}
	
//	private void onScreenShotClicked() {
//		mCanStartVideo = false;
//		mCanHideFullscreenBg = true;
//		
//		dismiss();
//		mHandler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				Intent broadcast = new Intent("android.intent.action.CAPTURE_SCREENSHOT");
//				broadcast.putExtra("capture_delay", 0L);
//				mContext.sendOrderedBroadcast(broadcast, null, mCaptureScreenshotResultBroadcastReceiver, null, Activity.RESULT_OK, null, null);
//			}
//		}, 500);
//	}
	
	private void onAudioEffectClicked(){
		mCanStartVideo = true;
		mCanHideFullscreenBg = true;
		boolean isOpenDir = IS_AUDIO_EFFECT_ENHANCE;
		isOpenDir = !isOpenDir;
		IS_AUDIO_EFFECT_ENHANCE = isOpenDir;
		SoundEffect.turnOnMovieMode(IS_AUDIO_EFFECT_ENHANCE);
		refreshListView();
	}
	
	private void onSwitchVideoWhClicked() {
		mCanStartVideo = false;
		mCanHideFullscreenBg = false;
		IS_FULL_SCREEN = !IS_FULL_SCREEN;
		Message msg = Message.obtain();
		msg.what = UIConfig.MSG_WHAT_SCALE_SCREEN;
		if(IS_FULL_SCREEN){
			msg.arg1 = UIConfig.VIDEO_SIZE_STYLE_FULL_SCREEN;
		}else{
			msg.arg1 = UIConfig.VIDEO_SIZE_STYLE_AUTO;
		}
//		Controller.sendMessage(msg);
	}
	
	private BroadcastReceiver mCaptureScreenshotResultBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
//					Controller.sendMessage(UIConfig.MSG_WHAT_VIDEO_START);
				}
			}, 1000);
		}
	}; 
	
	@Override
	public int getGravity() {
		return Gravity.RIGHT;
	}

	@Override
	public int getAnimationStyle() {
		return R.style.rightmenu_popup_anim_style;
	}
}
