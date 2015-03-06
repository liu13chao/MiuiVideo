/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IIMediaPlayer.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-18
 */

package com.miui.videoplayer.media;

import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * @author tianli
 *
 */
public interface IMediaPlayer{

    public static final int MEDIA_ERROR_UNKNOWN = 1;
    
	public int getCurrentPosition();

	public int getDuration();

	public int getVideoHeight();

	public int getVideoWidth();

	public boolean isPlaying();

	public void pause() throws IllegalStateException;

	public void prepare() throws IOException, IllegalStateException;

	public void prepareAsync() throws IllegalStateException;

	public void release();

	public void reset();

	public void seekTo(int ms) throws IllegalStateException;
	
    public void setScreenOnWhilePlaying(boolean screenOn);


	public void setDataSource(Context context, Uri arg1, Map<String, String> headers)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException;

	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException;

	public void setDataSource(String path, Map<String, String> headers)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException;

	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException;

	public void setDisplay(SurfaceHolder arg0);

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

	public void setOnCompletionListener(OnCompletionListener listener);

	public void setOnErrorListener(OnErrorListener listener);

	public void setOnInfoListener(OnInfoListener listener);

	public void setOnPreparedListener(OnPreparedListener listener);

	public void setOnSeekCompleteListener(OnSeekCompleteListener listener);

	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener);

	public void setSurface(Surface surface);

	public void setVolume(float leftVolume, float rightVolume);

	public void start() throws IllegalStateException;

	public void stop() throws IllegalStateException;
	
    /**
     * Interface definition for a callback to be invoked when the media
     * source is ready for playback.
     */
    public interface OnPreparedListener {
        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the IMediaPlayer that is ready for playback
         */
        void onPrepared(IMediaPlayer mp);
    }
    
    /**
     * Interface definition for a callback to be invoked when playback of
     * a media source has completed.
     */
    public interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the IMediaPlayer that reached the end of the file
         */
        void onCompletion(IMediaPlayer mp);
    }

    /**
     * Interface definition of a callback to be invoked indicating buffering
     * status of a media resource being streamed over the network.
     */
    public interface OnBufferingUpdateListener {
        /**
         * Called to update status in buffering a media stream received through
         * progressive HTTP download. The received buffering percentage
         * indicates how much of the content has been buffered or played.
         * For example a buffering update of 80 percent when half the content
         * has already been played indicates that the next 30 percent of the
         * content to play has been buffered.
         *
         * @param mp      the IMediaPlayer the update pertains to
         * @param percent the percentage (0-100) of the content
         *                that has been buffered or played thus far
         */
        void onBufferingUpdate(IMediaPlayer mp, int percent);
    }

    /**
     * Interface definition of a callback to be invoked indicating
     * the completion of a seek operation.
     */
    public interface OnSeekCompleteListener {
        /**
         * Called to indicate the completion of a seek operation.
         *
         * @param mp the IMediaPlayer that issued the seek operation
         */
        public void onSeekComplete(IMediaPlayer mp);
    }

    /**
     * Interface definition of a callback to be invoked when the
     * video size is first known or updated
     */
    public interface OnVideoSizeChangedListener {
        /**
         * Called to indicate the video size
         *
         * @param mp        the IMediaPlayer associated with this callback
         * @param width     the width of the video
         * @param height    the height of the video
         */
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height);
    }

    /**
     * Interface definition of a callback to be invoked when there
     * has been an error during an asynchronous operation (other errors
     * will throw exceptions at method call time).
     */
    public interface OnErrorListener {
        /**
         * Called to indicate an error.
         *
         * @param mp      the IMediaPlayer the error pertains to
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
        boolean onError(IMediaPlayer mp, int what, int extra);
    }

    /**
     * Interface definition of a callback to be invoked to communicate some
     * info and/or warning about the media or its playback.
     */
    public interface OnInfoListener {
        /**
         * Called to indicate an info or a warning.
         *
         * @param mp      the IMediaPlayer the info pertains to.
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
        boolean onInfo(IMediaPlayer mp, int what, int extra);
    }

}
