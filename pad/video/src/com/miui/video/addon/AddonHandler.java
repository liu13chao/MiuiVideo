/**
 *   Copyright(c) 2012 DuoKan TV Group   
 *  
 *   @author dz
 *    
 *   @date 2014-05-10 
 */
package com.miui.video.addon;


import java.io.File;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.addon.AddonManager;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.helper.JobRunner;
import com.miui.video.model.AppEnv;
import com.miui.video.offline.Downloader;
import com.miui.video.offline.Downloader.DownloadCallback;
import com.miui.video.type.AddonInfo;
import com.miui.video.util.Util;
import com.xiaomi.common.util.Strings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class AddonHandler {

	public final static int REQUEST_INSTALL = 100;
	public final static int REQUEST_UNINSTALL = 101;

	public interface AddonHandlerInterface {
		public void onInstall();
		public void onInstallComplete();
	}
		
	private AddonHandlerInterface mAddonHandlerInterface;
	private Activity mActivity;
	private AddonInfo mAddonInfo;
	private Downloader mDownloader;
	private AlertDialog mAlertDialog;
	
	private int mFileSize = 0;
	
	private DownloadCallback mCallback = new DownloadCallback() {
		
		@Override
		public void onDownloadSucceed() {
			onInstallComplete();
		}
		
		@Override
		public void onDownloadProgress(int nCompleteSize) {
			TextView textView = (TextView) mAlertDialog.findViewById(R.id.hint_text);
			if (textView != null) {
				String disc = mAddonInfo.getName()
						+ mActivity.getResources().getString(R.string.loading)
						+ Strings.formatPercent(nCompleteSize, mFileSize);
				textView.setText(disc);
			}
		}
		
		@Override
		public void onDownloadFail(int nState) {
			delFile();
			showErrorDialog(nState);
		}
		
		@Override
		public void onDownloadCancel() {
			delFile();
		}
		
		@Override
		public void onContentLength(int nContentLength) {
			mFileSize = nContentLength;
		}
	};
		
	public AddonHandler(Activity activity, AddonHandlerInterface addonHandlerInterface) {
		mActivity = activity;
		mAddonHandlerInterface = addonHandlerInterface;
	}
	
	public void onAddonClick(AddonInfo addonInfo) {
		if (addonInfo == null)
			return;
		AddonInfo info = AddonManager.getInstance().getById(addonInfo.getId());
		if (info == null) {
			install(addonInfo);
		} else if (info.getVersion() < addonInfo.getVersion()) {
			upgrade(addonInfo);
		} else {
			start(info);
		}
	}
	
	private void start(AddonInfo addonInfo) {
		if (addonInfo == null)
			return;
		
//		if (Util.isEmpty(addonInfo.getPackageName())) {
//			if (Util.isEmpty(addonInfo.getLocalPath()))
//				return;
//			Intent intent = new Intent(mActivity, AddonProxyActivity.class);
//			intent.putExtra(AddonProxyActivity.ADDON_CLASS, addonInfo.getMainClassName());
//			intent.putExtra(AddonProxyActivity.ADDON_PATH, addonInfo.getLocalPath());
//			mActivity.startActivity(intent);
//		} else if (!Util.isEmpty(addonInfo.getMainClassName())){
//			Intent intent = new Intent();
//			intent.setClassName(addonInfo.getPackageName(), addonInfo.getMainClassName());
//			mActivity.startActivity(intent);
//		}
	}
	
	private void install(AddonInfo addonInfo) {
		mAddonInfo = addonInfo;
		showInstallDialog();
	}

	private void upgrade(AddonInfo addonInfo) {
		mAddonInfo = addonInfo;
		showUpdateDialog();
	}
	

	private void showInstallDialog() {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();
		if (mActivity == null || mAddonInfo == null)
			return;
		mAlertDialog = new AlertDialog.Builder(mActivity)
        .setCancelable(false)
        .setTitle(mAddonInfo.getName())        
        .setMessage(mAddonInfo.getDescription())
        .setPositiveButton(R.string.install_comfirm, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			//	dialog.dismiss();
				onInstall();
			}
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		 })
        .create();
		mAlertDialog.show();
	}
	
	private void showLoadingDialog() {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();		
		if (mActivity == null || mAddonInfo == null)
			return;
		Resources res = mActivity.getResources();
		
		String disc = mAddonInfo.getName() + res.getString(R.string.loading);
		View view = View.inflate(mActivity, R.layout.horizontal_load_view, null);
		TextView textView = (TextView) view.findViewById(R.id.hint_text);
		textView.setText(disc);
		
		mAlertDialog = new AlertDialog.Builder(mActivity)
        .setCancelable(false)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mDownloader != null) {
					mDownloader.stop();
				}
				dialog.dismiss();
			}
		 })
        .setView(view)
        .create();
		mAlertDialog.show();
	}
	
	private void showCompleteDialog() {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();
		if (mActivity == null || mAddonInfo == null)
			return;
		Resources res = mActivity.getResources();
		String disc = res.getString(R.string.addon_install_complete_disc);
		disc = String.format(disc, mAddonInfo.getName());
		mAlertDialog = new AlertDialog.Builder(mActivity)
        .setCancelable(false)
        .setTitle(mAddonInfo.getName())
        .setPositiveButton(R.string.start_addon, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				start(mAddonInfo);
			}
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		 })
        .setMessage(disc)
        //.setView(alertView)
        .create();
		mAlertDialog.show();
	}
	
	private void showErrorDialog(int nError) {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();
		
		if (mActivity == null || mAddonInfo == null)
			return;
		
		String disc = "";
		Resources res = mActivity.getResources();
		if (nError == MediaConstantsDef.OFFLINE_STATE_FILE_ERROR) {
			disc = res.getString(R.string.download_file_fail);
		} else if (nError == MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR) {
			disc = res.getString(R.string.download_geturl_fail);
		}
		
		mAlertDialog = new AlertDialog.Builder(mActivity)
        .setCancelable(false)
        .setTitle(mAddonInfo.getName())
        .setMessage(disc)
        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onInstall();
			}
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		 })
        .create();
		mAlertDialog.show();
	}
	
	private void showUpdateDialog() {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();
		if (mActivity == null || mAddonInfo == null)
			return;
		Resources res = mActivity.getResources();
		String disc = res.getString(R.string.addon_update_desc);
		mAlertDialog = new AlertDialog.Builder(mActivity)
        .setCancelable(false)
        .setTitle(mAddonInfo.getName())
        .setMessage(disc)
        .setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onInstall();
			}
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				start(AddonManager.getInstance().getById(mAddonInfo.getId()));
			}
		 })
        .create();
		mAlertDialog.show();
	}

	private void onInstall() {
		if (mAddonHandlerInterface != null) {
			mAddonHandlerInterface.onInstall();
		}
		showLoadingDialog();

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mDownloader != null) {
					mDownloader.stop();
				}
				mDownloader = new Downloader(mAddonInfo.getPackageUrl(),
						getLocalPath(mAddonInfo), 0, 0, mCallback);
				mDownloader.download();
			}
		}).start();
	}
	
	private String getLocalPath(AddonInfo info) {
		if (info == null) {
			return null;
		}
		if (!TextUtils.isEmpty(info.getLocalPath())) {
			return info.getLocalPath();
		}
		String dir = DKApp.getSingleton(AppEnv.class).getAddonDir();
		if (TextUtils.isEmpty(dir)) {
			return null;
		}
		String url = info.getPackageUrl();
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		return dir + url.substring(url.lastIndexOf(File.separator));
	}

	private void onInstallComplete() {
		AddonManager.getInstance().add(mAddonInfo);
		if (!Util.isEmpty(mAddonInfo.getPackageName())) {
			if (mAlertDialog != null) {
				mAlertDialog.dismiss();
			}
			installAPK(mAddonInfo);
		} else {
			showCompleteDialog();
			if (mAddonHandlerInterface != null) {
				mAddonHandlerInterface.onInstallComplete();
			}
		}
	}
	
	private void delFile() {
		JobRunner.postJob(new DeleteFileRunnable(mAddonInfo.getLocalPath()));
	}

	private class DeleteFileRunnable implements Runnable {
		private String filePath;
		public DeleteFileRunnable(String filePath) {
			this.filePath = filePath;
		}
		@Override
		public void run() {
			try {
				File file = new File(filePath);
				if (file.isFile()) {
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void installAPK(AddonInfo addonInfo) {
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(new File(addonInfo.getLocalPath())), "application/vnd.android.package-archive");
		mActivity.startActivityForResult(intent, REQUEST_INSTALL);

//		Uri mPackageURI = Uri.fromFile(new File(addonInfo.getLocalPath()));
//		int installFlags = 0;
//		PackageManager pm = mActivity.getPackageManager();
//		try {
//			PackageInfo pi = pm.getPackageInfo(addonInfo.getPackageName(),	PackageManager.GET_UNINSTALLED_PACKAGES);
//			if(pi != null) {
//				installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
//			}
//		} catch (Exception e) {
//
//		}
//		PackageInstallObserver observer = new PackageInstallObserver();
//		pm.installPackage(mPackageURI, observer, installFlags, addonInfo.getPackageName());
	}
	
	public void unInstallAPK(AddonInfo addonInfo) {
		Uri packageURI = Uri.parse("package:" + addonInfo.getPackageName());
		Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
		mActivity.startActivityForResult(intent, REQUEST_UNINSTALL);

//		PackageDeleteObserver observer = new PackageDeleteObserver(); 
//		mActivity.getPackageManager().deletePackage(addonInfo.getPackageName(), observer, 0);  
	}
	
//	private class PackageInstallObserver extends IPackageInstallObserver.Stub {
//
//		@Override
//		public void packageInstalled(String arg0, int arg1)
//				throws RemoteException {
//			
//		}
//	}
//	
//	private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
//		@Override
//		public void packageDeleted(String arg0, int arg1)
//				throws RemoteException {
//			
//		}
//	}

}
