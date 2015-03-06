/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   DexVideoView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-23
 */
package com.miui.videoplayer.videoview;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.video.DKApp;
import com.miui.video.controller.CancelableRequestor;
import com.miui.video.controller.CancelableRequestor.OnRequestListener;
import com.miui.video.model.AppSettings;
import com.miui.video.model.DexLoader;
import com.miui.videoplayer.common.IOUtil;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.download.SourceManager.SourceConfig;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.media.AdsPlayListener;
import com.miui.videoplayer.media.IMediaPlayer.OnBufferingUpdateListener;
import com.miui.videoplayer.media.IMediaPlayer.OnCompletionListener;
import com.miui.videoplayer.media.IMediaPlayer.OnErrorListener;
import com.miui.videoplayer.media.IMediaPlayer.OnInfoListener;
import com.miui.videoplayer.media.IMediaPlayer.OnPreparedListener;
import com.miui.videoplayer.media.IMediaPlayer.OnSeekCompleteListener;
import com.miui.videoplayer.media.IMediaPlayer.OnVideoSizeChangedListener;
import com.miui.videoplayer.widget.AdView;

import dalvik.system.DexClassLoader;


/**
 * @author tianli
 *
 */
public abstract class DexVideoView extends RelativeLayout implements IVideoView {

    // Data
    private String mUri;
    private SourceConfig mSource;
    
    ClassLoaderTask mClassLoaderTask;
    private DexClassLoader mDexLoader = null;

    // Views
    public IVideoView mVideoView = null;
    private AdView mAdView;

    // Listeners
    private OnVideoSizeChangedListener mCacheVideoSizeChangedListener;
    private OnBufferingUpdateListener mCacheBufferingUpdateListener;
    private OnInfoListener mCacheInfoListener;
    private OnSeekCompleteListener mCacheSeekCompleteListener;
    private OnErrorListener  mCacheOnErrorListener;
    private OnCompletionListener  mCacheOnCompleteListener;
    private OnPreparedListener mCacheOnPreparedListener;
    private OnVideoLoadingListener mOnVideoLoadingListener;
    private AdsPlayListener mOnAdsPlayListener;

    public DexVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DexVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DexVideoView(Context context) {
        super(context);
    }

    @Override
    final public void setDataSource(String uri) {
        setDataSource(uri, null);
    }

    @Override
    final public void setDataSource(String uri, Map<String, String> headers) {
        mUri = uri;
        if(mVideoView != null){
            mVideoView.setDataSource(uri, headers);
        }else{
            if(mOnVideoLoadingListener != null){
                mOnVideoLoadingListener.onVideoLoading(this);
            }
            requestDexPath();
        }
    }

    private void onVideoViewReady(IVideoView videoView){
        if(videoView != null){
            mVideoView = videoView;  
            if(mAdView != null){
                mVideoView.attachAdView(mAdView);
            }
            addView(mVideoView.asView());
            mVideoView.setOnBufferingUpdateListener(mCacheBufferingUpdateListener);
            mVideoView.setOnCompletionListener(mCacheOnCompleteListener);
            mVideoView.setOnErrorListener(mCacheOnErrorListener);
            mVideoView.setOnInfoListener(mCacheInfoListener);
            mVideoView.setOnPreparedListener(mCacheOnPreparedListener);
            mVideoView.setOnSeekCompleteListener(mCacheSeekCompleteListener);
            mVideoView.setOnVideoLoadingListener(mOnVideoLoadingListener);
            mVideoView.setOnVideoSizeChangedListener(mCacheVideoSizeChangedListener);
            mVideoView.setAdsPlayListener(mOnAdsPlayListener);
            if(!TextUtils.isEmpty(mUri)){
                setDataSource(mUri);
                start();
            }
        }
    }


