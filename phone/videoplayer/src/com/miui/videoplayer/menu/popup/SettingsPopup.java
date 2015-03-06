/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SettingsPopup.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-30
 */
package com.miui.videoplayer.menu.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.videoplayer.adapter.SettingAdapter;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DuoKanCodecConstants;
import com.miui.videoplayer.common.DuoKanConstants;
import com.miui.videoplayer.framework.popup.SettingPopupWindow;
import com.miui.videoplayer.model.PlayerSettings;
import com.miui.videoplayer.model.SoundEffect;
import com.miui.videoplayer.videoview.IVideoView;

/**
 * @author tianli
 *
 */
public class SettingsPopup extends BaseMenuPopup {

    public static final String TAG = SettingPopupWindow.class.getName();
    private SettingAdapter mSettingAdapter;
    
    private SeekBar mAudioSeekBar, mBrightNessSeekBar;
    
    private String[] mDuokanCodecValues;
    private String[] mOriginCodecValues;
    
    private Drawable[] mDuokanCodecDrawables;
    private Drawable[] mOriginCodecDrawables;
    
    private boolean mIsAudioEffEnhance = true;
    private boolean mIsFullScreen = false;
//    private Handler mHandler = new Handler(Looper.getMainLooper());
    
    AudioManager mAudioManager;
    int mMaxVolume;
    
    private IVideoView mVideoView;
    
    private PlayerSettings mPlayerSettings;
    
    public SettingsPopup(Context context, IVideoView videoView) {
        super(context);
        mVideoView = videoView;
        init();
    }
    
    private void init(){
        setTitle(getResources().getString(R.string.vp_select_function));
        mPlayerSettings = DKApp.getSingleton(PlayerSettings.class);
        mIsFullScreen = mPlayerSettings.isForceFullScreen();
        mIsAudioEffEnhance = mPlayerSettings.isAudioEffectOn();
        mAudioManager = (AudioManager) ((Activity) getContext()).getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        View header = View.inflate(getContext(), R.layout.vp_menu_setting_header, null);
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
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
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
                AndroidUtils.setActivityBrightness((Activity) getContext(), progress * DuoKanConstants.BRIGHTNESS_STEP);
            }
        });
        updateAudioAndBrightness();
        mListView.addHeaderView(header);
        mSettingAdapter = new SettingAdapter(getContext());
        mSettingAdapter.setOnPerformCheckedChangelistener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                if (DuoKanCodecConstants.sUseDuokanCodec) {
                    if (id == 0) {
                        onSwitchVideoWhClicked();
                    } else if (id == 1) {
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
        mListView.setAdapter(mSettingAdapter);
    }
    
    @Override
    public void show(ViewGroup anchor) {
        super.show(anchor);
        updateAudioAndBrightness();
        refreshListView();
    }

    private void setupValues() {
        if (DuoKanCodecConstants.USE_DIRAC_SOUND) {
            mDuokanCodecValues = new String[]{getContext().getResources().getString(R.string.vp_scale_screen),
//                  getContext().getResources().getString(R.string.vp_skip),
                    getContext().getResources().getString(R.string.menu_item_enable_audio_enhance)
            };
            mDuokanCodecDrawables = new Drawable[]{getContext().getResources().getDrawable(R.drawable.play_full_icon),
//                  getContext().getResources().getDrawable(R.drawable.play_skip_icon),
                    getContext().getResources().getDrawable(R.drawable.play_sound_icon)
            };
        } else {
            mDuokanCodecValues = new String[]{getContext().getResources().getString(R.string.vp_scale_screen),
//              getContext().getResources().getString(R.string.vp_skip),
            };
            mDuokanCodecDrawables = new Drawable[]{getContext().getResources().getDrawable(R.drawable.play_full_icon),
//                  getContext().getResources().getDrawable(R.drawable.play_skip_icon)
            };
        }
        mOriginCodecValues =  new String[]{
                getContext().getResources().getString(R.string.vp_scale_screen)
        };
        mOriginCodecDrawables = new Drawable[]{getContext().getResources().getDrawable(R.drawable.play_full_icon)
        };
    }
    
    private void updateAudioAndBrightness(){
        final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioSeekBar.setMax(mMaxVolume);
        mAudioSeekBar.setProgress(currentVolume);
        int currentValue = (int) (AndroidUtils.getActivityBrightness((Activity) getContext()) * 255);
        if (currentValue < 0) {
            currentValue = AndroidUtils.getSystemBrightness(getContext());
        }
        mBrightNessSeekBar.setMax(DuoKanConstants.BRIGHTNESS_MAX_VALUE);
        mBrightNessSeekBar.setProgress(currentValue / DuoKanConstants.BRIGHTNESS_STEP);
    }
    
    private void onAudioEffectClicked(){
        boolean isOpenDir = mIsAudioEffEnhance;
        isOpenDir = !isOpenDir;
        mIsAudioEffEnhance = isOpenDir;
        DKApp.getSingleton(PlayerSettings.class).setAudioEffect(mIsAudioEffEnhance);
        SoundEffect.turnOnMovieMode(mIsAudioEffEnhance);
        refreshListView();
    }
    
    private void onSwitchVideoWhClicked() {
        mIsFullScreen = !mIsFullScreen;
        DKApp.getSingleton(PlayerSettings.class).setForeceFullScreen(mIsFullScreen);
        if(mVideoView != null){
            mVideoView.setForceFullScreen(mIsFullScreen);
        }
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
            return new boolean[] {mIsFullScreen, mIsAudioEffEnhance};
        }else if(DuoKanCodecConstants.sUseDuokanCodec){
            return new boolean[] {mIsFullScreen};
        }else{
            return new boolean[] {mIsFullScreen};
        }
    }

    @Override
    protected int getPopupWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.vp_menu_popup_settings_width);
    }
    
    
}
