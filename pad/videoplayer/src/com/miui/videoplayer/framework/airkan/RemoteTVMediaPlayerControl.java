package com.miui.videoplayer.framework.airkan;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;

import com.miui.videoplayer.framework.ui.MediaPlayerControl;
import com.miui.videoplayer.framework.views.OriginMediaController;

public class RemoteTVMediaPlayerControl implements MediaPlayerControl {

    private static final String TAG = RemoteTVMediaPlayerControl.class.getSimpleName();
    private Context mContext;

    public RemoteTVMediaPlayerControl(Context context, AirkanManager manager) {
        this.mContext = context;
        this.mManager = manager;
    }

    private AirkanManager mManager;
    private OriginMediaController mMediaController;
    private MilinkClientManager mVideoManager;
    private String mDeviceName;

    private String mUri;
    private String mTitle;
    protected long mCurrentPosition;
    protected long mWantedPosition;
    protected long mDuration;
    protected boolean mIsPlaying;

    public String getUri() {
        return mUri;
    }

    public void setMediaController(OriginMediaController mediaController) {
        if (mMediaController != null) {
            // mMediaController.hide();
        }
        this.mMediaController = mediaController;
        if (mMediaController != null) {
            mMediaController.show(0);
            mMediaController.setEnabled(true);
            mMediaController.setMediaPlayer(this);
        }
    }

    public void setVideoManager(MilinkClientManager videoManager) {
        this.mVideoManager = videoManager;
    }

    public void setVideoURI(String url, String title, int position) {
        Log.i(TAG, "setVideoURI " + title + " " + url);
        mUri = url;
        mTitle = title;
        mWantedPosition = position;
    }

    public float getVolume() {
        if (mVideoManager != null) {
            return mVideoManager.getVolume();
        }
        return 0.0f;
    }

    public void setVolume(float volume) {
        if (mVideoManager != null) {
            mVideoManager.setVolume((int) volume);
        }
    }

    public void startPlay() {
        if (mVideoManager != null) {
            mVideoManager.startPlay(mUri, mTitle, (int) mWantedPosition, 0.0, MediaType.Video);
        }
    }

    public void playTo(String deviceName) {
        if (mVideoManager != null) {
            mVideoManager.connect(mManager.getDeviceByName(deviceName), 3000);
            mDeviceName = deviceName;
        }
    }

    public void takeBack() {
        if (mVideoManager != null) {
            mVideoManager.disconnect();
        }
    }

    @Override
    public void start() {
        if (mVideoManager != null) {
            mVideoManager.setPlaybackRate(1);
        }
    }

    @Override
    public void pause() {
        if (mVideoManager != null) {
            mVideoManager.setPlaybackRate(0);
        }
    }

    @Override
    public int getDuration() {
        int duration = 0;
        if (mVideoManager != null) {
            duration = mVideoManager.getPlaybackDuration();
        }
        if (duration <= 0) {
            return mManager.getLocalDuration();
        }
        return duration;
    }

    @Override
    public int getCurrentPosition() {
        if (mVideoManager != null) {
            return mVideoManager.getPlaybackProgress();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mVideoManager != null) {
            mVideoManager.setPlaybackProgress(pos);
        }
    }

    public void stop() {
        if (mVideoManager != null) {
            mVideoManager.stopPlay();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mVideoManager != null) {
            return mVideoManager.getPlaybackRate() == 1;
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setSpName(String spName) {
    }

    public void updatePlayingState(boolean playing) {
        if (mMediaController != null) {
            mMediaController.updatePlayingState(playing);
        }
    }

    public void showMediaController() {
        if (mMediaController != null) {
            mMediaController.show(0);
        }
    }

    public void setVideoURI(String title, int position, long mediaID, int ci, int preferedSource,
            String url) {
        mUri = url;
        mWantedPosition = position;
        mTitle = title;
    }
}
