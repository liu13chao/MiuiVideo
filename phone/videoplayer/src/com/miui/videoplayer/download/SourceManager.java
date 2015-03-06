package com.miui.videoplayer.download;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.miui.video.R;
import com.miui.video.model.AppSingleton;
import com.miui.video.model.DataStore;
import com.miui.video.type.SourceInfo;
import com.miui.video.type.VideoPlayerSoInfo;
import com.miui.videoplayer.common.DuoKanConstants;
import com.miui.videoplayer.common.SignatureUtil;
import com.miui.videoplayer.download.ApkLoaderConsole.OnApkStatusListener;
import com.miui.videoplayer.download.SoLoaderConsole.OnSoStatusListener;
import com.miui.videoplayer.model.MediaConfig;


public class SourceManager extends AppSingleton{
	
    public static final String TAG = "SourceManager";

	private String mAppLibPath = null;
	private List<SourceInfo> mSourceInfos = new ArrayList<SourceInfo>();

	private DownloadSoStatus mDownloadSoStatus = DownloadSoStatus.IDLE;
	private DownloadSoStatus mDownloadMySoStatus = DownloadSoStatus.IDLE;
	
	public SparseArray<SourceConfig> mConfigMap = new SparseArray<SourceConfig>();
	
	private SoLoaderConsole mSoConsole;
	private ApkLoaderConsole mApkConsole;

    @Override
    public void init(Context context) {
        super.init(context);
        SourceConfig qiyi = SourceConfig.getQiyiSource();
        mConfigMap.put(qiyi.mSource, qiyi);
        SourceConfig ifeng = SourceConfig.getIfengSource();
        mConfigMap.put(ifeng.mSource, ifeng);
    }

	public enum DownloadSoStatus{
		IDLE, BUSY
	}
	
	public String getSourceDir(int source){
	    File file = mContext.getDir("sdk", Context.MODE_PRIVATE);
	    String dir = file.getAbsolutePath() + "/" + source;
	    dirChecker(dir);
	    return dir + "/";
	}
	
	public SourceConfig getSourceConfig(int source){
	    SourceConfig config = mConfigMap.get(source, null);
	    return config;
	}
	
//	public String getSourceZip
	
	public void dirChecker(String dir){
	    if(!TextUtils.isEmpty(dir)){
	        File file = new File(dir);
	        if(!file.exists()){
	            file.mkdir();
	        }
	    }
	}
	
	public void setSourceInfos(SourceInfo[] infos) {
		mSourceInfos.clear();
		if (infos != null && infos.length > 0) {
			mSourceInfos.addAll(Arrays.asList(infos));
		}
	}

	public List<SourceInfo> getSourceInfos(){
		return mSourceInfos;
	}
	
	public SourceInfo getSourceInfo(int sourceId) {
		if(mSourceInfos == null || mSourceInfos.size() == 0){
			setSourceInfos(DataStore.getInstance().getSourceInfos());
		}
		for (SourceInfo info : mSourceInfos) {
			if (info != null && info.sourceid == sourceId) {
				return info;
			}
		}
		return null;
//		return fake();
	}
	
	public String getSourceName( int source) {
		SourceInfo info = getSourceInfo(source);
		if (info == null) {
			return mContext.getString(R.string.vp_unknown_source);
		}
		return info.name;
	}
	
	public void setAppLibDir(String dir) {
		mAppLibPath = dir;
	}
	
	public String getAppLibDir(Context context) {
		Log.d(TAG, "app: " + mAppLibPath);
//		if(TextUtils.isEmpty(mAppLibPath)){
//			mAppLibPath = context.getDir("lib", Context.MODE_PRIVATE).getAbsolutePath();
//			if (mAppLibPath.endsWith(File.separator)) {
//				mAppLibPath = mAppLibPath + "armeabi";
//			}else{
//				mAppLibPath = mAppLibPath + File.separator + "armeabi";
//			}
//		}
		return mAppLibPath;
	}
	
	public String getApkDir(Context context) {
		File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		if (file == null) {
			return null;
		}
		return file.getAbsolutePath();
	}
	
//	private SourceInfo fake() {
//		SourceInfo info = new SourceInfo();
//		info.name = "sohu";
//		ClientInfo client = new ClientInfo();
//		client.url = "http://upgrade.m.tv.sohu.com//channels//hdv//4.3.3//SohuTV_4.3.3_1193_201409021210.apk";
//		client.is_download = 1;
//		PluginInfo plugin = new PluginInfo();
//		plugin.url = "http://package.box.xiaomi.com//mfsv2//download//s010//Hp01C4VpNQOt//vPE9opVGRWDjNW.zip";
//		plugin.md5 = "93e990a844007834373e9c289d34f376";
//		info.client_info = client;
//		info.plugin_info = plugin;
//		return info;
//	}
	
//	private VideoPlayerSoInfo fakeMyInfo(){
//		VideoPlayerSoInfo info = new VideoPlayerSoInfo();
//		info.url = "http://package.box.xiaomi.com//mfsv2//download//s010//Hp01C4VpNQOt//vPE9opVGRWDjNW.zip";
//		info.md5 = "93e990a844007834373e9c289d34f376";
//		return info;
//	}

