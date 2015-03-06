package com.duokan;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

public class MediaPlayer {
	
	/**
	   Constant to retrieve only the new metadata since the last
	   call.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean METADATA_UPDATE_ONLY = true;
	
	/**
	   Constant to retrieve all the metadata.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean METADATA_ALL = false;
	
	/**
	   Constant to enable the metadata filter during retrieval.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean APPLY_METADATA_FILTER = true;
	
	/**
	   Constant to disable the metadata filter during retrieval.
	   // FIXME: unhide.
	   // FIXME: add link to getMetadata(boolean, boolean)
	   {@hide}
	 */
	public static final boolean BYPASS_METADATA_FILTER = false;
	
	public static class MediaInfo {
		public int duration;		
		public int videoHeight;
		public int videoWidth;		
		public int videoCodecId;
		public String videoCodecName;
		public String videoCodecProfile;
		public float videoFrameRate;
		public int fpaType;
		
		public int audioCodecId;
		public String audioCodecName;
		public int audioSampleRate;
		
		public MediaInfo() {			
		}
	}
	
	public class AudioTrackInfo {
		private List<String> mAudioTrackList;
		private int mSelected;
		
		public AudioTrackInfo() {
			mAudioTrackList = new ArrayList<String>();
			mSelected = -1;
		}
		
		public void addAudioTrack(String audio) {
			mAudioTrackList.add(audio);
		}
		
		public void setSelectedAudioTrack(int index) {
			mSelected = index;
		}
		
		public List<String> getAudioTrack() {
			return mAudioTrackList;
		}
		
		public int getSelectedAudioTrack() {
			return mSelected;
		}
	}
 
	static {
		System.loadLibrary("xiaomimediaplayer");
		System.loadLibrary("xiaomiplayerwrapper");
	}
			
	private final static String TAG = "XiaomiMediaPlayer";
	
    private long mNativeContext; // accessed by native methods
    private long mNativeSurfaceTexture;  // accessed by native methods
    private long mListenerContext; // accessed by native methods
    private SurfaceHolder  mSurfaceHolder;
    private EventHandler mEventHandler;
    private PowerManager.WakeLock mWakeLock = null;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;        
	
	public MediaPlayer() {
		Log.i(TAG, "DkMediaPlayer");
		
		Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }		
		
