package com.miui.videoplayer.views;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.media.audiofx.*;

import com.duokan.MediaPlayer;
import com.duokan.MediaPlayer.MediaInfo;
import com.duokan.MediaPlayer.OnBufferingUpdateListener;
import com.duokan.MediaPlayer.OnCompletionListener;
import com.duokan.MediaPlayer.OnErrorListener;
import com.duokan.MediaPlayer.OnInfoListener;
import com.duokan.MediaPlayer.OnSeekCompleteListener;
import com.duokan.MediaPlayer.OnTimedTextListener;
import com.duokan.MediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.DuoKanCodecConstants;
import com.miui.videoplayer.framework.popup.VpCtrlFunctionPopupWindow;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;

public class DuoKanCodecOriginVideoView extends SurfaceView implements LocalMediaPlayerControl, LocalVideoPlaySizeAdjustable{
		private String TAG = "DuoKanCodecOriginVideoView";
	    // settable by the client
	    private Uri         mUri;

		private Map<String, String> mHeaders = new HashMap<String, String>();
	    private int         mDuration;
	    
	    private String mUserAgent = "MiuiVideo/1.0";

	    // all possible internal states
	    private static final int STATE_ERROR              = -1;
	    private static final int STATE_IDLE               = 0;
	    private static final int STATE_PREPARING          = 1;
	    private static final int STATE_PREPARED           = 2;
	    private static final int STATE_PLAYING            = 3;
	    private static final int STATE_PAUSED             = 4;
	    private static final int STATE_PLAYBACK_COMPLETED = 5;

	    // mCurrentState is a VideoView object's current state.
	    // mTargetState is the state that a method caller intends to reach.
	    // For instance, regardless the VideoView object's current state,
	    // calling pause() intends to bring the object to a target state
	    // of STATE_PAUSED.
	    private int mCurrentState = STATE_IDLE;
	    private int mTargetState  = STATE_IDLE;

	    // All the stuff we need for playing and showing a video
	    private SurfaceHolder mSurfaceHolder = null;
	    private MediaPlayer mMediaPlayer = null;
	    private int         mVideoWidth;
	    private int         mVideoHeight;
	    private int         mSurfaceWidth;
	    private int         mSurfaceHeight;
//	    private TESTMediaController mMediaController;
	    private OnCompletionListener mOnCompletionListener;
	    private MediaPlayer.OnPreparedListener mOnPreparedListener;
	    private int         mCurrentBufferPercentage;
	    private OnErrorListener mOnErrorListener;
	    private int         mSeekWhenPrepared;  // recording the seek position while preparing
	    private boolean     mCanPause;
	    private boolean     mCanSeekBack;
	    private boolean     mCanSeekForward;

	    //custom part
	    private Context mContext;
		private DuoKanMediaController mDuoKanMediaController;

//	    private OnTimedTextListener mOnTimedTextListener;
	    private String mTimedTextSourceUri;
	    private OnSeekCompleteListener mOnSeekCompleteListener;
	    private OnInfoListener mOnInfoListener;
	    private OnBufferingUpdateListener mOnBufferingUpdateListener;
	    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
	    
	    private static final int NETWORK_1003_ERROR_RETRY_TIMES = 3;
	    private int mRemainRetryTimes = NETWORK_1003_ERROR_RETRY_TIMES;
	    
	    private long mTotalPausedTime = 0L;
	    private long mLastPausedTimeStamp = 0L;
	    
		public int getVideoWidth() {
			return mVideoWidth;
		}

		public int getVideoHeight() {
			return mVideoHeight;
		}

		//for adjust the size of videoview
	    private int mAdjustWidth;
	    

		private int mAdjustHeight;
	    private boolean mUserAdjustSize = false;
	    
	    
	    //end custom
	    public DuoKanCodecOriginVideoView(Context context) {
	        super(context);
	        initVideoView();
	        this.mContext = context;
	    }

	    public DuoKanCodecOriginVideoView(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	        initVideoView();
	        this.mContext = context;
	    }

