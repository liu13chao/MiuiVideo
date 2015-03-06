package com.miui.video.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.db.DBUtil;
import com.miui.video.model.AppEnv;
import com.miui.video.model.MediaUrlForPlayerUtil;
import com.miui.video.model.MediaUrlForPlayerUtil.PlayUrlObserver;
import com.miui.video.offline.Downloader.DownloadCallback;
import com.miui.video.response.MediaDetailInfoResponse;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.util.DKLog;
import com.miui.video.util.ObjectStore;
import com.miui.video.util.Util;
import com.xiaomi.common.util.Network;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class OfflineDownloader implements PlayUrlObserver, Observer, DownloadCallback {
	private final static String TAG = "OfflineDownloader";
	
	private final static int MAX_GETURL_RETRY = 5;
	private final static int MAX_DOWNLOAD_RETRY = 2;
	
	private final Executor mExecutor;
	private int mCompleteSize = 0;
	
	private volatile OfflineMedia mOfflineMedia;
	private int[] mSourcelist;
	private int mCurSource = -1;

	private Downloader mLoader;
	
	private ServiceRequest mDetailInfoRequest = null;
	private int mGetUrlRetry;
	private int mDownloadRetry;
	
	private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());
	
	public OfflineDownloader(OfflineMedia media, Executor executor) {
		if (media == null) {
			throw new IllegalArgumentException("media should not be null");
		}
		if (executor == null) {
			throw new IllegalArgumentException("executor should not be null");
		}
		mOfflineMedia = media;
		mExecutor = executor;

		mGetUrlRetry = 0;
		mDownloadRetry = 0;
		mCurSource = mOfflineMedia.source;
	}

	public OfflineMedia getOfflineMedia() {
		return (OfflineMedia) mOfflineMedia.clone();
	}
	
	public String getKey() {
		return mOfflineMedia.getKey();
	}
	
	public boolean isNone() {
		return mOfflineMedia.isNone();
	}
	
	public boolean isWaiting() {
		return mOfflineMedia.isWaiting();
	}

	public boolean isLoading() {
		return mOfflineMedia.isLoading();
	}

	public boolean isPaused() {
		return mOfflineMedia.isPaused();
	}

	// really finish or can not recover
	public boolean isFinished() {
		return mOfflineMedia.isFinished();
	}

	public boolean isError() {
		return mOfflineMedia.isError();
	}

	public boolean isUnrecovrableError() {
		return mOfflineMedia.isUnrecovrableError();
	}
	
	public void idle() {
		DKLog.d(TAG, "idle");
		mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_IDLE;
		onStatusChange();
	}
	
	// TODO: read setting
	private boolean canUseNetwork() {
		return Network.isWifiConnected(DKApp.getAppContext());
	}

	public void start() {
		DKLog.d(TAG, "start");
		if (!canUseNetwork()) {
			mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR;
			onStatusChange();
			return;
		}
		mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_INIT;
		onStatusChange();
		if (TextUtils.isEmpty(mOfflineMedia.remoteUrl)) {
			getUrl();
		} else {
			download();
		}
	}
	
	public void pause() {
		DKLog.d(TAG, "pause");
		mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_PAUSE;
		onStatusChange();
		
		stopTaskIfNeeded();
	}
	
	public void stop() {
		DKLog.d(TAG, "stop");
		mOfflineMedia.status = MediaConstantsDef.OFFLINE_NONE;
		onStatusChange();
		stopTaskIfNeeded();
	}
	
	private void stopTaskIfNeeded() {
		if (mLoader != null) {
			mLoader.stop();
			DKLog.d(TAG, "Loader stop");
		}
	}
	
	private void download() {
		final int type = mOfflineMedia.type;
		if (type == MediaConstantsDef.SOURCE_TYPE_MEDIA) {
			downloadMedia();
		} else if (type == MediaConstantsDef.SOURCE_TYPE_M3U8) {
			downloadM3U8();
		}
	}

	private void downloadMedia() {
		DKLog.d(TAG, "downloadMedia");
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final String path = getLocalPath(mOfflineMedia);
				if (path == null) {
					mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_FILE_ERROR;
					postStatusChange();
					return;
				}
				final int length = getMediaLength(path);
				final int offset = getMediaOffset(path);
				if (length < 0 || offset < 0 || length < offset) {
					mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_FILE_ERROR;
					postStatusChange();
					return;
				}
				
				if (!path.equals(mOfflineMedia.localPath)) {
					mOfflineMedia.localPath = path;
					postMediaChange();
				}
				if (mOfflineMedia.completeSize != length - offset) {
					mOfflineMedia.completeSize = length - offset;
					postMediaChange();
				}
				stopTaskIfNeeded();
				mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_LOADING;
				postStatusChange();
				DKLog.d(TAG, "localPath: " + mOfflineMedia.localPath + ", localStart: " + length + ", remoteStart: " + (length - offset));
				mLoader = new Downloader(mOfflineMedia.remoteUrl, mOfflineMedia.localPath, length - offset, length, OfflineDownloader.this);
				mLoader.download();
                saveCongfigFile();
			}
		});
	}
	
	// TODO: in non-ui thread
	private static int getMediaOffset(String path) {
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(path, "rwd");
			randomAccessFile.seek(4);
			return randomAccessFile.readInt();
		} catch (Exception e) {
			DKLog.d(TAG, "get media file failed");
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (Exception e) {
					DKLog.d(TAG, "close media file failed");
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	// TODO: in non-ui thread
	private static int getMediaLength(String path) {
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(path, "rwd");
			return Long.valueOf(randomAccessFile.length()).intValue();
		} catch (Exception e) {
			DKLog.d(TAG, "get media file failed");
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (Exception e) {
					DKLog.d(TAG, "close media file failed");
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	private void downloadM3U8() {
		DKLog.d(TAG, "downloadM3U8");
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				final String path = getLocalPath(mOfflineMedia);
				if (path == null) {
					mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_FILE_ERROR;
					postStatusChange();
					return;
				}
				
				if (!path.equals(mOfflineMedia.localPath)) {
					mOfflineMedia.nLineFinish = -1;
					mOfflineMedia.localPath = path;
					postMediaChange();
				}

				if (mOfflineMedia.status != MediaConstantsDef.OFFLINE_STATE_LOADING) {
					postStatusChange();
					return;
				}
				
				DKLog.d(TAG, "lineFinish: " + mOfflineMedia.nLineFinish + ", fileSize: " + mOfflineMedia.fileSize);
				if (mOfflineMedia.nLineFinish < 0) {// first file
					//list file
					stopTaskIfNeeded();
					DKLog.d(TAG, "remoteUrl: " + mOfflineMedia.remoteUrl + ", localPath: " + mOfflineMedia.localPath);
					mLoader = new Downloader(mOfflineMedia.remoteUrl, mOfflineMedia.localPath, 0, 0, OfflineDownloader.this);
					mLoader.download();
				} else {// other files
					final String url = getNextUrlfromM3U8();
					if (url == null) {
						mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_FINISH;
						postStatusChange();
						return;
					}
					final String localPath = getM3U8LocalPath();
					DKLog.d(TAG, "remoteUrl: " + url + ", localPath: " + localPath);
					stopTaskIfNeeded();
					mLoader = new Downloader(url, localPath, 0, 0, OfflineDownloader.this);
					mLoader.download();
				}
				saveCongfigFile();
			}
		});
	}
	
	   private Runnable mSaveConfigTask = new Runnable() {
	        @Override
	        public void run() {
	            saveCongfigFile();
	        }
	    };
	    
	    private void saveCongfigFile() {
	        if (!Util.isEmpty(mOfflineMedia.localPath)) {
	            String path = mOfflineMedia.localPath.substring(0,
	                    mOfflineMedia.localPath.lastIndexOf(File.separator)+1) + DBUtil.OFFLINE_MEDIA_CONFIG;
	            ObjectStore.writeObject(path, mOfflineMedia);
	        }
	    }

	
	private void getUrl() {
		DKLog.d(TAG, "getUrl mediaId: " + mOfflineMedia.mediaId + " ci: " + mOfflineMedia.episode + " source: " + mCurSource);
		mOfflineMedia.remoteUrl = null;
		MediaUrlForPlayerUtil mediaUrlForPlayerUtil = new MediaUrlForPlayerUtil(DKApp.getAppContext());
		mediaUrlForPlayerUtil.setObserver(this);
		mediaUrlForPlayerUtil.getMediaUrlForPlayer(mOfflineMedia.mediaId, mOfflineMedia.episode, mCurSource, null);
		mGetUrlRetry++;
	}
	
	@Override
	public void onUrlUpdate(int mediaId, int ci, String mediaUrl, String html5Url) {
		DKLog.d(TAG, "onUrlUpdate mediaId: " + mediaId + " ci: " + ci + "\n" + "html5Url: " + 
		        html5Url + "\n" + "mediaUrl: " + mediaUrl);
		mGetUrlRetry = 0;
//		mediaUrl = "http://hot.vrs.sohu.com/ipad1750874_4611149491589_4961195.m3u8?plat=17&vid=1750874&uid=1400815177979159&plat=17&pt=5&prod=h5&pg=1&eye=0&cateCode=101;101103;101109";
		DKLog.d(TAG, "state: " + mOfflineMedia.status);
		if (mOfflineMedia.status == MediaConstantsDef.OFFLINE_STATE_INIT) {
//			if (mOfflineMedia.mediaId == mediaId && mOfflineMedia.episode == ci) {
				if (mOfflineMedia.remoteUrl == null || !mOfflineMedia.remoteUrl.equals(mediaUrl)) {
					mOfflineMedia.remoteUrl = mediaUrl;
					mOfflineMedia.source = mCurSource;
					mOfflineMedia.completeSize = 0;
					mOfflineMedia.nLineFinish = -1;
					if (mOfflineMedia.remoteUrl.contains(".m3u")) {
						mOfflineMedia.type = MediaConstantsDef.SOURCE_TYPE_M3U8;
					} else {
						mOfflineMedia.type = MediaConstantsDef.SOURCE_TYPE_MEDIA;
					}
					onMediaChange();
				}
//			}
			download();
		}
	}

	@Override
	public void onError() {
		if (mGetUrlRetry >= MAX_GETURL_RETRY) {
			mGetUrlRetry = 0;
			if (mSourcelist == null) {
				getMediaDetailInfo();
			} else {
				int n = mSourcelist.length;
				for (int i=0; i<n; i++) {
					if (mCurSource == mSourcelist[i]) {
						mCurSource = mSourcelist[(i+1)%n];
						if (mCurSource == mOfflineMedia.source) {
							mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR;
							onStatusChange();
						}
					}
				}
			}
		} else if (mOfflineMedia.status == MediaConstantsDef.OFFLINE_STATE_INIT) {
			getUrl();
		}
	}

	@Override
	public void onReleaseLock() {
		DKLog.d(TAG, "release lock");
	}
	
	
	private void getMediaDetailInfo() {
		DKLog.d(TAG, "getMediaDetailInfo mediaId: " + mOfflineMedia.mediaId);
		mOfflineMedia.source = -1;
		mDetailInfoRequest = DKApi.getMediaDetailInfo(mOfflineMedia.mediaId, true, MediaFeeDef.MEDIA_ALL, null, this);
		mDetailInfoRequest.setShowResultDesc(false);	
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
		DKLog.d(TAG, "onRequestCompleted");
		if (response instanceof MediaDetailInfoResponse) {
			MediaDetailInfo2 detailInfo2 = null;
			if (response.isSuccessful()) {
				MediaDetailInfoResponse detailResponse = (MediaDetailInfoResponse) response;
				detailInfo2 = detailResponse.data;
				int a = 0;
				int b = detailInfo2.mediaciinfo.videos.length - 1;
				int c = -1;
				while (a <= b) {
					c = (a + b)/2;
					if (mOfflineMedia.episode < detailInfo2.mediaciinfo.videos[c].ci) {
						b = c - 1;
					} else if (mOfflineMedia.episode > detailInfo2.mediaciinfo.videos[c].ci) {
						a = c + 1;
					} else {
						mSourcelist = detailInfo2.mediaciinfo.videos[c].ci_available_download_source;
						break;
					}
				}
			} else {
				mDetailInfoRequest = null;
			}
			
			if (mSourcelist == null || mSourcelist.length <= 0) {
				mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
				onStatusChange();
			} else if (detailInfo2 != null) {
				mCurSource = mSourcelist[0];				
				mOfflineMedia.playLength = detailInfo2.mediainfo.playlength;
				if (mOfflineMedia.status == MediaConstantsDef.OFFLINE_STATE_INIT) {
					getUrl();
				}
			}
		} else {
			mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
			onStatusChange();
		}
	}

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	}

	// TODO: in non-ui thread
	private static String getLocalPath(OfflineMedia media) {
		if (media == null) {
			return null;
		}
		
		if (media.localPath != null && new File(media.localPath).exists()) {
			return media.localPath;
		}
		
		AppEnv appEnv = DKApp.getSingleton(AppEnv.class);
		if (appEnv == null) {
			DKLog.d(TAG, "appEnv: " + appEnv);
			return null;
		}
		String path = appEnv.getOfflineDir();
		if (TextUtils.isEmpty(path)) {
			DKLog.d(TAG, "offline dir: " + path);
			return null;
		}
		path += media.getKey();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		path += File.separator;
		path += media.getKey();
		DKLog.d(TAG, "offline path: " + path);
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(path, "rwd");
			randomAccessFile.writeByte('m');
			randomAccessFile.writeByte('i');
			randomAccessFile.writeByte('u');
			randomAccessFile.writeByte('i');
			byte[] buf = new byte[56];
			randomAccessFile.writeInt(buf.length + 12);
			randomAccessFile.writeInt(DBUtil.OFFLINE_FILE_VERSION);
			randomAccessFile.write(buf);
			
			return path;
		} catch (Exception e) {
			DKLog.d(TAG, "get local file: " + e.toString());
			e.printStackTrace();
			return null;
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (Exception e2) {
					DKLog.d(TAG, "close local file failed");
					e2.printStackTrace();
				}
			}
		}
	}
	
	// TODO: in non-ui thread
	private int getM3U8ListLines() {
		int nline = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(mOfflineMedia.localPath)));
			while(reader.readLine() != null) {
				nline++;
			}
		} catch (Exception e) {
			DKLog.d(TAG, "getM3U8ListLines failed");
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		DKLog.d(TAG, "getM3U8ListLines: " + nline);
		return nline;
	}

	// TODO: in non-ui thread
	private String getNextUrlfromM3U8() {
		RandomAccessFile randomAccessFile = null;
		String url = null;
		try {
			randomAccessFile = new RandomAccessFile(mOfflineMedia.localPath, "rwd");
			String line = "";
			
			String rootPath = mOfflineMedia.remoteUrl.substring(0, mOfflineMedia.remoteUrl.lastIndexOf("/")+1);
			int nline = 0;
			while((line = randomAccessFile.readLine()) != null) {
				if (nline >= mOfflineMedia.nLineFinish) {
					if (line.length() > 0 && !line.startsWith("#")) {
						if (line.startsWith("http")) {
							url = line;
						} else {
							url = rootPath + line;
						}
						mOfflineMedia.nLineFinish = nline;
					}
				}
				nline++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return url;
	}
	
	private String getM3U8LocalPath() {
		return mOfflineMedia.localPath + "_" + mOfflineMedia.nLineFinish;
	}

	// TODO: in non-ui thread
	private int changeM3U8Uri() {
		int nline = 0;
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(mOfflineMedia.localPath, "rwd");
			String line = "";
			long startPos = 0;
			StringBuffer stringBuffer = new StringBuffer(getM3U8LocalPath());
			while((line = randomAccessFile.readLine()) != null) {
				if (nline == mOfflineMedia.nLineFinish - 1) {
					startPos = randomAccessFile.getFilePointer();
				} else if (nline > mOfflineMedia.nLineFinish) {
					stringBuffer.append(System.getProperty("line.separator"));
					stringBuffer.append(line);
				}
				nline++;
			}
			randomAccessFile.setLength(startPos + stringBuffer.length());
			randomAccessFile.seek(startPos);
			randomAccessFile.write(stringBuffer.toString().getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return nline;
	}

	@Override
	public void onDownloadSucceed() {
		final int type = mOfflineMedia.type;
		DKLog.d(TAG, "succeed, type: " + type);
		if (type == MediaConstantsDef.SOURCE_TYPE_MEDIA) {
			mOfflineMedia.status = MediaConstantsDef.OFFLINE_STATE_FINISH;
			onStatusChange();
			mExecutor.execute(mSaveConfigTask);
		} else if (type == MediaConstantsDef.SOURCE_TYPE_M3U8) {
			if (mOfflineMedia.nLineFinish < 0) {
				mOfflineMedia.fileSize = getM3U8ListLines();
			} else {
				mOfflineMedia.fileSize = changeM3U8Uri();
				mOfflineMedia.completeSize = mOfflineMedia.nLineFinish;
				mOfflineMedia.completeSize += mCompleteSize;
//				LocalMediaInfo.getInstance().onDownloadUpdate(mKey, mOfflineMedia);
			}
			DKLog.d(TAG, "onDownloadStop m3u8: " + "complete: " + mCompleteSize + "\n" + "total complete: " + mOfflineMedia.completeSize);
			mOfflineMedia.nLineFinish++;
//			onMediaChange();
//			LocalMediaInfo.getInstance().updateBasicInfo(mKey, mOfflineMedia);
			downloadM3U8();
		}
	}

	@Override
	public void onDownloadFail(int nState) {
		DKLog.d(TAG, "fail, state: " + nState);
		mOfflineMedia.status = nState;
		switch (nState) {
		case MediaConstantsDef.OFFLINE_STATE_FILE_ERROR:
		case MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR:
			onStatusChange();
			break;
		case MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR:
			if (mDownloadRetry < MAX_DOWNLOAD_RETRY) {
				mDownloadRetry++;
				download();
			} else {
				onStatusChange();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDownloadCancel() {
	}

	@Override
	public void onDownloadProgress(int nCompleteSize) {
		final int type = mOfflineMedia.type;
		DKLog.d(TAG, "progress, completed: " + nCompleteSize + ", diff: "
				+ (nCompleteSize - mOfflineMedia.fileSize) + ", type: " + type);
		if (type == MediaConstantsDef.SOURCE_TYPE_M3U8) {
			mCompleteSize = nCompleteSize;
		} else {
			if (mOfflineMedia.completeSize != nCompleteSize) {
				mOfflineMedia.completeSize = nCompleteSize;
				onProgressChange();
			}
		}
	}

	@Override
	public void onContentLength(int nContentLength) {
		final int type = mOfflineMedia.type;
		DKLog.d(TAG, "onContentLength: " + nContentLength + ", type: " + type);
		if (type == MediaConstantsDef.SOURCE_TYPE_MEDIA) {
			if (mOfflineMedia.fileSize != nContentLength) {
				mOfflineMedia.fileSize = nContentLength;
				mExecutor.execute(mSaveConfigTask);
				onLengthChange();
			}
		}
	}
	
	private void onMediaChange() {
//		DKLog.i(TAG, "onMediaChange");
		if (mListener != null) {
			mListener.onMediaChange(OfflineDownloader.this);
		}
	}
	
	private void postMediaChange() {
//		DKLog.i(TAG, "postMediaChange");
		if (mListener != null) {
			UI_HANDLER.post(new Runnable() {
				@Override
				public void run() {
					mListener.onMediaChange(OfflineDownloader.this);
				}
			});
		}
	}
	
	private void onStatusChange() {
		DKLog.i(TAG, "onStatusChange, status: " + mOfflineMedia.status);
		if (mListener != null) {
			mListener.onStatusChange(OfflineDownloader.this);
		}
	}
	
	private void postStatusChange() {
		DKLog.i(TAG, "postStatusChange, status: " + mOfflineMedia.status);
		UI_HANDLER.post(new Runnable() {
		    @Override
		    public void run() {
		        if (mListener != null) {
		            mListener.onStatusChange(OfflineDownloader.this);
		        }
		    }
		});
	}

	private void onProgressChange() {
		DKLog.i(TAG, "onProgressChange, completeSize: " + mOfflineMedia.completeSize);
		if (mListener != null) {
			mListener.onProgressChange(OfflineDownloader.this);
		}
	}

	private void onLengthChange() {
		DKLog.i(TAG, "onLengthChange, fileSize: " + mOfflineMedia.fileSize);
		if (mListener != null) {
			mListener.onLengthChange(OfflineDownloader.this);
		}
	}

	private OfflineLoaderListener mListener;
	public void setOfflineLoaderListener(OfflineLoaderListener listener) {
		mListener = listener;
	}

	/**
	 * Every time completeSize or status changed, we should notify to refresh UI. 
	 * 
	 * We store completeSize into DB only when we read from the local file.
	 * 
	 * Every time other information changed, we should save record into DB. 
	 * 
	 * @author zzc
	 *
	 */
	public static interface OfflineLoaderListener {

		public void onMediaChange(OfflineDownloader loader);

		public void onStatusChange(OfflineDownloader loader);

		public void onProgressChange(OfflineDownloader loader);

		public void onLengthChange(OfflineDownloader loader);

	}
	
}
