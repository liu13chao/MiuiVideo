package com.miui.videoplayer.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.miui.video.R;
import com.miui.videoplayer.common.Constants;

public class ApkLoaderConsole extends SourceLoaderConsole {

	public final static int REQUEST_INSTALL = 100;
	public final static int REQUEST_UNINSTALL = 101;
	private OnApkStatusListener mListener;
	
	public ApkLoaderConsole(Context context, 
			String remoteUrl, String localPath) {
		super(context, remoteUrl, localPath);
	}
	
	@Override
	protected void downloadStart() {
		if(mListener != null){
			mListener.onApkDownloadStart();
		}
	}
	
	@Override
	protected void downloadProgress(int completed, int total) {
		if(mListener != null){
			mListener.onApkDownloadProgress(completed, total);
		}		
	}
	
	@Override
	protected void downloadError(int error) {
		if (error == Constants.OFFLINE_STATE_PAUSE) {
			return;
		}
		if(mListener != null){
			mListener.onApkDownloadError(error);
		}	
	}
	
	private void installAPK(String path) {
	    if(path == null){
	        return;
	    }
	    if(!path.startsWith("file://")){
	        path  = "file://" + path;
	    }
	    try{
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.setDataAndType(Uri.parse(path),
	                "application/vnd.android.package-archive");
	        mContext.startActivity(intent);     
	    }catch(Exception e){
	    }
	}

	@Override
	protected void onLoadComplete(String path) {
		if(mListener != null){
			mListener.onApkDownloadComplete();
		}
		installAPK(path);
	}

	public void setOnApkStatusListener(OnApkStatusListener listener) {
		mListener = listener;
	}
	
	public static interface OnApkStatusListener{
		public void onApkDownloadComplete();
		public void onApkDownloadStart();
		public void onApkDownloadProgress(int completed, int total);
		public void onApkDownloadError(int error);
	}
	
	@Override
	protected String getStartMessage() {
		return mContext.getResources().getString(R.string.vp_client_message,
				mInfo.name);
	}

}