    private IVideoView getVideoView(DexClassLoader classLoader, String className){
        try{
            Class<?> clazz = classLoader.loadClass(className);
            Constructor<?> ctor = clazz.getConstructor(Context.class);
            return (IVideoView)ctor.newInstance(getContext());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public void setDexZipPath(String filePath, String apkName, String className){
////        mDexZipPath = filePath;
////        mApkName = apkName;
////        mClassName = className;
//        unzip();
//    }
    
    public void setZipPath(SourceConfig source){
        mSource = source;
        if(mSource != null){
            unzip();
        }
    }
//
//    public void setDexZipAssets(String assetPath, String apkName, String className){
//        mDexZipAsset =assetPath;
//        mApkName = apkName;
//        mClassName =className;
//        unzip();
//    }

    private void unzip(){
        if(mClassLoaderTask != null){
            mClassLoaderTask.cancel();
        }
        mClassLoaderTask = new ClassLoaderTask();
        mClassLoaderTask.setRequestListener(new OnRequestListener() {
            @Override
            public void onRequestDone() {
                if(mDexLoader  != null){
                    mVideoView = getVideoView(mDexLoader, mSource.mClassName);
                }
                onVideoViewReady(mVideoView);
            }
        });
        mClassLoaderTask.start();
    }

    protected abstract void requestDexPath();

    protected abstract int getSource();

    private class ClassLoaderTask extends CancelableRequestor{
        @Override
        protected void onDoRequest() {
                String output = DKApp.getSingleton(SourceManager.class).getSourceDir(getSource());
                String apkPath = output + mSource.mApkName;
                String md5 = DKApp.getSingleton(AppSettings.class).getValue(apkPath);
                File file = new File(apkPath);
                boolean success = true;
                if(!file.exists() || TextUtils.isEmpty(md5) || !md5.equals(mSource.mZipMd5)){
                    success = doUnzip(output);
                }
                if(success){
                    mDexLoader = DKApp.getSingleton(DexLoader.class).getClassLoader(
                            apkPath, IVideoView.class.getClassLoader());
                    DKApp.getSingleton(AppSettings.class).saveValue(apkPath,  mSource.mZipMd5);
                }
        }
        
        private boolean doUnzip(String output){
            try{
                if(mSource.mIsAsset){
                    InputStream is = getContext().getAssets().open(mSource.mZipPath);
                    return IOUtil.unZip(is, output);
                }else{
                    return  IOUtil.unZip(new File(mSource.mZipPath), output);
                }
            }catch(Exception e){
            }
            return  false;
        }
    };

    @Override
    public void start() {
        if(mVideoView != null){
            mVideoView.start();
        }
    }

    @Override
    public void pause() {
        if(mVideoView != null){
            mVideoView.pause();
        }
    }

    @Override
    public int getDuration() {
        try{
            if(mVideoView != null){
                return mVideoView.getDuration();
            }
        }catch(Throwable t){
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        try{
            if(mVideoView != null){
                return mVideoView.getCurrentPosition();
            }
        }catch(Throwable t){
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        try{
            if(mVideoView != null){
                mVideoView.seekTo(pos);
            }
        }catch(Throwable t){
        }
    }

    @Override
    public boolean isPlaying() {
        try{
            if(mVideoView != null){
                return mVideoView.isPlaying();
            }
        }catch(Throwable t){
        }
        return false;
    }

    @Override
    public boolean isInPlaybackState() {
        try{
            if(mVideoView != null){
                return mVideoView.isInPlaybackState();
            }
        }catch(Throwable t){
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        try{
            if(mVideoView != null){
                return mVideoView.getBufferPercentage();
            }
        }catch(Throwable t){
        }
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

    @Override
    public boolean canBuffering() {
        return true;
    }

    @Override
    public boolean isAirkanEnable() {
        try{
            if(mVideoView != null){
                return mVideoView.isAirkanEnable();
            }
        }catch(Throwable t){
        }
        return false;
    }

    @Override
    public void close() {
        try{
            if(mVideoView != null){
                mVideoView.close();
            }
        }catch(Throwable e){
        }
    }

    @Override
    public Uri getUri() {
        try{
            if(mVideoView != null){
                return mVideoView.getUri();
            }
        }catch(Throwable e){
        }
        return null;
    }
    
    @Override
    public int getRealPlayPosition() {
        try{
            if(mVideoView != null){
                return mVideoView.getRealPlayPosition();
            }
        }catch(Throwable e){
        }
        return 0;
    }

    @Override
    public boolean isAdsPlaying() {
        try{
            if(mVideoView != null){
                return mVideoView.isAdsPlaying();
            }
        }catch(Throwable e){
        }
        return false;
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
    public void adjustVideoPlayViewSize(int videoSizeStyle) {
    }

    @Override
    public int getVideoWidth() {
        try{
            if(mVideoView != null){
                return mVideoView.getVideoWidth();
            }
        }catch(Throwable t){
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        try{
            if(mVideoView != null){
                return mVideoView.getVideoHeight();
            }
        }catch(Throwable t){
        }
        return 0;
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public void continuePlay(PlayHistoryEntry history) {
        try{
            if(mVideoView != null){
                mVideoView.continuePlay(history);
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void setPlayInfo(Object playInfo) {
    }

    @Override
    public void onActivityPause() {
        try{
            if(mVideoView != null){
                mVideoView.onActivityPause();
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void onActivityResume() {
        try{
            if(mVideoView != null){
                mVideoView.onActivityResume();
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void onActivityDestroy() {
        try{
            if(mVideoView != null){
                mVideoView.onActivityDestroy();
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {
        try{
            if(mVideoView != null){
                mVideoView.setForceFullScreen(forceFullScreen);
            }
        }catch(Throwable t){
        }
    }


    @Override
    public void requestVideoLayout() {
        try{
            if(mVideoView != null){
                mVideoView.requestVideoLayout();
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void attachAdView(AdView adView) {
        try{
            if(adView != null){
                mAdView = adView;
                if(mVideoView != null){
                    mVideoView.attachAdView(adView);
                }
            }
        }catch(Throwable t){
        }
    }
    @Override
    public void setAdsPlayListener(AdsPlayListener adPlayListener) {
        try{
            mOnAdsPlayListener = adPlayListener;
            if(mVideoView != null){
                mVideoView.setAdsPlayListener(adPlayListener);
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void setOnVideoLoadingListener(OnVideoLoadingListener listener) {
        try{
            mOnVideoLoadingListener = listener;
            if(mVideoView != null){
                mVideoView.setOnVideoLoadingListener(listener);
            }
        }catch(Throwable t){
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mCacheOnPreparedListener = listener;
        if(mVideoView != null){
            mVideoView.setOnPreparedListener(listener);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mCacheOnCompleteListener = listener;
        if(mVideoView != null){
            mVideoView.setOnCompletionListener(listener);
        }
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mCacheOnErrorListener = listener;
        if(mVideoView != null){
            mVideoView.setOnErrorListener(listener);
        }
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mCacheSeekCompleteListener = listener;
        if(mVideoView != null){
            mVideoView.setOnSeekCompleteListener(listener);
        }
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        mCacheInfoListener = listener;
        if(mVideoView != null){
            mVideoView.setOnInfoListener(listener);
        }
    }

    @Override
    public void setOnBufferingUpdateListener(
            OnBufferingUpdateListener onBufferingUpdateListener) {
        mCacheBufferingUpdateListener = onBufferingUpdateListener;
        if(mVideoView != null){
            mVideoView.setOnBufferingUpdateListener(onBufferingUpdateListener);
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mCacheVideoSizeChangedListener = onVideoSizeChangedListener;
        if(mVideoView != null){
            mVideoView.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        }
    }

    @Override
    public boolean isSupportZoom() {
        try{
            if(mVideoView != null){
                return mVideoView.isSupportZoom();
            }
        }catch(Throwable t){
        }
        return false;
    }

    @Override
    public boolean hasLoadingAfterAd() {
        return true;
    }

}
