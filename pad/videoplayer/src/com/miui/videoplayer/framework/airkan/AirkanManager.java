package com.miui.videoplayer.framework.airkan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MiuiSettings;
import android.util.Log;
import android.view.KeyEvent;
import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.MilinkClientManagerDelegate;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.milink.IDeviceDiscoveryListener;
import com.miui.videoplayer.framework.milink.VideoControlData;
import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.popup.VolumePopupWindow;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.utils.DuoKanUtils;
import com.miui.videoplayer.framework.views.OriginMediaController;

public class AirkanManager {
    private static final String TAG = AirkanManager.class.getSimpleName();

    public static final String AIRKAN_DEVICE_XIAOMI_PHONE = "airkan_device_xiaomi_phone";

    private static final int MESSAGE_WHAT_DIRECT_PLAY_TO_EXIST_DEVICE = 0;
    private static final int MESSAGE_WHAT_FADE_OUT_VOLUME_POPUP_WINDOW = 1;

    private static final int MESSAGE_WHAT_DEVICES_EMPTY = 2;
    private static final int MESSAGE_WHAT_DEVICES_NOT_EMPTY = 3;

    private static final float AIRKAN_VOLUME_DELTA = 10;

    private AirkanOnChangedListener mAirkanOnChangedListener;

    private Hashtable<String, String> mDevices;
    private MilinkClientManager mVideoManager;

    private OriginMediaController mMediaController;
    private DuoKanMediaController mDuokanMediaController;

    private LocalMediaPlayerControl mLocalPhoneMediaControl;
    private RemoteTVMediaPlayerControl mRemoteTVMediaControl;

    private Context mContext;

    private String mPlayingDeviceName = AIRKAN_DEVICE_XIAOMI_PHONE;

    private VolumePopupWindow mVolumePopupWindow;

    private boolean mDirectAirkan = false;
    private String mExistDeviceName;
    private Uri mDirectAirkanUri;

    private PlayHistoryManager mPlayHistoryManager;

    private List<IDeviceDiscoveryListener> mDeviceChangedListenerList = new ArrayList<IDeviceDiscoveryListener>();

    public void registeOnDeviceChangeListener(IDeviceDiscoveryListener listener) {
        if (!mDeviceChangedListenerList.contains(listener)) {
            mDeviceChangedListenerList.add(listener);
        }
    }

    public void unregisteOnDeviceChangeListener(IDeviceDiscoveryListener listener) {
        if (mDeviceChangedListenerList.contains(listener)) {
            mDeviceChangedListenerList.remove(listener);
        }
    }

    public AirkanManager(LocalMediaPlayerControl localPhoneMediaControl,
            OriginMediaController mediaController, Context context) {
        this.mLocalPhoneMediaControl = localPhoneMediaControl;
        this.mContext = context;
        this.mMediaController = mediaController;
        this.mPlayHistoryManager = new PlayHistoryManager(context);
        this.mDevices = new Hashtable<String, String>();
        initAirkan();
    }

    public AirkanManager(LocalMediaPlayerControl localPhoneMediaControl,
            OriginMediaController mediaController, Context context,
            AirkanExistDeviceInfo airkanExistDeviceInfo) {
        this.mLocalPhoneMediaControl = localPhoneMediaControl;
        this.mContext = context;
        this.mMediaController = mediaController;
        this.mPlayHistoryManager = new PlayHistoryManager(context);
        this.mDevices = new Hashtable<String, String>();
        if(airkanExistDeviceInfo != null && airkanExistDeviceInfo.getExistDeviceName() != null){
            this.mExistDeviceName = airkanExistDeviceInfo.getExistDeviceName();
            this.mDirectAirkan = true;
        }
        initAirkan();
    }

    public void setAirkanOnChangedListener(AirkanOnChangedListener airkanOnChangedListener) {
        this.mAirkanOnChangedListener = airkanOnChangedListener;
    }

    public boolean isPlayingInLocal() {
        return AIRKAN_DEVICE_XIAOMI_PHONE.equals(mPlayingDeviceName);
    }

