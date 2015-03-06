package com.miui.video.offline;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.model.ContentLengthRetriever;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.xiaomi.common.util.Network;

public class Downloader {
	private static final String TAG = "Downloader";

	private static final int BUFFER_SIZE = 4096;

	private static final int TICK_SIZE = 100;

	private final String mRemoteUrl;
	private final String mLocalPath;
	private final int mLocalStartPos;
	private int mRemoteStartPos;

	private final DownloadCallback mCallback;

	private AtomicBoolean mAtomPause = new AtomicBoolean(false);

	private static final Handler UI_HANDLER = new DownloadHandler(
			Looper.getMainLooper());

	private static class DownloadHandler extends Handler {

		public DownloadHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			DownloadCallback callback = (DownloadCallback) msg.obj;
			if (callback == null) {
				return;
			}
			switch (msg.what) {
			case MSG_STATE:
				final int state = msg.arg1;
				if (MediaConstantsDef.OFFLINE_STATE_FINISH == state) {
					callback.onDownloadSucceed();
				} else {
					callback.onDownloadFail(state);
				}
				break;
			case MSG_PROGRESS:
				callback.onDownloadProgress(msg.arg1);
				break;
			case MSG_LENGTH:
				callback.onContentLength(msg.arg1);
				break;
			}
		}
	}

	public Downloader(String url, String path, int remoteStart, int localStart, DownloadCallback callback) {
		mRemoteUrl = url;
		mLocalPath = path;
		mRemoteStartPos = remoteStart;
		mLocalStartPos = localStart;
		mCallback = callback;
	}

	public static interface DownloadCallback {
		public void onDownloadSucceed();

		public void onDownloadFail(int nState);

		public void onDownloadCancel();

		public void onDownloadProgress(int nCompleteSize);

		public void onContentLength(int nContentLength);
	}

	private static final int MSG_STATE = 0;
	private static final int MSG_PROGRESS = 1;
	private static final int MSG_LENGTH = 2;

//	private static final String KEY_PROGRESS = "progress";
//	private static final String KEY_LENGTH = "length";

	private void postState(int state) {
		Message msg = Message.obtain();
		msg.obj = mCallback;
		msg.what = MSG_STATE;
		msg.arg1 = state;
		msg.setTarget(UI_HANDLER);
		msg.sendToTarget();
	}

	private void postProgress(int completed) {
		Message msg = Message.obtain();
		msg.obj = mCallback;
		msg.what = MSG_PROGRESS;
		msg.arg1 = completed;
		msg.setTarget(UI_HANDLER);
		msg.sendToTarget();
	}

	private void postLength(int length) {
		Message msg = Message.obtain();
		msg.obj = mCallback;
		msg.what = MSG_LENGTH;
		msg.arg1 = length;
		msg.setTarget(UI_HANDLER);
		msg.sendToTarget();
	}

	public void stop() {
		mAtomPause.set(true);
	}

	/**
	 * call in non-ui thread
	 */
	public int download() {
		if (TextUtils.isEmpty(mLocalPath)) {
			postState(MediaConstantsDef.OFFLINE_STATE_FILE_ERROR);
			return MediaConstantsDef.OFFLINE_STATE_FILE_ERROR;
		}
		if (TextUtils.isEmpty(mRemoteUrl)) {
			postState(MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR);
			return MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
		}
		int fileSize = 0;
		RandomAccessFile randomAccessFile = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
		    if(mRemoteStartPos != 0){
                ContentLengthRetriever retriever = new ContentLengthRetriever(mRemoteUrl);
                fileSize = retriever.get(15000);
            }
            URL url = new URL(mRemoteUrl);
            conn = (HttpURLConnection)url.openConnection();
            if (mRemoteStartPos == 0) {
                fileSize = conn.getContentLength();
            }else{
                conn.setRequestProperty("Range", "bytes=" + mRemoteStartPos + "-");             
            }
            DKLog.d(TAG, "file size: " + fileSize);
            if(fileSize > 0){
                postLength(fileSize);
            }
            if(fileSize > 0 && mRemoteStartPos == fileSize){
                postState(MediaConstantsDef.OFFLINE_STATE_FINISH);
                return MediaConstantsDef.OFFLINE_STATE_FINISH;
            }
            is = conn.getInputStream();
//            HttpGet httpGet = new HttpGet(URI.create(mRemoteUrl));
//			httpGet.addHeader("Range", "bytes=" + mRemoteStartPos + "-");
//			HttpResponse response = new DefaultHttpClient().execute(httpGet);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_PARTIAL_CONTENT) {
//				DKLog.e(TAG, "statusCode: " + statusCode);
//				postState(MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR);
//				return MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
//			}
//			HttpEntity entity = response.getEntity();
//			if (entity == null) {
//				DKLog.e(TAG, "entity: " + entity);
//				postState(MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR);
//				return MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR;
//			}
//			final int contentLength = Long.valueOf(entity.getContentLength() + mRemoteStartPos).intValue();
//			DKLog.d(TAG, "content length: " + contentLength);
//			postLength(contentLength);
//			is = entity.getContent();
			if (is == null) {
				DKLog.e(TAG, "" + null);
                postState(MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR);
                return MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR;
			}
			randomAccessFile = new RandomAccessFile(mLocalPath, "rwd");
			randomAccessFile.seek(mLocalStartPos);
			DKLog.d(TAG, "local start at: " + randomAccessFile.getFilePointer());
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = -1;
			int tick = 0;
			while ((length = is.read(buffer)) != -1) {
				randomAccessFile.write(buffer, 0, length);
				mRemoteStartPos += length;
                if(mRemoteStartPos >= fileSize){
                    break;
                }
 				tick++;
				if (tick % TICK_SIZE == 0) {
					postProgress(mRemoteStartPos);
					if (!Network.isWifiConnected(DKApp.getAppContext())) {
						mAtomPause.set(true);
					}
				}
				if (mAtomPause.get()) {
					break;
				}
			}
			if (mAtomPause.get()) {
				postState(MediaConstantsDef.OFFLINE_STATE_PAUSE);
				return MediaConstantsDef.OFFLINE_STATE_PAUSE;
			} else if (mRemoteStartPos >= fileSize && fileSize > 0) {
				postState(MediaConstantsDef.OFFLINE_STATE_FINISH);
				return MediaConstantsDef.OFFLINE_STATE_FINISH;
			}  else {
				postState(MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR);
				return MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR;
			}
		} catch (Exception e) {
			DKLog.e(TAG, "DownloadThread: " , e);
			// Handle Errors
				postState(MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR);
				return MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR;
		} finally {
		    Util.closeSafely(randomAccessFile);
		    Util.closeSafely(is);
		    Util.disconnectSafely(conn);
		}
	}

}
