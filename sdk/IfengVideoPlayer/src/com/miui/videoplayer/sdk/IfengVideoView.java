/**
 * 
 */
package com.miui.videoplayer.sdk;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.ifeng.media.IfengMediaPlayer;
import com.ifeng.media.OnVideoEventListener;
import com.miui.videoplayer.videoview.DuoKanVideoView;

/**
 * @author tianli
 *
 */
public class IfengVideoView extends DuoKanVideoView {

    IfengMediaPlayer mIfengPlayer;
    
    public IfengVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public IfengVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IfengVideoView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        mMediaPlayer = createMediaPlayer();
        initMediaPlayer(mMediaPlayer);
        mIfengPlayer = new IfengMediaPlayer(getContext(), mMediaPlayer);
        mIfengPlayer.setOnVideoEventListener(mOnVideoEventListener);
    }

    @Override
    public void setDataSource(String uri) {
        setDataSource(uri, null);
    }

    @Override
    public void setDataSource(String uri, Map<String, String> headers) {
//        mIfengPlayer.setVideoGuid("0130c8bd-28ee-4902-aeab-d58b58b0f6b4", null, IfengMediaPlayer.STREAM_LEVEL_H);//tanmishike
    	mIfengPlayer.setVideoGuid(getVid(uri), null, getResolution(uri));
    }

    private String getVid(String uri){
		try {
			JSONObject json = new JSONObject(uri);
			return json.getString("vid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return uri;
	}
    
	private int getResolution(String uri){
		try {
			JSONObject json = new JSONObject(uri);
			return json.getInt("resolution");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return IfengMediaPlayer.STREAM_LEVEL_H;
	}	
	
    @Override
    protected void onSurfaceCreated(SurfaceHolder holder) {
        super.onSurfaceCreated(holder);
        mIfengPlayer.surfaceCreated(mSurfaceHolder);
    }

    @Override
    protected void onSurfaceChanged(SurfaceHolder holder, int format, int w,
            int h) {
        super.onSurfaceChanged(holder, format, w, h);
        mIfengPlayer.surfaceChanged(holder, format, w, h);
    }

    @Override
    protected void onSurfaceDestroyed(SurfaceHolder holder) {
        super.onSurfaceDestroyed(holder);
        mIfengPlayer.surfaceDestroyed(holder);
    }

    @Override
    public boolean canBuffering() {
        return true;
    }

    private OnVideoEventListener mOnVideoEventListener = new OnVideoEventListener() {
		@Override
		public void onVideoPreparedFailed(Throwable arg0) {
		}
		
		@Override
		public void onVInfoLoadSuccessed() {
		}
		
		@Override
		public void onVInfoLoadFailed(Throwable arg0) {
		}
	};
}