	public void downloadApk(int source, final OnDownloadApkListener listener) {
		if(listener == null){
			return;
		}
		final SourceInfo info = getSourceInfo(source);
		if (info == null || info.client_info == null || info.client_info.url == null
				|| info.client_info.is_download == 0) {
			return;
		}
		Log.d(TAG, "info.client_info.url: " + info.client_info.url);
		
		final String path = getApkLocalPath(info.client_info.url);
		Log.d(TAG, "apk path: " + path);
		if (path == null) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		mApkConsole = new ApkLoaderConsole(mContext, info.client_info.url, path);
		mApkConsole.setSourceInfo(info);
		mApkConsole.setOnApkStatusListener(new OnApkStatusListener() {
			@Override
			public void onApkDownloadStart() {
				listener.onApkDownloadStart(info.name);
			}
			
			@Override
			public void onApkDownloadProgress(int completed, int total) {
//				listener.onApkDownloadProgress(completed, total);
			}
			
			@Override
			public void onApkDownloadError(int error) {
				listener.onApkDownloadError(error);
			}

			@Override
			public void onApkDownloadComplete() {
				listener.onApkDownloadComplete();
			}
		});
		mApkConsole.start();
	}
	
	public void downloadVideoPlayerSo(final VideoPlayerSoInfo info){
		if (info == null || info.url == null || mDownloadMySoStatus == DownloadSoStatus.BUSY) {
			return;
		}
		final String path = getMySoLocalPath(info.url);
		Log.d(TAG, "plugin path: " + path);
		if (path == null) {
			return;
		}
		final File file = new File(path);
		new CompareMD5Task(file, info.md5, new OnCompareResultListener() {
			@Override
			public void onCompareResult(boolean result) {
				if (result) {
					return;
				} else {
					if (file.exists()) {
						file.delete();
					}
					final SoLoaderConsole tSoConsole = new SoLoaderConsole(mContext, info.url, path);
					tSoConsole.setOnSoStatusListener(new OnSoStatusListener() {
						@Override
						public void onSoReady(String path) {
							mDownloadMySoStatus = DownloadSoStatus.IDLE;
						}
						
						@Override
						public void onSoNotReady() {
							mDownloadMySoStatus = DownloadSoStatus.IDLE;
						}

						@Override
						public void onSoDownloadStart() {
							tSoConsole.startDownload();
							mDownloadMySoStatus = DownloadSoStatus.BUSY;
						}

						@Override
						public void onSoDownloadProgress(int completed,
								int total) {
						}

						@Override
						public void onSoDownloadError(int error) {
							mDownloadMySoStatus = DownloadSoStatus.IDLE;
						}

						@Override
						public void onSoDownloadComplete() {
							mDownloadMySoStatus = DownloadSoStatus.IDLE;
						}
					});
					tSoConsole.start();
				}
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void downloadSo(int source, final OnDownloadSoListener listener) {
		if (listener == null || mDownloadSoStatus == DownloadSoStatus.BUSY) {
			return;
		}
		final SourceInfo info = getSourceInfo(source);
		if (info == null || info.plugin_info == null || info.plugin_info.url == null) {
			listener.onSoNotReady();
			return;
		}
		final String path = getSoLocalPath(info.plugin_info.url);
		Log.d(TAG, "plugin path: " + path);
		if (path == null) {
			listener.onSoNotReady();
			return;
		}
		final File file = new File(path);
		new CompareMD5Task(file, info.plugin_info.md5, new OnCompareResultListener() {
			
			@Override
			public void onCompareResult(boolean result) {
				if (result) {
					listener.onSoNotReady();
				} else {
					if (file.exists()) {
						file.delete();
					}
					mSoConsole = new SoLoaderConsole(mContext, info.plugin_info.url, path);
					mSoConsole.setSourceInfo(info);
					mSoConsole.setOnSoStatusListener(new OnSoStatusListener() {
						@Override
						public void onSoReady(String path) {
							listener.onSoReady(path);
							mDownloadSoStatus = DownloadSoStatus.IDLE;
						}
						
						@Override
						public void onSoNotReady() {
							listener.onSoNotReady();
							mDownloadSoStatus = DownloadSoStatus.IDLE;
						}

						@Override
						public void onSoDownloadStart() {
							listener.onSoDownloadStart();
						}

						@Override
						public void onSoDownloadProgress(int completed,
								int total) {
							listener.onSoDownloadProgress(completed, total);
						}

						@Override
						public void onSoDownloadError(int error) {
							listener.onSoDownloadError(error);
							mDownloadSoStatus = DownloadSoStatus.IDLE;
						}

						@Override
						public void onSoDownloadComplete() {
							listener.onSoDownloadComplete();
							mDownloadSoStatus = DownloadSoStatus.IDLE;
						}
					});
					mSoConsole.start();
				}
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void downloadApk(){
		if(mApkConsole != null){
			mApkConsole.downloadProgress(0, 0);
			mApkConsole.startDownload();
		}
	}
	
	public void downloadApkStop(){
		if(mApkConsole != null){
			mApkConsole.stopDownload();
		}
	}
	
	public void downloadSo(){
		if(mSoConsole != null){
			mSoConsole.downloadProgress(0, 0);
			mSoConsole.startDownload();
			mDownloadSoStatus = DownloadSoStatus.BUSY;
		}
	}
	
	public void downloadSoWithoutLoading(){
		if(mSoConsole != null){
			mSoConsole.startDownload();
			mDownloadSoStatus = DownloadSoStatus.BUSY;
		}
	}	
	
	public void downloadSoStop(){
		if(mSoConsole != null){
			mSoConsole.stopDownload();
			mDownloadSoStatus = DownloadSoStatus.IDLE;
		}
	}
	
	private static class CompareMD5Task extends AsyncTask<Void, Void, Boolean> {
		
		private final File mFile;
		private final String mMD5;
		private final OnCompareResultListener mListener;
		public CompareMD5Task(File localFile, String remoteMD5, OnCompareResultListener listener) {
			mFile = localFile;
			mMD5 = remoteMD5;
			mListener = listener;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			final long start = System.currentTimeMillis();
			final String md5 = SignatureUtil.getMD5(mFile);
			Log.e(TAG, "compute md5 use " + (System.currentTimeMillis() - start) + " ms");
			Log.d(TAG, "file md5: " + md5 + ", url md5: " + mMD5);
			if (md5.equalsIgnoreCase(mMD5)) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
	    protected void onPostExecute(Boolean result) {
			if (mListener != null) {
				mListener.onCompareResult(result);
			}
	    }
		
	}
	
	public static interface OnCompareResultListener {
		public void onCompareResult(boolean same);
	}

	private String getApkLocalPath(String url) {
		return getLocalPath(mContext, url, mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
	}

	private String getMySoLocalPath(String url){
		String path = DuoKanConstants.PATH_DATA;
		if (url == null || url.lastIndexOf(File.separator) < 0) {
			return path + "temp.zip";
		}else{
			if (path.endsWith(File.separator)) {
				path = path.substring(0, path.length() - 1);
			}
			return path + url.substring(url.lastIndexOf(File.separator));
		}
	}
	
	private String getSoLocalPath(String url) {
		return getLocalPath(mContext, url, mContext.getDir("lib", Context.MODE_PRIVATE));
	}
	
	private String getLocalPath(Context context, String url, File file) {
		if (file == null || file.getAbsolutePath() == null) {
			return null;
		}
		if (url == null || url.lastIndexOf(File.separator) < 0) {
			return null;
		}
		String path = file.getAbsolutePath();
		if (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}
		return path + url.substring(url.lastIndexOf(File.separator));
	}
	
	public static interface OnDownloadSoListener{
		public void onSoReady(String path);
		public void onSoNotReady();
		public void onSoDownloadComplete();
		public void onSoDownloadStart();
		public void onSoDownloadProgress(int completed, int total);
		public void onSoDownloadError(int error);
	}

	public static interface OnDownloadApkListener{
		public void onApkDownloadComplete();
		public void onApkDownloadStart(String name);
		public void onApkDownloadProgress(int completed, int total);
		public void onApkDownloadError(int error);
	}	
	
	public static class SourceConfig{
	    public int mSource;
	    public String mZipPath;
	    public String mZipMd5;
	    public boolean mIsAsset;
	    public String mClassName;
	    public String mApkName;
	    
	    public static SourceConfig getQiyiSource(){
	        SourceConfig config = new SourceConfig();
	        config.mSource = MediaConfig.MEDIASOURCE_IQIYI_PHONE_TYPE_CODE;
	        config.mZipPath = "iqiyi.zip";
	        config.mIsAsset = true;
	        config.mClassName = "com.miui.videoplayer.sdk.QiyiVideoView";
	        config.mApkName = "IQiyiVideoPlayer.apk";
	        return config;
	    }
	    
	    public static SourceConfig getIfengSource(){
	        SourceConfig config = new SourceConfig();
	        config.mSource = MediaConfig.MEDIASOURCE_IFENG_PHONE_TYPE_CODE;
	        config.mZipPath = "ifeng.zip";
	        config.mIsAsset = true;
	        config.mClassName = "com.miui.videoplayer.sdk.IfengVideoView";
	        config.mApkName = "IfengVideoPlayer.apk";
	        return config;
	    }
	}
	
}
