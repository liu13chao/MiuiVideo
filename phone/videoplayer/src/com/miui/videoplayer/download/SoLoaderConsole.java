package com.miui.videoplayer.download;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.common.IOUtil;

import android.content.Context;
import android.os.AsyncTask;


public class SoLoaderConsole extends SourceLoaderConsole {

	private OnSoStatusListener mListener;
	
	public SoLoaderConsole(Context context,
			String remoteUrl, String localPath) {
		super(context, remoteUrl, localPath);
	}
	
	@Override
	protected void onLoadComplete(String path) {
		if(mListener != null){
			mListener.onSoDownloadComplete();
		}
		new PrepareSoTask(path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	protected void downloadStart() {
		if(mListener != null){
			mListener.onSoDownloadStart();
		}
	}
	
	@Override
	protected void downloadProgress(int completed, int total) {
		if(mListener != null){
			mListener.onSoDownloadProgress(completed, total);
		}
	}
	
	@Override
	protected void downloadError(int error) {
		if (error == Constants.OFFLINE_STATE_PAUSE) {
			return;
		}
		if(mListener != null){
			mListener.onSoDownloadError(error);
		}		
	}
	
	private class PrepareSoTask extends AsyncTask<Void, Void, Boolean> {
		private final String mPath;
		
		public PrepareSoTask(String path) {
			mPath = path;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			final int lastSlash = mPath.lastIndexOf(File.separator);
			if (lastSlash < 0) {
				return false;
			}
			try {
				return IOUtil.upZipFile(new File(mPath), mPath.substring(0, lastSlash));
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
	    protected void onPostExecute(Boolean result) {
			if (mListener != null) {
				if (result) {
					mListener.onSoReady(mPath.substring(0, mPath.lastIndexOf(File.separator)));
				} else {
					mListener.onSoNotReady();
				}
			}
	    }
		
	}
	
	public void setOnSoStatusListener(OnSoStatusListener listener) {
		mListener = listener;
	}
	
	public static interface OnSoStatusListener{
		public void onSoReady(String path);
		public void onSoNotReady();
		public void onSoDownloadComplete();
		public void onSoDownloadStart();
		public void onSoDownloadProgress(int completed, int total);
		public void onSoDownloadError(int error);
	}

	@Override
	protected String getStartMessage() {
//		return mContext.getResources().getString(R.string.vp_plugin_message);
		return null;
	}

}
