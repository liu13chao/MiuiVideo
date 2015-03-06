package com.miui.videoplayer.framework.airkan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.MilinkClientManagerDelegate;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;
import com.miui.video.R;
import com.miui.videoplayer.framework.milink.IDeviceDiscoveryListener;
import com.miui.videoplayer.framework.milink.VideoControlData;
import com.miui.videoplayer.media.MediaPlayerControl;


public class AirkanManager {
	
    private final String TAG = "AirkanManager";

    public static String AIRKAN_DEVICE_XIAOMI_PHONE;

    private final float AIRKAN_VOLUME_DELTA = 10;

    private OnStatusChangedListener mAirkanOnChangedListener;

    private Hashtable<String, String> mDevices;
    private MilinkClientManager mVideoManager;

    private MediaPlayerControl mLocalMediaControl;
    private RemoteMediaPlayerControl mRemoteMediaControl;

    private Context mContext;

    private String mPlayingDeviceName;

    public static final int STATE_LOCAL = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    
    private int mState = STATE_LOCAL;
    
//    private boolean mDirectAirkan = false;
//    private String mExistDeviceName;
//    private Uri mDirectAirkanUri;
    
    /* Observers */
    private List<IDeviceDiscoveryListener> mDeviceChangedListenerList = 
    		new ArrayList<IDeviceDiscoveryListener>();
    private List<OnStatusChangedListener> mObservers = 
    		new ArrayList<OnStatusChangedListener>();

    public AirkanManager(Context context) {
        mContext = context.getApplicationContext();
        mDevices = new Hashtable<String, String>();
        AIRKAN_DEVICE_XIAOMI_PHONE = mContext.getResources().getString(R.string.airkan_device_xiaomi_phone);
        mPlayingDeviceName = AIRKAN_DEVICE_XIAOMI_PHONE;
        initAirkan();
    }

    public void registeOnDeviceChangeListener(IDeviceDiscoveryListener listener) {
    	if(listener != null){
    		synchronized (mDeviceChangedListenerList) {
                if (!mDeviceChangedListenerList.contains(listener)) {
                    mDeviceChangedListenerList.add(listener);
                }
			}
    	}
    }

    public void unregisteOnDeviceChangeListener(IDeviceDiscoveryListener listener) {
    	if(listener != null){
    		synchronized (mDeviceChangedListenerList) {
    	        if (mDeviceChangedListenerList.contains(listener)) {
    	            mDeviceChangedListenerList.remove(listener);
    	        }
			}
    	}
    }
    
    public void registerStatusObserver(OnStatusChangedListener observer){
    	if(observer != null){
    		synchronized (mObservers) {
    			if(!mObservers.contains(observer)){
    				mObservers.add(observer);
    			}
    		}
    	}
    }
    
    public void unRegisterStatusObserver(OnStatusChangedListener observer){
    	if(observer != null){
    		synchronized (mObservers) {
    			if(mObservers.contains(observer)){
    				mObservers.remove(observer);
    			}
    		}
    	}
    }
    
    public void setLocalMediaControl(MediaPlayerControl mediaControl){
    	mLocalMediaControl = mediaControl;
    }
    
    public void setAirkanOnChangedListener(OnStatusChangedListener airkanOnChangedListener) {
        this.mAirkanOnChangedListener = airkanOnChangedListener;
    }
    
    public RemoteMediaPlayerControl getPlayer(){
    	return mRemoteMediaControl;
    }

    public boolean isPlayingInLocal() {
    	return mState == STATE_LOCAL;
    }

    private void initAirkan() {
    	String deviceName = null;
        try {
        	deviceName = MiuiSettings.System.getDeviceName(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = "XiaoMi";
        }
        mVideoManager = new MilinkClientManager(mContext);
        mVideoManager.setDeviceName(deviceName);
        mVideoManager.setDelegate(mDelegate);
        mRemoteMediaControl = new RemoteMediaPlayerControl(mContext, this);
        mRemoteMediaControl.setVideoManager(mVideoManager);
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

    public void closeDeviceManager() {
        if (mVideoManager != null) {
            Log.i(TAG, "Start Unbind Phone Service!!!");
            mVideoManager.close();
            Log.i(TAG, "End Unbind Phone Service!!!");
        }
    }
    
    private void onRemotePlayStoped() {
        AirkanChangedEvent event = new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_PLAY_STOPED);
        onAirkanChanged(event);
    }
    
    private void onRemotePlayPaused() {
        AirkanChangedEvent event = new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_PLAY_PAUSED);
        onAirkanChanged(event);
    }
    
