package com.miui.videoplayer.download;

import com.miui.video.offline.Downloader;
import com.miui.video.offline.Downloader.DownloadCallback;

import android.app.AlertDialog;
import android.content.Context;

public abstract class LoaderConsole {

	protected final Context mContext;
	protected final Downloader mDownloader;
	protected AlertDialog mAlertDialog;
	
	private String mLocalPath;
	private int mFileSize = 0;
	
	public LoaderConsole(Context context, String remoteUrl, String localPath) {
		mContext = context;
		mDownloader = new Downloader(remoteUrl, localPath, 0, 0, mCallback);
		mLocalPath = localPath;
	}

	public void start() {
		downloadStart();
	}
	
	public void startDownload() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mDownloader.download();
			}

		}).start();
	}
	
	public void stopDownload() {
		mDownloader.stop();
	}

	private DownloadCallback mCallback = new DownloadCallback() {

		@Override
		public void onContentLength(int nContentLength) {
			mFileSize = nContentLength;
		}

		@Override
		public void onDownloadSucceed() {
			downloadComplete(mLocalPath);
		}

		@Override
		public void onDownloadFail(int nState) {
			downloadError(nState);
		}

		@Override
		public void onDownloadCancel() {
		}

		@Override
		public void onDownloadProgress(int nCompleteSize) {
			downloadProgress(nCompleteSize, mFileSize);
		}
	};
	
	protected void hideDialog() {
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
		}
	}

	protected abstract void downloadStart();

	protected abstract void downloadProgress(int completed, int total);

	protected abstract void downloadComplete(String path);

	protected abstract void downloadError(int error);
}