	    public DuoKanCodecOriginVideoView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        initVideoView();
	        this.mContext = context;
	    }

	    
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//	        Log.i("@@@@", "onMeasure");
	        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
	        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
	        if (mVideoWidth > 0 && mVideoHeight > 0) {
	            if ( mVideoWidth * height  > width * mVideoHeight ) {
	                //Log.i("@@@", "image too tall, correcting");
	                height = width * mVideoHeight / mVideoWidth;
	            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
	                //Log.i("@@@", "image too wide, correcting");
	                width = height * mVideoWidth / mVideoHeight;
	            } else {
	                //Log.i("@@@", "aspect ratio is correct: " +
	                        //width+"/"+height+"="+
	                        //mVideoWidth+"/"+mVideoHeight);
	            }
	        }
	        
	        if (mUserAdjustSize) {
	        	width = mAdjustWidth;
	        	height = mAdjustHeight;
//	        	mUserAdjustSize = false;
	        }
	        
	        setMeasuredDimension(width, height);
	    }

	    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
	        int result = desiredSize;
	        int specMode = MeasureSpec.getMode(measureSpec);
	        int specSize =  MeasureSpec.getSize(measureSpec);

	        switch (specMode) {
	            case MeasureSpec.UNSPECIFIED:
	                /* Parent says we can be as big as we want. Just don't be larger
	                 * than max size imposed on ourselves.
	                 */
	                result = desiredSize;
	                break;

	            case MeasureSpec.AT_MOST:
	                /* Parent says we can be as big as we want, up to specSize.
	                 * Don't be larger than specSize, and don't be larger than
	                 * the max size imposed on ourselves.
	                 */
	                result = Math.min(desiredSize, specSize);
	                break;

	            case MeasureSpec.EXACTLY:
	                // No choice. Do what we are told.
	                result = specSize;
	                break;
	        }
	        return result;
	}

	    private void initVideoView() {
	        mVideoWidth = 0;
	        mVideoHeight = 0;
	        getHolder().addCallback(mSHCallback);
	        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        setFocusable(true);
	        setFocusableInTouchMode(true);
	        requestFocus();
	        mCurrentState = STATE_IDLE;
	        mTargetState  = STATE_IDLE;
	        WebView wv = new WebView(getContext());
	        mUserAgent = wv.getSettings().getUserAgentString();
	        mUserAgent += " MiuiVideo/1.0";
	    }

	    public void setVideoPath(String path) {
	        setVideoURI(Uri.parse(path));
	    }

	    public void setVideoURI(Uri uri) {
	    	Log.i(TAG, "SetVideoURI: " + uri);
	    	mRemainRetryTimes = NETWORK_1003_ERROR_RETRY_TIMES;
	        setVideoURI(uri, null);
	    }

	    /**
	     * @hide
	     */
	    public void setVideoURI(Uri uri, Map<String, String> headers) {
	    	if(headers != null){
	    		mHeaders = headers;
	    	}
	        mUri = uri;
	        mSeekWhenPrepared = 0;
	        openVideo();
	        requestLayout();
	        invalidate();
	    }

	    public void stopPlayback() {
	        if (mMediaPlayer != null) {
	            mMediaPlayer.stop();
	            mMediaPlayer.release();
	            mMediaPlayer = null;
	            mCurrentState = STATE_IDLE;
	            mTargetState  = STATE_IDLE;
	        }
	    }

	    private void openVideo() {
//	    	Log.e("OPEN VIDEO !!!!!!!!!!!!!!!!!!!", "OPEN VIDEO!!!!!!!!!!!!!!: " + mUri + mSurfaceHolder);
	        if (mUri == null || mSurfaceHolder == null) {
	            // not ready for playback just yet, will try again later
	            return;
	        }
	        // Tell the music playback service to pause
	        // TODO: these constants need to be published somewhere in the framework.
	        Intent i = new Intent("com.android.music.musicservicecommand");
	        i.putExtra("command", "pause");
	        mContext.sendBroadcast(i);

	        // we shouldn't clear the target state, because somebody might have
	        // called start() previously
	        release(false);

            mDuoKanMediaController.checkNetwork(mUri);

	        try {
	            mMediaPlayer = new MediaPlayer();
	            mMediaPlayer.setOnPreparedListener(mPreparedListener);
	            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
	            mDuration = -1;
	            mMediaPlayer.setOnCompletionListener(mCompletionListener);
	            mMediaPlayer.setOnErrorListener(mErrorListener);
	            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
	            mCurrentBufferPercentage = 0;
//	            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
	            //custom
	            if (mOnSeekCompleteListener != null) {
	            	 mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
	            }
	           
	            if (mOnInfoListener != null) {
	            	mMediaPlayer.setOnInfoListener(mOnInfoListener);
	            }
	            
//	            if (mOnTimedTextListener != null && mTimedTextSourceUri != null) {
//	                mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
//	                mMediaPlayer.setOutOfBandTextSource(mTimedTextSourceUri);
//	            }
	            //end custom
	            //end custom
	            Log.i(TAG, "setDataSource: " + mUri);
	            mHeaders.put("user-agent", mUserAgent);
	            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
	            mMediaPlayer.setDisplay(mSurfaceHolder);
//	            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	            mMediaPlayer.setScreenOnWhilePlaying(true);
	            mMediaPlayer.prepareAsync();
	            // we don't set the target state here either, but preserve the
	            // target state that was there before.
	            mCurrentState = STATE_PREPARING;
//	            attachMediaController();
	        } catch (IOException ex) {
	            Log.w(TAG, "Unable to open content: " + mUri, ex);
	            mCurrentState = STATE_ERROR;
	            mTargetState = STATE_ERROR;
	            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
	            return;
	        } catch (IllegalArgumentException ex) {
	            Log.w(TAG, "Unable to open content: " + mUri, ex);
	            mCurrentState = STATE_ERROR;
	            mTargetState = STATE_ERROR;
	            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
	            return;
	        }
	    }

	    public void setDuokanMediaController(DuoKanMediaController controller) {
	    	mDuoKanMediaController = controller;
	    }

	    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
	        new MediaPlayer.OnVideoSizeChangedListener() {
	            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	                mVideoWidth = mp.getVideoWidth();
	                mVideoHeight = mp.getVideoHeight();
	                if (mVideoWidth != 0 && mVideoHeight != 0) {
	                    getHolder().setFixedSize(mVideoWidth, ((mVideoHeight == 1088) ? 1080 : mVideoHeight));
	                }
	                if (mOnVideoSizeChangedListener != null) {
	                	mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height);
	                }