    private void initAirkan() {
        String deviceName = null;
        try {
        	deviceName = MiuiSettings.System.getDeviceName(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = "XiaoMi";
        }
        mVideoManager = new MilinkClientManager(mContext);
        mVideoManager.setDeviceName(deviceName);
        mVideoManager.setDelegate(mDelegate);
        mRemoteTVMediaControl = new RemoteTVMediaPlayerControl(mContext, this);
        mRemoteTVMediaControl.setVideoManager(mVideoManager);
    }

    public void openDeviceManager() {
        if (mVideoManager != null) {
        	try{
        		Log.i(TAG, "Bind Phone Service!!!");
        		mVideoManager.open();
        	}catch (Exception e) {
        		Log.e(TAG, "already bound.", e);
        	}
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
        	if (msg.what == MESSAGE_WHAT_DIRECT_PLAY_TO_EXIST_DEVICE) {
        		Log.i(TAG, "Direct play to device: " + mExistDeviceName);
        		final Uri uri = (Uri) msg.obj;
                if (mExistDeviceName != null && mDirectAirkanUri != null) {
                    playToDevice(mExistDeviceName, (Uri)msg.obj);
                }
        	}
        	
        	if (msg.what == MESSAGE_WHAT_FADE_OUT_VOLUME_POPUP_WINDOW) {
        		if (mVolumePopupWindow != null && mVolumePopupWindow.isShowing()) {
        			mVolumePopupWindow.dismiss();
        		}
        	}
        	
        	if (msg.what == MESSAGE_WHAT_DEVICES_EMPTY) {
        		if (mMediaController != null && mMediaController.getCtrlMenuPopupWindow() != null) {
        			mMediaController.getCtrlMenuPopupWindow().setMiLinkEnabled(false);
        		}
        	}
        	
        	if (msg.what == MESSAGE_WHAT_DEVICES_NOT_EMPTY) {
        		if (mMediaController != null && mMediaController.getCtrlMenuPopupWindow() != null) {
        			mMediaController.getCtrlMenuPopupWindow().setMiLinkEnabled(true);
        			
        			boolean tipOff = false;
        			if (mContext instanceof VideoPlayerActivity) {
        				VideoPlayerActivity activity = (VideoPlayerActivity) mContext;
        				SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        				tipOff = sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_AIREKAN_USER_USED, false)
        						||activity.isDirectAirkan();
        			}
        		}
        	}
        }
	    
    };

    public void closeDeviceManager() {
        if (mVideoManager != null) {
            Log.i(TAG, "Start Unbind Phone Service!!!");
            mVideoManager.close();
            Log.i(TAG, "End Unbind Phone Service!!!");
        }
    }

    MilinkClientManagerDelegate mDelegate = new MilinkClientManagerDelegate() {

        @Override
        public void onOpen() {
            Log.v(TAG, "open device manager success");
            for (IDeviceDiscoveryListener listener : mDeviceChangedListenerList) {
                listener.onOpened();
            }
            onDeviceCountChanged();
            if (mDirectAirkan && mDirectAirkanUri != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "start airkan directly");
                        Message m = Message.obtain();
                        m.obj = mDirectAirkanUri;
                        m.what = MESSAGE_WHAT_DIRECT_PLAY_TO_EXIST_DEVICE;
                        if (DuoKanUtils.isValidFormatVideo(mDirectAirkanUri)) {
                            mHandler.sendMessage(m);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void onClose() {
            Log.v(TAG, "device manager closed");
        }

        private void onDeviceCountChanged() {
            new Thread(new Runnable() {

                @Override
                public void run() {
                	try{
                		List<String> deviceList = queryAirkanDevices();
                		if (deviceList != null) {
                			if (deviceList.isEmpty()) {
                				mHandler.sendEmptyMessage(MESSAGE_WHAT_DEVICES_EMPTY);
                			} else {
                				mHandler.sendEmptyMessage(MESSAGE_WHAT_DEVICES_NOT_EMPTY);
                			}
                		}
                	}catch (Exception e) {
                        Log.e(TAG, "queryAirkanDevices exception.");
                	}
                }
            }).start();
        }

        @Override
        public void onDeviceFound(String deviceId, String name, DeviceType type) {
            if (type != DeviceType.TV) return;
            Log.v(TAG, "device found: " + name);
            mDevices.put(name, deviceId);
            for (IDeviceDiscoveryListener listener : mDeviceChangedListenerList) {
                listener.onDeviceAdded(name);
            }
            onDeviceCountChanged();
        }

        @Override
        public void onDeviceLost(String deviceId) {
            String name = null;
            synchronized (mDevices) {
            	for (Iterator<String> it = mDevices.keySet().iterator(); it.hasNext();) {
                    String dn = it.next();
                    if (mDevices.get(dn).equals(deviceId)) {
                        name = dn;
                        break;
                    }
                }
			}
            if (name != null) {
                for (IDeviceDiscoveryListener listener : mDeviceChangedListenerList) {
                    listener.onDeviceRemoved(name);
                }
                onDeviceCountChanged();
            }
        }

        @Override
        public void onConnected() {
            Log.i(TAG, "onConnected");
            mMediaController.setUpdateProgressEnable(true);
            mRemoteTVMediaControl.startPlay();
        }

        @Override
        public void onConnectedFailed(ErrorCode errorCode) {
            Log.i(TAG, "onConnectedFailed");
            mMediaController.setUpdateProgressEnable(false);
            stopRemotePlay();
            remotePlayStoped();
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "onDisconnected");
            remotePlayStoped();
        }

        @Override
        public void onLoading() {
            Log.i(TAG, "onLoading");
        }

        @Override
        public void onPlaying() {
            Log.i(TAG, "onPlaying");
            mMediaController.updatePlayingState(true);
            mMediaController.setUpdateProgressEnable(true);
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "onStopped");
            remotePlayStoped();
        }

        @Override
        public void onPaused() {
            Log.i(TAG, "onPaused");
            mMediaController.updatePlayingState(false);
            mMediaController.setUpdateProgressEnable(false);
        }

        @Override
        public void onVolume(int volume) {
        }

        @Override
        public void onNextAudio(boolean isAuto) {
        }

        @Override
        public void onPrevAudio(boolean isAuto) {
        }

        private void remotePlayStoped() {
            AirkanChangedEvent event = new AirkanChangedEvent(
                    AirkanChangedEvent.CODE_AIR_KAN_BACK_TO_PHONE);
            onAirkanChanged(event);
        }
    };