    private void onRemotePlayStarted() {
        AirkanChangedEvent event = new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_PLAY_STARTED);
        onAirkanChanged(event);
    }

    MilinkClientManagerDelegate mDelegate = new MilinkClientManagerDelegate() {
        @Override
        public void onOpen() {
            Log.v(TAG, "open device manager success");
            for (IDeviceDiscoveryListener listener : mDeviceChangedListenerList) {
                listener.onOpened();
            }
        }

        @Override
        public void onClose() {
            Log.v(TAG, "device manager closed");
        }

        @Override
        public void onDeviceFound(String deviceId, String name, DeviceType type) {
            if (type != DeviceType.TV) return;
            Log.v(TAG, "device found: " + name);
            mDevices.put(name, deviceId);
            for (IDeviceDiscoveryListener listener : mDeviceChangedListenerList) {
                listener.onDeviceAdded(name);
            }
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
            }
        }

        @Override
        public void onConnected() {
        	mState = STATE_CONNECTED;
            Log.i(TAG, "onConnected");
            mRemoteMediaControl.startPlay();
        }

        @Override
        public void onConnectedFailed(ErrorCode errorCode) {
            Log.i(TAG, "onConnectedFailed");
            if(!isPlayingInLocal()){
            	takebackToPhone();
            }
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "onDisconnected");
//            if(mState == STATE_CONNECTED){
//            	takebackToPhone();
//            }
        }

        @Override
        public void onLoading() {
            Log.i(TAG, "onLoading");
        }

        @Override
        public void onPlaying() {
            Log.i(TAG, "onPlaying");
            onRemotePlayStarted();
            //TODO:
//            mMediaController.updatePlayingState(true);
//            mMediaController.setUpdateProgressEnable(true);
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "onStopped");
            if(mState != STATE_LOCAL){
                onRemotePlayStoped();
                takebackToPhone();
            }
        }

        @Override
        public void onPaused() {
            Log.i(TAG, "onPaused");
            onRemotePlayPaused();
            //TODO:
//            mMediaController.updatePlayingState(false);
//            mMediaController.setUpdateProgressEnable(false);
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
    };

    // call in other thread
    public List<String> queryAirkanDevices() {
    	List<String> result = new ArrayList<String>();
    	synchronized(mDevices){
    		result.addAll(mDevices.keySet());
    	}
        return result;
    }

    public void onAirkanChanged(AirkanChangedEvent event) {
        if (mAirkanOnChangedListener != null) {
            mAirkanOnChangedListener.onStatusChanged(event);
        }
    }
    
    public void stopRemotePlay() {
        Log.i(TAG, "call stop and tackback");
        mRemoteMediaControl.stop();
        mRemoteMediaControl.takeBack();
    }

    public void takebackToPhone() {
    	mState = STATE_LOCAL;
//        ((Activity) mContext).setVolumeControlStream(AudioManager.RINGER_MODE_NORMAL);
        mPlayingDeviceName = AIRKAN_DEVICE_XIAOMI_PHONE;
        VideoControlData remoteControlData = getRemoteVideoControlDate();
        mRemoteMediaControl.stop();
        mRemoteMediaControl.takeBack();
    	onAirkanChanged(new AirkanChangedEvent(
    			AirkanChangedEvent.CODE_AIR_KAN_BACK_TO_PHONE));
        playInLocal(remoteControlData);
    }

    public void playToDevice(final String deviceName) {
    	Uri uri = mLocalMediaControl.getUri();
    	playToDevice(deviceName, uri);
    }
    
    public void playToDevice(final String deviceName, Uri uri) {
    	mState = STATE_CONNECTING;
        Log.i(TAG, "Play to device: " + deviceName);
//        ((Activity) mContext).setVolumeControlStream(AudioManager.RINGER_MODE_SILENT);
        playInRemote(deviceName, uri, getLocalVideoControlDate());
        mPlayingDeviceName = deviceName;
        stopLocalPlay();
        onAirkanChanged(new AirkanChangedEvent(
                AirkanChangedEvent.CODE_AIR_KAN_PLAY_TO_DEVICE));
    }

    private void playInLocal(VideoControlData remoteControlData) {
    	mState = STATE_LOCAL;
    	
    	Log.d(TAG, "playInLocal ");
        int position = remoteControlData.getPosition();
//        String uri = remoteControlData.getURL();
        if(mLocalMediaControl != null){
//        	Uri curUri = mLocalMediaControl.getUri();
//        	if(curUri != null && uri != null && curUri.toString().equals(uri)){
        		mLocalMediaControl.start();
//        	}else{
//        		mLocalMediaControl.close();
//                mLocalMediaControl.setDataSource(uri);
//        		mLocalMediaControl.start();
//        	}
        	if(mLocalMediaControl.getCurrentPosition()/10000 != position/10000){
        		// difference is beyond 10 seconds.
        		mLocalMediaControl.seekTo(position);
        	}
        }
        // TODO: show loading.
//        if (mDuokanMediaController != null) {
//            mDuokanMediaController.checkNetwork(Uri.parse(uri));
//        }
    }

    private VideoControlData getLocalVideoControlDate() {
        VideoControlData result = new VideoControlData();
        result.setPosition(mLocalMediaControl.getRealPlayPosition());
        result.setPlaying(mLocalMediaControl.isPlaying());
        result.setDuration(mLocalMediaControl.getDuration());
        return result;
    }

    private VideoControlData getRemoteVideoControlDate() {
        VideoControlData result = new VideoControlData();
        result.setDuration(mRemoteMediaControl.getDuration());
        result.setPosition(mRemoteMediaControl.getCurrentPosition());
        result.setPlaying(mRemoteMediaControl.isPlaying());
        if(mRemoteMediaControl.getUri() != null){
            result.setURL(mRemoteMediaControl.getUri().toString());
        }
        return result;
    }

    private void playInRemote(String deviceName, Uri videoUri, VideoControlData vcd) {
//        VideoControlData localControlData = getLocalVideoControlDate();
        if(videoUri == null){
            return;
        }
    	mState = STATE_CONNECTING;
        int localPosition = vcd.getPosition();
        Log.i(TAG, "air play url: " + videoUri.toString());
        mRemoteMediaControl.setVideoUri(videoUri.toString(), "", localPosition);
        mRemoteMediaControl.playTo(deviceName);
    }

    private void stopLocalPlay() {
    	if(mLocalMediaControl != null){
    		mLocalMediaControl.pause();
    	}
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
        float current = mRemoteMediaControl.getVolume();
        Log.i(TAG, "current airkan volume:  " + current);
        float newvol = current + AIRKAN_VOLUME_DELTA;
        if (newvol > 100f) {
            newvol = 100f;
        }
        Log.i(TAG, "increase airkan volume to:  " + newvol);
        mRemoteMediaControl.setVolume(newvol);
    }

    private void decreaseDeviceVolume() {
        float current = mRemoteMediaControl.getVolume();
        Log.i(TAG, "current airkan volume:  " + current);
        float newvol = current - AIRKAN_VOLUME_DELTA;
        if (newvol < 0f) {
            newvol = 0f;
        }
        Log.i(TAG, "decrease airkan volume to:  " + newvol);
        mRemoteMediaControl.setVolume(newvol);
    }

    public String getPlayingDeviceName() {
        return mPlayingDeviceName;
    }

//    public void setDirectAirkanUri(Uri uri) {
//        mDirectAirkanUri = uri;
//    }

    public int getCurrentPosition() {
        if (mRemoteMediaControl != null) {
            return mRemoteMediaControl.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mRemoteMediaControl != null) {
            return mRemoteMediaControl.getDuration();
        }
        return 0;
    }

    public int getLocalDuration() {
        if (mLocalMediaControl != null) {
            return mLocalMediaControl.getDuration();
        }
        return 0;
    }

    public String getDeviceByName(String name) {
        if (name == null) return null;
        return mDevices.get(name);
    }

    public static interface OnStatusChangedListener {
        public void onStatusChanged(AirkanChangedEvent event);
    }

    public static class AirkanChangedEvent {
        public static final int CODE_AIR_KAN_PLAY_TO_DEVICE = 0;
        public static final int CODE_AIR_KAN_BACK_TO_PHONE = 1;
        public static final int CODE_AIR_KAN_PLAY_STOPED = 2;
        public static final int CODE_AIR_KAN_PLAY_PAUSED = 3;
        public static final int CODE_AIR_KAN_PLAY_STARTED = 4;

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
