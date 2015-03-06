package com.miui.videoplayer.framework.airkan;

import java.util.Map;

import android.net.Uri;
import android.util.Log;

import com.duokan.MediaPlayer.MediaInfo;
import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.MediaType;

public class MilinkMediaPlayerControl implements RemoteMediaPlayerControlNew {
    public static final String TAG = "MilinkMediaPlayerControl";
    
//    private Context mContext;
    
//    private AirkanManagerNew mManager;
    //  private OriginMediaController mMediaController;
    private MilinkClientManager mVideoManager;
    private String mDeviceName;

    private String mUri;
    private String mTitle;
    private int mWantedPosition;
//    protected long mCurrentPosition;
//    protected long mDuration;
//    protected boolean mIsPlaying;
    
    public MilinkMediaPlayerControl(MilinkClientManager manager) {
    	if (manager == null) {
    		throw new IllegalArgumentException("MilinkClientManager should not be null");
		}
    	mVideoManager = manager;
    }

//    public RemoteMediaPlayerControl(Context context, AirkanManagerNew manager) {
//        this.mContext = context;
//        this.mManager = manager;
//    }

    @Override
    public Uri getUri() {
    	if (mUri == null) {
			return null;
		}
        return Uri.parse(mUri);
    }

//    public void setMediaController(OriginMediaController mediaController) {
//        if (mMediaController != null) {
//            // mMediaController.hide();
//        }
//        this.mMediaController = mediaController;
//        if (mMediaController != null) {
//            mMediaController.show(0);
//            mMediaController.setEnabled(true);
//            mMediaController.setMediaPlayer(this);
//        }
//    }

//    public void setVideoManager(MilinkClientManager videoManager) {
//        this.mVideoManager = videoManager;
//    }

    @Override
    public void setVideoUri(String url, String title, int position) {
        Log.i(TAG, "setVideoURI title: " + title + ", url: " + url + ", pos: " + position);
        mUri = url;
        mTitle = title;
        mWantedPosition = position;
    }

    @Override
    public float getVolume() {
    	return mVideoManager.getVolume();
    }

    @Override
    public void setVolume(float volume) {
    	mVideoManager.setVolume((int) volume);
    }

    @Override
    public void startPlay() {
    	Log.d(TAG, "start play " + mUri);
    	mVideoManager.startPlay(mUri, mTitle, mWantedPosition, 0.0, MediaType.Video);
    }

    @Override
    public void playTo(String deviceName) {
    	mVideoManager.connect(deviceName, 3000);
    	mDeviceName = deviceName;
    }

    @Override
    public void takeBack() {
    	mVideoManager.disconnect();
    }

    @Override
    public void start() {
    	mVideoManager.setPlaybackRate(1);
    }

    @Override
    public void pause() {
    	mVideoManager.setPlaybackRate(0);
    }

    @Override
    public int getDuration() {
    	return mVideoManager.getPlaybackDuration();
    }

    @Override
    public int getCurrentPosition() {
    	return mVideoManager.getPlaybackProgress();
    }

    @Override
    public void seekTo(int pos) {
    	mVideoManager.setPlaybackProgress(pos);
    }

    public void stop() {
    	mVideoManager.stopPlay();
    }

    @Override
    public boolean isPlaying() {
    	return mVideoManager.getPlaybackRate() == 1;
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

//    public void updatePlayingState(boolean playing) {
//        if (mMediaController != null) {
//            mMediaController.updatePlayingState(playing);
//        }
//    }
//
//    public void showMediaController() {
//        if (mMediaController != null) {
//            mMediaController.show(0);
//        }
//    }

//    public void setVideoUri(String title, int position, long mediaID, int ci, int preferedSource,
//            String url) {
//        mUri = url;
//        mWantedPosition = position;
//        mTitle = title;
//    }

	@Override
	public void setDataSource(String uri) {
	}


	@Override
	public void setDataSource(String uri, Map<String, String> headers) {
	}


	@Override
	public void close() {
	}


	@Override
	public boolean canBuffering() {
		return false;
	}


	@Override
	public boolean isAdsPlaying() {
		return false;
	}


	@Override
	public boolean isAirkanEnable() {
		return true;
	}
	@Override
	public MediaInfo getMediaInfo() {
		return null;
	}
	@Override
	public boolean get3dMode() {
		return false;
	}
	@Override
	public void set3dMode(boolean mode) {
	}

    @Override
    public boolean isInPlaybackState() {
        return true;
    }

    @Override
    public int getRealPlayPosition() {
        return getCurrentPosition();
    }
}