    // call in other thread
    public List<String> queryAirkanDevices() {
    	List<String> result = new ArrayList<String>();
    	synchronized (mDevices) {
        	result.addAll(mDevices.keySet());
    	}
    	return result;
    }

    public void onAirkanChanged(AirkanChangedEvent event) {
        if (mAirkanOnChangedListener != null) {
            mAirkanOnChangedListener.onAirKanChanged(event);
        }
    }

    public void takebackToPhone() {
        ((Activity) mContext).setVolumeControlStream(AudioManager.RINGER_MODE_NORMAL);

        mPlayingDeviceName = AIRKAN_DEVICE_XIAOMI_PHONE;

        AirkanChangedEvent event = new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_BACK_TO_PHONE);
        onAirkanChanged(event);
        VideoControlData remoteControlData = getRemoteVideoControlDate();
        stopRemotePlay();
        controlLocalPlay();
        playInLocal(remoteControlData);
    }

    public void playToDevice(final String deviceName, Uri videoUri) {
        Log.i(TAG, "Play to device: " + deviceName + " uri:" + videoUri);
        ((Activity) mContext).setVolumeControlStream(AudioManager.RINGER_MODE_SILENT);

        mPlayingDeviceName = deviceName;

        AirkanChangedEvent event = new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_PLAY_TO_DEVICE);
        onAirkanChanged(event);

        controlRemotePlay();
        playInRemoteTV(deviceName, videoUri);
        stopLocalPlay();
    }

    private void playInLocal(VideoControlData remoteControlData) {
        int position = remoteControlData.getPosition();
        String uri = remoteControlData.getURL();
        mLocalPhoneMediaControl.startLocalPlayForAirkan(Uri.parse(uri));
        mLocalPhoneMediaControl.seekTo(position);
        if (!mDirectAirkan) {
//            mMediaController.changeToLocalPlaySize();
        }
        if (mDuokanMediaController != null) {
            mDuokanMediaController.checkNetwork(Uri.parse(uri));
        }
    }

    public void stopRemotePlay() {
        Log.i(TAG, "call stop and tackback");
        mRemoteTVMediaControl.stop();
        mRemoteTVMediaControl.takeBack();
    }

    private VideoControlData getLocalVideoControlDate() {
        VideoControlData result = new VideoControlData();
        result.setPosition(mLocalPhoneMediaControl.getCurrentPosition());
        result.setPlaying(mLocalPhoneMediaControl.isPlaying());
        result.setDuration(mLocalPhoneMediaControl.getDuration());
        return result;
    }

    private VideoControlData getRemoteVideoControlDate() {
        VideoControlData result = new VideoControlData();
        result.setDuration(mRemoteTVMediaControl.getDuration());
        result.setPosition(mRemoteTVMediaControl.getCurrentPosition());
        result.setPlaying(mRemoteTVMediaControl.isPlaying());
        result.setURL(mRemoteTVMediaControl.getUri());
        return result;
    }

    private void playInRemoteTV(String deviceName, Uri videoUri) {
        VideoControlData localControlData = getLocalVideoControlDate();
        int localPosition = localControlData.getPosition();
        Log.i(TAG, "air play url: " + videoUri.toString());
        mRemoteTVMediaControl.setVideoURI(videoUri.toString(), "", localPosition);
        mRemoteTVMediaControl.playTo(deviceName);
        mMediaController.changeToAirkanSize();
    }

    private void stopLocalPlay() {
        mLocalPhoneMediaControl.stopLocalPlayForAirkan();
    }

    private void controlLocalPlay() {
        mMediaController.setMediaPlayer(mLocalPhoneMediaControl);
        mRemoteTVMediaControl.setMediaController(null);
    }

    private void controlRemotePlay() {
        mRemoteTVMediaControl.setMediaController(mMediaController);
    }

    public boolean onVolumeKeyEvent(KeyEvent event) {
        if (this.isPlayingInLocal()) {
            return false;
        }
        // ((Activity)mContext).setVolumeControlStream(AudioManager.RINGER_MODE_SILENT);
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            decreaseDeviceVolume();
        } else {
            increaseDeviceVolume();
        }

        return true;
    }

    private void increaseDeviceVolume() {
        float current = mRemoteTVMediaControl.getVolume();
        Log.i(TAG, "current airkan volume:  " + current);
        float newvol = current + AIRKAN_VOLUME_DELTA;
        if (newvol > 100f) {
            newvol = 100f;
        }
        Log.i(TAG, "increase airkan volume to:  " + newvol);
        mRemoteTVMediaControl.setVolume(newvol);
        // showVolumePopupWindow(newvol);
    }

    private void decreaseDeviceVolume() {
        float current = mRemoteTVMediaControl.getVolume();
        Log.i(TAG, "current airkan volume:  " + current);
        float newvol = current - AIRKAN_VOLUME_DELTA;
        if (newvol < 0f) {
            newvol = 0f;
        }
        Log.i(TAG, "decrease airkan volume to:  " + newvol);
        mRemoteTVMediaControl.setVolume(newvol);
        // showVolumePopupWindow(newvol);
    }

    public void showVolumePopupWindow(float newVolume) {
        if (mVolumePopupWindow == null) {
            mVolumePopupWindow = new VolumePopupWindow(mContext);
            if (DuoKanConstants.ENABLE_V5_UI) {
                mVolumePopupWindow = new VolumePopupWindow(mContext,
                        R.layout.vp_popup_right_vertical_seekbar_group_v5);
            }
        }

        mVolumePopupWindow.setMaxSeekbarValue(100);
        if (!mVolumePopupWindow.isShowing()) {
            mVolumePopupWindow.setShowProgressNumber(false);
            mVolumePopupWindow.show(mMediaController.getAnchor(), mContext);
        }
        mVolumePopupWindow.updateSeekbarValue((int) (newVolume * 100));

        mHandler.removeMessages(MESSAGE_WHAT_FADE_OUT_VOLUME_POPUP_WINDOW);
        mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_FADE_OUT_VOLUME_POPUP_WINDOW, 1000);
    }

    public void onActivityCreate() {
        // openDeviceManager();
    }

    public void onActivityStart() {
    }

    public void onActivityStop() {
    }

    public void onActivityDestroy() {
        // if (!isPlayingInLocal()) {
        // this.stopRemotePlay();
        // }
        // closeDeviceManager();
    }

    public String getPlayingDeviceName() {
        return mPlayingDeviceName;
    }

    public void setDirectAirkanUri(Uri uri) {
        mDirectAirkanUri = uri;
    }

    public void setDuokanMediaController(DuoKanMediaController duokanMediaController) {
        this.mDuokanMediaController = duokanMediaController;
    }

    public int getCurrentPosition() {
        if (mRemoteTVMediaControl != null) {
            return mRemoteTVMediaControl.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mRemoteTVMediaControl != null) {
            return mRemoteTVMediaControl.getDuration();
        }
        return 0;
    }

    public int getLocalDuration() {
        if (mLocalPhoneMediaControl != null) {
            return mLocalPhoneMediaControl.getDuration();
        }
        return 0;
    }

    public String getDeviceByName(String name) {
        if (name == null) return null;
        return mDevices.get(name);
    }

    public static interface AirkanOnChangedListener {
        public void onAirKanChanged(AirkanChangedEvent event);
    }

    public static class AirkanChangedEvent {
        public static final int CODE_AIR_KAN_PLAY_TO_DEVICE = 0;
        public static final int CODE_AIR_KAN_BACK_TO_PHONE = 1;
        public static final int CODE_AIR_KAN_PLAY_STOPED = 2;

        private int code;

        public AirkanChangedEvent(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static class AirkanExistDeviceInfo {

        private String mExistDeviceName;

        public AirkanExistDeviceInfo(String existDeviceName) {
            this.mExistDeviceName = existDeviceName;
        }

        public String getExistDeviceName() {
            return mExistDeviceName;
        }
    }
}