//	                Log.e(TAG, "mSizeChangedListener: " + mVideoWidth + "x" + mVideoHeight);
	            }
	    };

	    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
	        public void onPrepared(MediaPlayer mp) {
	            mTotalPausedTime = 0;
	            mLastPausedTimeStamp = 0;
	            
	        	mCurrentState = STATE_PREPARED;
	            
	            mCanPause = mCanSeekBack = mCanSeekForward = true;


	            if (mOnPreparedListener != null) {
	                mOnPreparedListener.onPrepared(mMediaPlayer);
	            }

	            mVideoWidth = mp.getVideoWidth();
	            mVideoHeight = mp.getVideoHeight();

	            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call

	            if (mVideoWidth != 0 && mVideoHeight != 0) {
	                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
	                getHolder().setFixedSize(mVideoWidth, ((mVideoHeight == 1088) ? 1080 : mVideoHeight));
	                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
	                    // We didn't actually change the size (it was already at the size
	                    // we need), so we won't get a "surface changed" callback, so
	                    // start the video here instead of in the callback.
	                    if (mTargetState == STATE_PLAYING) {
//	                        start();
//	                        if (mMediaController != null) {
//	                            mMediaController.show();
//	                        }
	                    } else if (!isPlaying() &&
	                               (seekToPosition != 0 || getCurrentPosition() > 0)) {
//	                       if (mMediaController != null) {
//	                           // Show the media controls when we're paused into a video and make 'em stick.
//	                           mMediaController.show(0);
//	                       }
	                   }
	                }
	            } else {
	                // We don't know the video size yet, but should start anyway.
	                // The video size might be reported to us later.
	                if (mTargetState == STATE_PLAYING) {
//	                    start();
	                }
	            }
	        }
	    };

	    private MediaPlayer.OnCompletionListener mCompletionListener =
	        new MediaPlayer.OnCompletionListener() {
	        public void onCompletion(MediaPlayer mp) {
	            mCurrentState = STATE_PLAYBACK_COMPLETED;
	            mTargetState = STATE_PLAYBACK_COMPLETED;
//	            if (mMediaController != null) {
//	                mMediaController.hide();
//	            }
	            if (mOnCompletionListener != null) {
	                mOnCompletionListener.onCompletion(mMediaPlayer);
	            }
	        }
	    };

	    private MediaPlayer.OnErrorListener mErrorListener =
	        new MediaPlayer.OnErrorListener() {
	        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
	            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
//	            Log.e(TAG, "mRemainRetryTimes: " + mRemainRetryTimes);
	            if (impl_err == -1003 && AndroidUtils.isOnlineVideo(mUri) && mRemainRetryTimes > 0) {
	            	Log.e(TAG, "-1003 error! re-try url: " + mUri + " leave times: " + mRemainRetryTimes);
	            	DuoKanCodecOriginVideoView.this.setVisibility(View.INVISIBLE);
	            	DuoKanCodecOriginVideoView.this.setVisibility(View.VISIBLE);
	            	mRemainRetryTimes--;
	            	return true;
	            }
	            mCurrentState = STATE_ERROR;
	            mTargetState = STATE_ERROR;
//	            if (mMediaController != null) {
//	                mMediaController.hide();
//	            }

	            /* If an error handler has been supplied, use it and finish. */
	            if (mOnErrorListener != null) {
	                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
	                    return true;
	                }
	            }

	            return true;
	        }
	    };

	    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
	        new MediaPlayer.OnBufferingUpdateListener() {
	        public void onBufferingUpdate(MediaPlayer mp, int percent) {
	            mCurrentBufferPercentage = percent;
	            if (mOnBufferingUpdateListener != null) {
	            	mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
	            }
	        }
	    };
	    
	    /**
	     * Register a callback to be invoked when the media file
	     * is loaded and ready to go.
	     *
	     * @param l The callback that will be run
	     */
	    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
	    {
	        mOnPreparedListener = l;
	    }

	    /**
	     * Register a callback to be invoked when the end of a media file
	     * has been reached during playback.
	     *
	     * @param l The callback that will be run
	     */
	    public void setOnCompletionListener(OnCompletionListener l)
	    {
	        mOnCompletionListener = l;
	    }

	    /**
	     * Register a callback to be invoked when an error occurs
	     * during playback or setup.  If no listener is specified,
	     * or if the listener returned false, VideoView will inform
	     * the user of any errors.
	     *
	     * @param l The callback that will be run
	     */
	    public void setOnErrorListener(OnErrorListener l)
	    {
	        mOnErrorListener = l;
	    }

	    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
	    {
	        public void surfaceChanged(SurfaceHolder holder, int format,
	                                    int w, int h)
	        {
	        	Log.i(TAG, "surfaceChanged!!!");
	            mSurfaceWidth = w;
	            mSurfaceHeight = h;
	            boolean isValidState =  (mTargetState == STATE_PLAYING);
	            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
//	            if (mMediaPlayer != null && isValidState && hasValidSize) {
//	                if (mSeekWhenPrepared != 0) {
//	                    seekTo(mSeekWhenPrepared);
//	                }
//	                start();
//	            }
	        }

	        public void surfaceCreated(SurfaceHolder holder)
	        {
	        	Log.i(TAG, "surfaceCreated");
	            mSurfaceHolder = holder;
	            openVideo();
	        }

	        public void surfaceDestroyed(SurfaceHolder holder)
	        {
	        	Log.i(TAG, "surface destroyed!!!");
	            // after we return from this we can't use the surface any more
	            mSurfaceHolder = null;
//	            if (mMediaController != null)  {
//	            	mMediaController.hide();
//	            }
	            release(true);
	        }
	    };

	    /*
	     * release the media player in any state
	     */
	    private void release(boolean cleartargetstate) {
	        if (mMediaPlayer != null) {
	            mMediaPlayer.reset();
	            mMediaPlayer.release();
	            mMediaPlayer = null;
	            mCurrentState = STATE_IDLE;
	            if (cleartargetstate) {
	                mTargetState  = STATE_IDLE;
	            }
				if (DuoKanCodecConstants.sUseDiracSound) {
					try {
						Class DiracSound = Class.forName("android.media.audiofx.DiracSound");
                        Log.i("EffectDiracSound", "class android.media.audiofx.DiracSound found!");
						Object diracSound = DiracSound.getDeclaredConstructor(new Class[] {int.class, int.class})
						.newInstance(new Object[] {new Integer(0), new Integer(0)});

						Method method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
						method.invoke(diracSound, new Object[] {new Integer(0)});

						method = DiracSound.getDeclaredMethod("setMode", new Class[] {int.class});
						Field field = DiracSound.getDeclaredField("DIRACSOUND_MODE_MUSIC");
						int diracMode = field.getInt(diracSound);
						method.invoke(diracSound, new Object[] {new Integer(diracMode)});

		            	Log.i("EffectDiracSound", "diable sound effect,and set music mode ");
						method = DiracSound.getSuperclass().getDeclaredMethod("release", new Class[]{});
						method.invoke(diracSound, new Object[]{});	
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	        }
	    }

	    public void start() {
	        if (isInPlaybackState()) {
				if (DuoKanCodecConstants.sUseDiracSound) {
					try {
						Class DiracSound = Class.forName("android.media.audiofx.DiracSound");
                        Log.i("EffectDiracSound", "class android.media.audiofx.DiracSound found!");
						Object diracSound = DiracSound.getDeclaredConstructor(new Class[] {int.class, int.class}).newInstance(new Object[] {new Integer(0), new Integer(0)});

		        		boolean isAudioEnhance = VpCtrlFunctionPopupWindow.IS_AUDIO_EFFECT_ENHANCE;

						Method method = DiracSound.getDeclaredMethod("setMode", new Class[] {int.class});
						Field field = DiracSound.getDeclaredField("DIRACSOUND_MODE_MOVIE");
						int diracMode = field.getInt(diracSound);
						method.invoke(diracSound, new Object[] {new Integer(diracMode)});
 	 
		    			Log.i("EffectDiracSound", "set movie mode in videoplayer");
		    			if(isAudioEnhance){
							method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
							method.invoke(diracSound, new Object[] {new Integer(1)});
		    				Log.i("EffectDiracSound", "enable sound effect in videoplayer");
		    			} else {
							method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
							method.invoke(diracSound, new Object[] {new Integer(0)});
		    				Log.i("EffectDiracSound", "disable sound effect in videoplayer");
		    			}

						method = DiracSound.getSuperclass().getDeclaredMethod("release", new Class[]{});
						method.invoke(diracSound, new Object[]{});	
					} catch (Exception e) {
						Log.e("EffectDiracSound", "android.media.audiofx.DiracSound not found!!!");
						e.printStackTrace();
					}
				}
	            mMediaPlayer.start();
	            if (mDuoKanMediaController != null) {
	            	mDuoKanMediaController.updatePlayingState(true);
	            }
	            VideoPlayerActivity.isVideoPaused = false;
	            VideoPlayerActivity.isVideoComplete = false;
	            mCurrentState = STATE_PLAYING;
	            if (mLastPausedTimeStamp != 0) {
	            	mTotalPausedTime = mTotalPausedTime + (System.currentTimeMillis() - mLastPausedTimeStamp);
	            	mLastPausedTimeStamp = 0;
	            }
	        }
	        mTargetState = STATE_PLAYING;
	    }

	    public void pause() {
	        if (isInPlaybackState()) {
	            if (mMediaPlayer.isPlaying()) {
	                mMediaPlayer.pause();
	                mCurrentState = STATE_PAUSED;	                
	                mLastPausedTimeStamp = System.currentTimeMillis();
					if (DuoKanCodecConstants.sUseDiracSound) {
						try {
							Class DiracSound = Class.forName("android.media.audiofx.DiracSound");
							Object diracSound = DiracSound.getDeclaredConstructor(new Class[] {int.class, int.class})
							.newInstance(new Object[] {new Integer(0), new Integer(0)});

							Method method = DiracSound.getDeclaredMethod("setMovie", new Class[] {int.class});
							method.invoke(diracSound, new Object[] {new Integer(0)});

							method = DiracSound.getDeclaredMethod("setMode", new Class[] {int.class});
							Field field = DiracSound.getDeclaredField("DIRACSOUND_MODE_MUSIC");
							int diracMode = field.getInt(diracSound);
							method.invoke(diracSound, new Object[] {new Integer(diracMode)});

			            	Log.i("EffectDiracSound", "diable sound effect,and set music mode ");
							method = DiracSound.getSuperclass().getDeclaredMethod("release", new Class[]{});
							method.invoke(diracSound, new Object[]{});		
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	            }
	        }
	        mTargetState = STATE_PAUSED;
	    }

	    public void suspend() {
	        release(false);
	    }

	    public void resume() {
	        openVideo();
	    }

	    // cache duration as mDuration for faster access
	    public int getDuration() {
	        if (isInPlaybackState()) {
	            if (mDuration > 0) {
	                return mDuration;
	            }
	            mDuration = mMediaPlayer.getDuration();
	            return mDuration;
	        }
	        mDuration = -1;
	        return mDuration;
	    }

	    public int getCurrentPosition() {
	        if (isInPlaybackState()) {
	            return mMediaPlayer.getCurrentPosition();
	        }
	        return 0;
	    }

	    public void seekTo(int msec) {
	        if (isInPlaybackState()) {
	            mMediaPlayer.seekTo(msec);
	            mSeekWhenPrepared = 0;
	        } else {
	            mSeekWhenPrepared = msec;
	        }
	    }

	    public boolean isPlaying() {
	        return isInPlaybackState() && mMediaPlayer.isPlaying();
	    }

	    public int getBufferPercentage() {
	        if (mMediaPlayer != null) {
	            return mCurrentBufferPercentage;
	        }
	        return 0;
	    }

	    private boolean isInPlaybackState() {
	        return (mMediaPlayer != null &&
	                mCurrentState != STATE_ERROR &&
	                mCurrentState != STATE_IDLE &&
	                mCurrentState != STATE_PREPARING);
	    }

	    public boolean canPause() {
	        return mCanPause;
	    }

	    public boolean canSeekBackward() {
	        return mCanSeekBack;
	    }

	    public boolean canSeekForward() {
	        return mCanSeekForward;
	    }
	    
	    public boolean isPaused() {
	    	return mCurrentState == STATE_PAUSED;
	    }

		public void adjustVideoPlayViewSize(int width, int height, boolean auto) {
			LayoutParams lp = new android.widget.FrameLayout.LayoutParams(width,  height);
			lp.gravity = Gravity.CENTER;
			this.setLayoutParams(lp);
			
			mUserAdjustSize = !auto;
			mAdjustWidth = width;
			mAdjustHeight = height;
			getHolder().setFixedSize(width, height);
//			 mMediaPlayer.setDisplay(getHolder());
		}

		@Override
		public MediaInfo getMediaInfo() {
			return mMediaPlayer.getMediaInfo();
		}

		@Override
		public void setOnTimedTextListener(OnTimedTextListener listener) {
//			mOnTimedTextListener = listener;
		}

		@Override
		public boolean setOutOfBandTextSource(String sourceUri) {
			this.mTimedTextSourceUri = sourceUri;
			return false;
		}

		@Override
		public void setOnInfoListener(OnInfoListener listener) {
			mOnInfoListener = listener;
		}

		@Override
		public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
			mOnSeekCompleteListener = listener;
		}

		public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
			this.mOnBufferingUpdateListener = onBufferingUpdateListener;
		}
		@Override
		public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
			this.mOnVideoSizeChangedListener = onVideoSizeChangedListener;
		}
		
		@Override
		public void stopLocalPlayForMediaSwitch() {
		}

		@Override
		public void startLocalPlayForMediaSwitch(Uri uri) {
		}


		@Override
		public void stopLocalPlayForAirkan() {
		}

		@Override
		public void startLocalPlayForAirkan(Uri videoUri) {
			
		}
		public int getAdjustWidth() {
			return mAdjustWidth;
		}

		public int getAdjustHeight() {
			return mAdjustHeight;
		}

		@Override
		public boolean enableMultiSpeedPlayback(int speed, boolean forward) {
			return mMediaPlayer.enableMultiSpeedPlayback(speed, forward);
		}

		@Override
		public boolean disableMultiSpeedPlayback() {
			return mMediaPlayer.disableMultiSpeedPlayback();
		}

		
		//Origin codec listener, ignore
		@Override
		public void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener l) {
		}
		@Override
		public void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener l) {
		}
		@Override
		public void setOnErrorListener(android.media.MediaPlayer.OnErrorListener l) {
		}
		@Override
		public void setOnSeekCompleteListener(android.media.MediaPlayer.OnSeekCompleteListener listener) {
		}
		@Override
		public void setOnInfoListener(android.media.MediaPlayer.OnInfoListener listener) {
		}
		@Override
		public void setOnBufferingUpdateListener(android.media.MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
		}
		@Override
		public void setOnVideoSizeChangedListener(android.media.MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener) {
		}

		@Override
		public long getPausedTotalTime() {
			return mTotalPausedTime;
		}

		public void set3dMode(boolean mode) {
			if (mode) {
				adjustVideoPlayViewSize(mVideoWidth, ((mVideoHeight == 1088) ? 1080 : mVideoHeight), false);
			} else {
				int screenWidth = DisplayInformationFetcher.getInstance(mContext).getScreenWidth();
				int screenHeight = DisplayInformationFetcher.getInstance(mContext).getScreenHeight();
				adjustVideoPlayViewSize(screenWidth, screenHeight, false);
			}
			mMediaPlayer.set3dMode(mode);
		}
		
		public boolean get3dMode() {
			return mMediaPlayer.get3dMode();
		}
}