		native_init();
		native_setup(new WeakReference<MediaPlayer>(this));
	}

    public static MediaPlayer create(Context context, Uri uri) {
        return create (context, uri, null);
    }

    public static MediaPlayer create(Context context, Uri uri, SurfaceHolder holder) {
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(context, uri);
            if (holder != null) {
                mp.setDisplay(holder);
            }
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }

        return null;
    }

    public static MediaPlayer create(Context context, int resid) {
//        try {
//            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
//            if (afd == null) return null;
//
//            MediaPlayer mp = new MediaPlayer();
//            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//            afd.close();
//            mp.prepare();
//            return mp;
//        } catch (IOException ex) {
//            Log.d(TAG, "create failed:", ex);
//            // fall through
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "create failed:", ex);
//           // fall through
//        } catch (SecurityException ex) {
//            Log.d(TAG, "create failed:", ex);
//            // fall through
//        }
        return null;
    }
    
    private String getRealFilePathFromContentUri(Context context, Uri contentUri) {
    	String[] columns = new String[]{MediaStore.Video.Media.DATA};
    	Cursor cursor = null;
        try {
    	    cursor = context.getContentResolver().query(contentUri, columns, null, null, null);
    	    if (cursor == null) {
    		    return null;
    	    }
    	    int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
    	    cursor.moveToFirst();
    	    String result = cursor.getString(index);
    	    cursor.close();
    	    return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return null;
        }
    }    
    
    public void setDataSource(Context context, Uri uri)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
    	
        setDataSource(context, uri, null);
    }
    
    public void setDataSource(Context context, Uri uri, Map<String, String> headers)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
    	
        String scheme = uri.getScheme();
        if(scheme == null || scheme.equals("file")) {        	
        	native_setdatasource(uri.getPath());
        }
        else if (scheme.equals("content")) {        	
            String realPath = getRealFilePathFromContentUri(context, uri);
            if (realPath == null) {
                throw new IllegalArgumentException("Can not get real path from content uri!!!");
            }
        	native_setdatasource(realPath);
        }
        else { 	
        	native_setdatasource(uri.toString(), headers);
        }
    }    
	
	public void setdatasource(String path) {
		Log.i(TAG, "native_setdatasource");
		native_setdatasource(path);
	}	
	
	public void setdatasource(String path, Map<String, String> headers) {
		Log.i(TAG, "native_setdatasource");
		native_setdatasource(path, headers);
	}
	
    /**
     * Sets the {@link SurfaceHolder} to use for displaying the video
     * portion of the media.
     *
     * Either a surface holder or surface must be set if a display or video sink
     * is needed.  Not calling this method or {@link #setSurface(Surface)}
     * when playing back a video will result in only the audio track being played.
     * A null surface holder or surface will result in only the audio track being
     * played.
     *
     * @param sh the SurfaceHolder to use for video display
     */	
    public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder = sh;
        Surface surface;
        if (sh != null) {
        	surface = sh.getSurface();
        } else {
        	surface = null;
        }
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }
    
    /**
    */	
    public void set3dMode(boolean mode) {
        _set3dMode(null, mode);
        updateSurfaceScreenOn();
    }
    
    /**
     * Sets the {@link Surface} to be used as the sink for the video portion of
     * the media. This is similar to {@link #setDisplay(SurfaceHolder)}, but
     * does not support {@link #setScreenOnWhilePlaying(boolean)}.  Setting a
     * Surface will un-set any Surface or SurfaceHolder that was previously set.
     * A null surface will result in only the audio track being played.
     *
     * If the Surface sends frames to a {@link SurfaceTexture}, the timestamps
     * returned from {@link SurfaceTexture#getTimestamp()} will have an
     * unspecified zero point.  These timestamps cannot be directly compared
     * between different media sources, different instances of the same media
     * source, or multiple runs of the same program.  The timestamp is normally
     * monotonically increasing and is unaffected by time-of-day adjustments,
     * but it is reset when the position is set.
     *
     * @param surface The {@link Surface} to be used for the video portion of
     * the media.
     */
    public void setSurface(Surface surface) {
        if (mScreenOnWhilePlaying && surface != null) {
            Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        mSurfaceHolder = null;
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }
        
    private void updateSurfaceScreenOn() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }    
    
	public void prepare () throws IllegalStateException {
		Log.i(TAG, "native_prepare");
		native_prepare();
	}

	public void prepareAsync() throws IllegalStateException {
		Log.i(TAG, "native_prepare_async");
		native_prepare_async();
	}
	
	public void start() throws IllegalStateException {
		Log.i(TAG, "native_start");	
		stayAwake(true);
		native_start();
	}

	public void stop() throws IllegalStateException {
		Log.i(TAG, "native_stop");
		stayAwake(false);
		native_stop();
	}
	
	public void pause() throws IllegalStateException {
		Log.i(TAG, "native_pause");
		stayAwake(false);
		native_pause();
	}
	
	public void release() {
		Log.i(TAG, "native_release");
		stayAwake(false);
		updateSurfaceScreenOn();
        mOnPreparedListener = null;
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnSeekCompleteListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
        mOnVideoSizeChangedListener = null;
        mOnTimedTextListener = null;
		native_release();
	}

    /**
     * Returns the width of the video.
     *
     * @return the width of the video, or 0 if there is no video,
     * no display surface was set, or the width has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
     * to provide a notification when the width is available.
     */
    public native int getVideoWidth();

    /**
     * Returns the height of the video.
     *
     * @return the height of the video, or 0 if there is no video,
     * no display surface was set, or the height has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
     * to provide a notification when the height is available.
     */
    public native int getVideoHeight();

    /**
     * Checks whether the MediaPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     */
    public native boolean isPlaying();
    
    /**
     * Sets the player to be looping or non-looping.
     *
     * @param looping whether to loop or not
     */
    public native void setLooping(boolean looping);

    /**
     * Checks whether the MediaPlayer is looping or non-looping.
     *
     * @return true if the MediaPlayer is currently looping, false otherwise
     */
    public native boolean isLooping();

    /**
     * Checks whether the MediaPlayer is in 3d or non-3d.
     *
     * @return true if the MediaPlayer is currently in 3d mode, false otherwise
     */
    public native boolean get3dMode();

    
    /**
     * Sets the volume on this player.
     * This API is recommended for balancing the output of audio streams
     * within an application. Unless you are writing an application to
     * control user settings, this API should be used in preference to
     * {@link AudioManager#setStreamVolume(int, int, int)} which sets the volume of ALL streams of
     * a particular type. Note that the passed volume values are raw scalars.
     * UI controls should be scaled logarithmically.
     *
     * @param leftVolume left volume scalar
     * @param rightVolume right volume scalar
     */
    public native void setVolume(float leftVolume, float rightVolume);    
    
    /**
     * Seeks to specified time position.
     *
     * @param msec the offset in milliseconds from the start to seek to
     * @throws IllegalStateException if the internal player engine has not been
     * initialized
     */	
	public void seekTo(int to_ms) throws IllegalStateException {
		Log.i(TAG, "native_seekto");
		native_seekto(to_ms);
	}
	
    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */	
	public int getCurrentPosition() throws IllegalStateException {
		Log.i(TAG, "native_getcurrenttime");
		return native_getcurrenttime();
	}

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds
     */	
	public int getDuration() throws IllegalStateException {
		Log.i(TAG, "native_getduration");
		return native_getduration();
	}
	
    /**
     * Resets the MediaPlayer to its uninitialized state. After calling
     * this method, you will have to initialize it again by setting the
     * data source and calling prepare().
     */
    public void reset() {
        stayAwake(false);
        native_reset();
        // make sure none of the listeners get called anymore
        mEventHandler.removeCallbacksAndMessages(null);
    }
    
    /**
     * Gets the media metadata.
     *
     * @param update_only controls whether the full set of available
     * metadata is returned or just the set that changed since the
     * last call. See {@see #METADATA_UPDATE_ONLY} and {@see
     * #METADATA_ALL}.
     *
     * @param apply_filter if true only metadata that matches the
     * filter is returned. See {@see #APPLY_METADATA_FILTER} and {@see
     * #BYPASS_METADATA_FILTER}.
     *
     * @return The metadata, possibly empty. null if an error occured.
     // FIXME: unhide.
     * {@hide}
     */
    public Metadata getMetadata(final boolean update_only,
                                final boolean apply_filter) {
        Parcel reply = Parcel.obtain();
        Metadata data = new Metadata();

        if (!native_getMetadata(update_only, apply_filter, reply)) {
            reply.recycle();
            return null;
        }

        // Metadata takes over the parcel, don't recycle it unless
        // there is an error.
        if (!data.parse(reply)) {
            reply.recycle();
            return null;
        }
        return data;
    }
	
    /**
     * Interface definition for a callback to be invoked when the media
     * source is ready for playback.
     */
    public interface OnPreparedListener
    {
        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        void onPrepared(MediaPlayer mp);
    }

    /**
     * Register a callback to be invoked when the media source is ready
     * for playback.
     *
     * @param listener the callback that will be run
     */
    public void setOnPreparedListener(OnPreparedListener listener)
    {
        mOnPreparedListener = listener;
    }

    private OnPreparedListener mOnPreparedListener;

    /**
     * Interface definition for a callback to be invoked when playback of
     * a media source has completed.
     */
    public interface OnCompletionListener
    {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        void onCompletion(MediaPlayer mp);
    }

    /**
     * Register a callback to be invoked when the end of a media source
     * has been reached during playback.
     *
     * @param listener the callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener listener)
    {
        mOnCompletionListener = listener;
    }

    private OnCompletionListener mOnCompletionListener;

    /**
     * Interface definition of a callback to be invoked indicating buffering
     * status of a media resource being streamed over the network.
     */
    public interface OnBufferingUpdateListener
    {
        /**
         * Called to update status in buffering a media stream received through
         * progressive HTTP download. The received buffering percentage
         * indicates how much of the content has been buffered or played.
         * For example a buffering update of 80 percent when half the content
         * has already been played indicates that the next 30 percent of the
         * content to play has been buffered.
         *
         * @param mp      the MediaPlayer the update pertains to
         * @param percent the percentage (0-100) of the content
         *                that has been buffered or played thus far
         */
        void onBufferingUpdate(MediaPlayer mp, int percent);
    }

    /**
     * Register a callback to be invoked when the status of a network
     * stream's buffer has changed.
     *
     * @param listener the callback that will be run.
     */
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener)
    {
        mOnBufferingUpdateListener = listener;
    }

    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    /**
     * Interface definition of a callback to be invoked indicating
     * the completion of a seek operation.
     */
    public interface OnSeekCompleteListener
    {
        /**
         * Called to indicate the completion of a seek operation.
         *
         * @param mp the MediaPlayer that issued the seek operation
         */
        public void onSeekComplete(MediaPlayer mp);
    }

    /**
     * Register a callback to be invoked when a seek operation has been
     * completed.
     *
     * @param listener the callback that will be run
     */
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener)
    {
        mOnSeekCompleteListener = listener;
    }

    private OnSeekCompleteListener mOnSeekCompleteListener;

    /**
     * Interface definition of a callback to be invoked when the
     * video size is first known or updated
     */
    public interface OnVideoSizeChangedListener
    {
        /**
         * Called to indicate the video size
         *
         * @param mp        the MediaPlayer associated with this callback
         * @param width     the width of the video
         * @param height    the height of the video
         */
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height);
    }

    /**
     * Register a callback to be invoked when the video size is
     * known or updated.
     *
     * @param listener the callback that will be run
     */
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener)
    {
        mOnVideoSizeChangedListener = listener;
    }

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    
    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /** Unspecified media player error.
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_UNKNOWN = 1;

    /** Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_SERVER_DIED = 100;

    /** The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    /**
     * Interface definition of a callback to be invoked when there
     * has been an error during an asynchronous operation (other errors
     * will throw exceptions at method call time).
     */
    public interface OnErrorListener
    {
        /**
         * Called to indicate an error.
         *
         * @param mp      the MediaPlayer the error pertains to
         * @param what    the type of error that has occurred:
         * <ul>
         * <li>{@link #MEDIA_ERROR_UNKNOWN}
         * <li>{@link #MEDIA_ERROR_SERVER_DIED}
         * </ul>
         * @param extra an extra code, specific to the error. Typically
         * implementation dependant.
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the OnCompletionListener to be called.
         */
        boolean onError(MediaPlayer mp, int what, int extra);
    }

    /**
     * Register a callback to be invoked when an error has happened
     * during an asynchronous operation.
     *
     * @param listener the callback that will be run
     */
    public void setOnErrorListener(OnErrorListener listener)
    {
        mOnErrorListener = listener;
    }

    private OnErrorListener mOnErrorListener;
    
    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /** Unspecified media player info.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_UNKNOWN = 1;
    
    /** A new set of metadata is available.
     * @see android.media.MediaPlayer.OnInfoListener
     */    
    public static final int MEDIA_INFO_HARDWARE_DECODER = 601;
    
    /** A new set of metadata is available.
     * @see android.media.MediaPlayer.OnInfoListener
     */    
    public static final int MEDIA_INFO_SOFTWARE_DECODER = 602;    

    /** The video is too complex for the decoder: it can't decode frames fast
     *  enough. Possibly only the audio plays fine at this stage.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;

    /** MediaPlayer is temporarily pausing playback internally in order to
     * buffer more data.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    /** MediaPlayer is resuming playback after filling buffers.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    
    /** MediaPlayer reports it's buffer percentage
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_UPDATE = 770;    

    /** Bad interleaving means that a media has been improperly interleaved or
     * not interleaved at all, e.g has all the video samples first then all the
     * audio ones. Video is playing but a lot of disk seeks may be happening.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;

    /** The media cannot be seeked (e.g live stream)
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;

    /** A new set of metadata is available.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;

    /**
     * Interface definition of a callback to be invoked to communicate some
     * info and/or warning about the media or its playback.
     */
    public interface OnInfoListener
    {
        /**
         * Called to indicate an info or a warning.
         *
         * @param mp      the MediaPlayer the info pertains to.
         * @param what    the type of info or warning.
         * <ul>
         * <li>{@link #MEDIA_INFO_UNKNOWN}
         * <li>{@link #MEDIA_INFO_VIDEO_TRACK_LAGGING}
         * <li>{@link #MEDIA_INFO_BUFFERING_START}
         * <li>{@link #MEDIA_INFO_BUFFERING_END}
         * <li>{@link #MEDIA_INFO_BAD_INTERLEAVING}
         * <li>{@link #MEDIA_INFO_NOT_SEEKABLE}
         * <li>{@link #MEDIA_INFO_METADATA_UPDATE}
         * <li>{@link #MEDIA_INFO_HARDWARE_DECODER}
         * <li>{@link #MEDIA_INFO_SOFTWARE_DECODER}
         * </ul>
         * @param extra an extra code, specific to the info. Typically
         * implementation dependant.
         * @return True if the method handled the info, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the info to be discarded.
         */
        boolean onInfo(MediaPlayer mp, int what, int extra);
    }

    /**
     * Register a callback to be invoked when an info/warning is available.
     *
     * @param listener the callback that will be run
     */
    public void setOnInfoListener(OnInfoListener listener)
    {
        mOnInfoListener = listener;
    }

    private OnInfoListener mOnInfoListener;
    
    
    /**
     * Interface definition of a callback to be invoked when a
     * timed text is available for display.
     * {@hide}
     */
    public interface OnTimedTextListener
    {
        /**
         * Called to indicate an avaliable timed text
         *
         * @param mp             the MediaPlayer associated with this callback
         * @param text           the timed text sample which contains the text
         *                       needed to be displayed and the display format.
         * {@hide}
         */
        public void onTimedText(MediaPlayer mp, TimedText text);
    }

    /**
     * Register a callback to be invoked when a timed text is available
     * for display.
     *
     * @param listener the callback that will be run
     * {@hide}
     */
    public void setOnTimedTextListener(OnTimedTextListener listener)
    {
        mOnTimedTextListener = listener;
    }

    private OnTimedTextListener mOnTimedTextListener;

    
    
    /* Do not change these values (starting with KEY_PARAMETER) without updating
     * their counterparts in include/media/mediaplayer.h!
     */
    /*
     * Key used in setParameter method.
     * Indicates the index of the timed text track to be enabled/disabled.
     * The index includes both the in-band and out-of-band timed text.
     * The index should start from in-band text if any. Application can retrieve the number
     * of in-band text tracks by using MediaMetadataRetriever::extractMetadata().
     * Note it might take a few hundred ms to scan an out-of-band text file
     * before displaying it.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX = 1000;
    /*
     * Key used in setParameter method.
     * Used to add out-of-band timed text source path.
     * Application can add multiple text sources by calling setParameter() with
     * KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE multiple times.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE = 1001;
    /*
     * Key used in getParameter method.
     * Used to retrieve in-band timed text source info.
     */
    private static final int KEY_PARAMETER_IN_BAND_SUBTITLE_INFO = 1201;
    /*
     * Key used in getParameter method.
     * Used to retrieve audio track info.
     */    
    private static final int KEY_PARAMETER_AUDIO_TRACK_INFO = 1202;
    /*
     * Key used in getParameter method.
     * Used to retrieve media info.
     */
    private static final int KEY_PARAMETER_MEDIA_INFO = 1300;
    /*
     * Key used in getParameter method.
     * Used to set audio track index
     */
    private static final int KEY_PARAMETER_AUDIO_TRACK_INDEX = 1400;
    /*
     * Key used in setParameter method.
     * Used to set multi-speed playback
     */
    private static final int KEY_PARAMETER_ENABLE_MULTI_SPEED_PLAYBACK = 2001;
    private static final int KEY_PARAMETER_DISABLE_MULTI_SPEED_PLAYBACK = 2002;
    
    private static final int MULTI_SPEED_FORWARD_PLAYBACK = 0x01;
    private static final int MULTI_SPEED_BACKWARD_PLAYBACK = 0x02;
    

    // There are currently no defined keys usable from Java with get*Parameter.
    // But if any keys are defined, the order must be kept in sync with include/media/mediaplayer.h.
    // private static final int KEY_PARAMETER_... = ...;

    /**
     * Sets the parameter indicated by key.
     * @param key key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public native boolean setParameter(int key, Parcel value);

    /**
     * Sets the parameter indicated by key.
     * @param key key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public boolean setParameter(int key, String value) {
        Parcel p = Parcel.obtain();
        p.writeString(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /**
     * Sets the parameter indicated by key.
     * @param key key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public boolean setParameter(int key, int value) {
        Parcel p = Parcel.obtain();
        p.writeInt(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /**
     * Gets the value of the parameter indicated by key.
     * @param key key indicates the parameter to get.
     * @param reply value of the parameter to get.
     */
    private native void getParameter(int key, Parcel reply);

    /**
     * Gets the value of the parameter indicated by key.
     * The caller is responsible for recycling the returned parcel.
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public Parcel getParcelParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        return p;
    }

    /**
     * Gets the value of the parameter indicated by key.
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public String getStringParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        String ret = p.readString();
        p.recycle();
        return ret;
    }

    /**
     * Gets the value of the parameter indicated by key.
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public int getIntParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        int ret = p.readInt();
        p.recycle();
        return ret;
    }
    
    /**
     * @param index The index of the text track to be turned on.
     * @return true if the text track is enabled successfully.
     * {@hide}
     */
    public boolean enableTimedTextTrackIndex(int index) {
        if (index < 0) {
            return false;
        }
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, index);
    }

    /**
     * Enables the first timed text track if any.
     * @return true if the text track is enabled successfully
     * {@hide}
     */
    public boolean enableTimedText() {
        return enableTimedTextTrackIndex(0);
    }

    /**
     * Disables timed text display.
     * @return true if the text track is disabled successfully.
     * {@hide}
     */
    public boolean disableTimedText() {
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, -1);
    }    
    
    /**
     * Retrieve in-band timed text information
     * @return true if get text track successfully.
     */    
    public boolean getInBandTimedTextInfo(List<String> subtitleInfoList) {
    	Parcel p = getParcelParameter(KEY_PARAMETER_IN_BAND_SUBTITLE_INFO);
    	int subtitleCount = p.readInt();
    	for (int i = 0; i < subtitleCount; ++i) {
    		String subtitleName = p.readString();
    		subtitleInfoList.add(subtitleName);
    	}
    	p.recycle();
    	return true;
    }
    
    /**
     * Retrieve audio track information
     * @return true if get audio track successfully.
     */    
    public boolean getAudioTrackInfo(AudioTrackInfo trackInfo) {
    	Parcel p = getParcelParameter(KEY_PARAMETER_AUDIO_TRACK_INFO);
    	int audioCount = p.readInt();
    	for (int i = 0; i < audioCount; ++i) {
    		String audioTrackName = p.readString();
    		trackInfo.addAudioTrack(audioTrackName);
    	}
    	trackInfo.setSelectedAudioTrack(p.readInt());
    	p.recycle();
    	return true;    	
    }
    
    /**
     * Retrieve media meta-data information
     * @return true if get media meta-data successfully.
     */     
    public MediaInfo getMediaInfo() {
    	Parcel p = getParcelParameter(KEY_PARAMETER_MEDIA_INFO);
    	MediaInfo info = new MediaInfo();
    	if (p.readInt() != 0) {// have video info
    		info.duration = p.readInt();
	    	info.videoHeight = p.readInt();
	    	info.videoWidth = p.readInt();
	    	info.videoCodecId = p.readInt();    	
	    	info.videoCodecName = p.readString();
	    	info.videoCodecProfile = p.readString();
	    	info.videoFrameRate = p.readFloat();
	    	info.fpaType = p.readInt();
    	}
    	
    	if (p.readInt() != 0) {// have audio info
	    	info.audioCodecId = p.readInt();
	    	info.audioCodecName = p.readString();
	    	info.audioSampleRate = p.readInt();
    	}
    	p.recycle();
    	return info;
    }
    
    /**
     * Sets the out-of-band timed-text source
     * @return true if sets successfully.
     */     
    public boolean setOutOfBandTextSource(String sourceUri) {
    	return setParameter(KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE, sourceUri);
    }
    
    /**
     * Sets the audio track source
     * @return true if sets successfully.
     */     
    public boolean setAudioTrackSource(int index) {
    	return setParameter(KEY_PARAMETER_AUDIO_TRACK_INDEX, index);
    }    
	
    /**
     * Enables the multi-speed playback.
     * @param speed, the playback speed, time unit is second
     * @param forward, the playback direction
     * @return true if multi-speed playback is enabled successfully
     * {@hide}
     */     
    public boolean enableMultiSpeedPlayback(int speed, boolean forward) {
        Parcel p = Parcel.obtain();
        p.writeInt(speed);
        if (forward) {
        	p.writeInt(MULTI_SPEED_FORWARD_PLAYBACK);
        }
        else {
        	p.writeInt(MULTI_SPEED_BACKWARD_PLAYBACK);
        }
        boolean ret = setParameter(KEY_PARAMETER_ENABLE_MULTI_SPEED_PLAYBACK, p);
        p.recycle();
        return ret;
    }
    
    /**
     * Disables the multi-speed playback.
     * @return true if multi-speed playback is disabled successfully
     * {@hide}
     */     
    public boolean disableMultiSpeedPlayback() {
        Parcel p = Parcel.obtain();
        boolean ret = setParameter(KEY_PARAMETER_DISABLE_MULTI_SPEED_PLAYBACK, p);
        p.recycle();
        return ret;
    }    
    
    /**
     * Called from native code when an interesting event happens.  This method
     * just uses the EventHandler system to post the event back to the main app thread.
     * We use a weak reference to the original MediaPlayer object so that the native
     * code is safe from the object disappearing from underneath it.  (This is
     * the cookie passed to native_setup().)
     */
    private static void postEventFromNative(Object mediaplayer_ref,
                                            int what, int arg1, int arg2, Object obj)
    {
        MediaPlayer mp = (MediaPlayer)((WeakReference)mediaplayer_ref).get();
        if (mp == null) {
            return;
        }

        if (mp.mEventHandler != null) {
            Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mp.mEventHandler.sendMessage(m);
        }
    }
    
    /**
     * Set the low-level power management behavior for this MediaPlayer.  This
     * can be used when the MediaPlayer is not playing through a SurfaceHolder
     * set with {@link #setDisplay(SurfaceHolder)} and thus can use the
     * high-level {@link #setScreenOnWhilePlaying(boolean)} feature.
     *
     * <p>This function has the MediaPlayer access the low-level power manager
     * service to control the device's power usage while playing is occurring.
     * The parameter is a combination of {@link android.os.PowerManager} wake flags.
     * Use of this method requires {@link android.Manifest.permission#WAKE_LOCK}
     * permission.
     * By default, no attempt is made to keep the device awake during playback.
     *
     * @param context the Context to use
     * @param mode    the power/wake mode to set
     * @see android.os.PowerManager
     */
    public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode|PowerManager.ON_AFTER_RELEASE, MediaPlayer.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }
    }    
    
    /**
     * Control whether we should use the attached SurfaceHolder to keep the
     * screen on while video playback is occurring.  This is the preferred
     * method over {@link #setWakeMode} where possible, since it doesn't
     * require that the application have permission for low-level wake lock
     * access.
     *
     * @param screenOn Supply true to keep the screen on, false to allow it
     * to turn off.
     */
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }
    
    private void stayAwake(boolean awake) {
        if (mWakeLock != null) {
            if (awake && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            } else if (!awake && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        mStayAwake = awake;
        updateSurfaceScreenOn();
    }    
    
    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    private static final int MEDIA_NOP = 0; // interface test message
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    
    private class EventHandler extends Handler
    {
        private MediaPlayer mMediaPlayer;

        public EventHandler(MediaPlayer mp, Looper looper) {
            super(looper);
            mMediaPlayer = mp;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mMediaPlayer.mNativeContext == 0) {
                Log.w(TAG, "mediaplayer went away with unhandled events");
                return;
            }
            switch(msg.what) {
            case MEDIA_PREPARED:
                if (mOnPreparedListener != null)
                    mOnPreparedListener.onPrepared(mMediaPlayer);
                return;

            case MEDIA_PLAYBACK_COMPLETE:
                if (mOnCompletionListener != null)
                    mOnCompletionListener.onCompletion(mMediaPlayer);
                stayAwake(false);
                return;

            case MEDIA_BUFFERING_UPDATE:
                if (mOnBufferingUpdateListener != null)
                    mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, msg.arg1);
                return;

            case MEDIA_SEEK_COMPLETE:
              if (mOnSeekCompleteListener != null)
                  mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
              return;

            case MEDIA_SET_VIDEO_SIZE:
              if (mOnVideoSizeChangedListener != null)
                  mOnVideoSizeChangedListener.onVideoSizeChanged(mMediaPlayer, msg.arg1, msg.arg2);
              return;

            case MEDIA_ERROR:
                // For PV specific error values (msg.arg2) look in
                // opencore/pvmi/pvmf/include/pvmf_return_codes.h
                Log.e(TAG, "Error (" + msg.arg1 + "," + msg.arg2 + ")");
                boolean error_was_handled = false;
                if (mOnErrorListener != null) {
                    error_was_handled = mOnErrorListener.onError(mMediaPlayer, msg.arg1, msg.arg2);
                }
                if (mOnCompletionListener != null && ! error_was_handled) {
                    mOnCompletionListener.onCompletion(mMediaPlayer);
                }
                stayAwake(false);
                return;

            case MEDIA_INFO:
                if (msg.arg1 != MEDIA_INFO_VIDEO_TRACK_LAGGING) {
                    Log.i(TAG, "Info (" + msg.arg1 + "," + msg.arg2 + ")");
                }
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mMediaPlayer, msg.arg1, msg.arg2);
                }
                // No real default action so far.
                return;
            case MEDIA_TIMED_TEXT:
                if (mOnTimedTextListener != null) {
                    if (msg.obj == null) {
                        mOnTimedTextListener.onTimedText(mMediaPlayer, null);
                    } else {
                        if (msg.obj instanceof byte[]) {
                            TimedText text = new TimedText((byte[])(msg.obj));
                            mOnTimedTextListener.onTimedText(mMediaPlayer, text);
                        }
                    }
                }
                return;

            case MEDIA_NOP: // interface test message - ignore
                break;

            default:
                Log.e(TAG, "Unknown message type " + msg.what);
                return;
            }
        }
    }
		
	private native int native_init();	
	private native void native_setup(Object amavjni_this);
	private native void native_setdatasource(String path) throws IllegalArgumentException;
	private native void native_setdatasource(String path, Map<String, String> headers) throws IllegalArgumentException;
	private native void _setVideoSurface(Surface surface);
	private native void _set3dMode(Surface surface, boolean mode);
	private native void native_prepare() throws IllegalStateException;
	private native void native_prepare_async() throws IllegalStateException;
	private native void native_start() throws IllegalStateException;
	private native void native_stop() throws IllegalStateException;
	private native void native_pause() throws IllegalStateException;
	private native void native_release() throws IllegalStateException;
	private native void native_seekto(int to_ms) throws IllegalStateException;
	private native int native_getcurrenttime() throws IllegalStateException;
	private native int native_getduration()	throws IllegalStateException;
    /**
     * @param update_only If true fetch only the set of metadata that have
     *                    changed since the last invocation of getMetadata.
     *                    The set is built using the unfiltered
     *                    notifications the native player sent to the
     *                    MediaPlayerService during that period of
     *                    time. If false, all the metadatas are considered.
     * @param apply_filter  If true, once the metadata set has been built based on
     *                     the value update_only, the current filter is applied.
     * @param reply[out] On return contains the serialized
     *                   metadata. Valid only if the call was successful.
     * @return The status code.
     */
    private native final boolean native_getMetadata(boolean update_only,
                                                    boolean apply_filter,
                                                    Parcel reply);	
	private native void native_reset();
}
