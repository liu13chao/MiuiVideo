package com.miui.video.appstore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.miui.video.DKApp;
import com.miui.video.MainActivity;
import com.miui.video.R;
import com.miui.video.model.AppConfig;
import com.miui.video.type.AppStoreApkInfo;
import com.miui.videoplayer.download.ApkLoaderConsole;
import com.miui.videoplayer.download.ApkLoaderConsole.OnApkStatusListener;
import miui.app.Activity;
import miui.app.AlertDialog;

import java.io.File;

public class UpdateManager {
	public static final String TAG = UpdateManager.class.getName();
	private static final UpdateManager INSTANCE = new UpdateManager();
//	private AppStoreApkInfo updateInfo;
	private ApkLoaderConsole mApkConsole;
	private DownloadApkStatus mDownloadApkStatus = DownloadApkStatus.IDLE;
	
	private NotificationManager notificationManager;//状态栏通知管理类
	private final int notificationID = 1;//通知的id
	private static final int PENDINGINTENT_REQUESTCODE = 200;
	private final static int ONE_DAY = 24 * 3600000;
	private static final String APK_UPDATE = "updateapk";
	private static final String APK_UPDATETIME = "apkupdatetime";
	
	public UpdateManager(){
	}
	
	public static UpdateManager getInstance() {
		return INSTANCE;
	}

	public enum DownloadApkStatus{
		IDLE, BUSY
	}
	
//	public void setUpdateInfo(AppStoreApkInfo info) {
//		updateInfo = info;
//	}
//	
//	public AppStoreApkInfo getUpdateInfo(){
//		if(updateInfo == null){
//			DataStore.getInstance().loadSourceInfo();
//		}
//		return updateInfo;
//	}
	
//	private AppStoreApkInfo fake() {
//		AppStoreApkInfo info = new AppStoreApkInfo();
//		info.apkurl = "http://upgrade.m.tv.sohu.com//channels//hdv//4.3.3//SohuTV_4.3.3_1193_201409021210.apk";
//		info.apkversion = 2014100914;
//		info.canupgrade = 1;
//		String[] intro = new String[3];
//		intro[0] = "1、新增电视直播功能, 热门频道隆重上线";
//		intro[1] = "2、优化离线缓存功能";
//		intro[2] = "3、优化播放体验";
//		info.apkintro = intro;
//		return info;
//	}
	
	public boolean isShowUpdateDialog(Context context, AppStoreApkInfo info){
		if(info != null 
				&& !TextUtils.isEmpty(info.apkurl) 
				&& info.apkversion > getCurApkVersion(context)
				&& (info.canupgrade == 1)
				&& mDownloadApkStatus == DownloadApkStatus.IDLE){
			return true;
		}
		return false;
	}
	

	public long getCurApkVersion(Context context){
		return DKApp.getSingleton(AppConfig.class).getVersionCode();
	}
	
	public void updateMiuiVideoApk(final Context context, AppStoreApkInfo info) {
		if (info == null || info.apkurl == null) {
			return;
		}
		final String path = getApkLocalPath(context, info.apkurl);
		Log.d(TAG, "apk path: " + path);
		if (path == null) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		mApkConsole = new ApkLoaderConsole(context, info.apkurl, path);
		mApkConsole.setOnApkStatusListener(new OnApkStatusListener() {
			
			@Override
			public void onApkDownloadStart() {
				mApkConsole.startDownload();
				mDownloadApkStatus = DownloadApkStatus.BUSY;
				handleNotification(context, 0);
				Log.d(TAG, "percent: " + 0);		
			}
			
			@Override
			public void onApkDownloadProgress(int completed, int total) {
				int percent = completed *100 / total;
				handleNotification(context, percent);
				Log.d(TAG, "completed: " + completed);
				Log.d(TAG, "total: " + total);
				Log.d(TAG, "percent: " + percent);
			}
			
			@Override
			public void onApkDownloadError(int error) {
				mDownloadApkStatus = DownloadApkStatus.IDLE;
			}

			@Override
			public void onApkDownloadComplete() {
				mDownloadApkStatus = DownloadApkStatus.IDLE;
				handleNotification(context, 100);
			}
		});
		mApkConsole.start();
	}
	
	private String getApkLocalPath(Context context, String url) {
		return getLocalPath(context, url, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
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
	
	
	private long loadUpdateApkTime(){
		SharedPreferences preference = DKApp.getAppContext().getSharedPreferences(APK_UPDATE, Activity.MODE_PRIVATE);
		long savetime = preference.getLong(APK_UPDATETIME, 0);
		return savetime;
	}
	
	public void saveUpdateApkTime(){
		long curTime = System.currentTimeMillis();
		SharedPreferences preference = DKApp.getAppContext().getSharedPreferences(APK_UPDATE, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putLong(APK_UPDATETIME, curTime);
		editor.commit();
	}
	
	
	public boolean isUpdateApkInfoExpired(){
		long curTime = System.currentTimeMillis();
		long saveTime = loadUpdateApkTime();
		Log.d(TAG, "saveTime:" + saveTime);
		if(curTime - saveTime > 3 * ONE_DAY){
			return true;
		}else{
			return false;
		}
	}
	
	public static String buildMessage(Context context, AppStoreApkInfo info){
		StringBuffer str = new StringBuffer();
		if(info == null ){
			return null;
		}
		str.append(info.apkintro).append("\n");
		return str.toString();
	}
	
	public AlertDialog createUpdateApkDialog(final Context context, final AppStoreApkInfo info){
		AlertDialog mAlertDialog = new AlertDialog.Builder(context, miui.R.style.Theme_Light_Dialog_Alert).create();
		if(info == null){
			return mAlertDialog;
		}
		View contentView = View.inflate(context, R.layout.update_apk_alertdialog, null);
		TextView maintitle = (TextView)contentView.findViewById(R.id.maintitle);
		TextView subtitle = (TextView)contentView.findViewById(R.id.subtitle);
		TextView updatecontent = (TextView)contentView.findViewById(R.id.updatecontent);

		String main_title = context.getResources().getString(R.string.maintitle_prefix) + info.apkversion;
		String sub_title = context.getResources().getString(R.string.subtitle_prefix) + DKApp.getSingleton(AppConfig.class).getVersionCode();
		String message = buildMessage(context, info);
		
		maintitle.setText(main_title);
		subtitle.setText(sub_title);
		updatecontent.setText(message);		
		
		mAlertDialog.setView(contentView);
		String btn_neg = context.getResources().getString(R.string.cancel_button);
		String btn_pos = context.getResources().getString(R.string.update_button);

		mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, btn_neg, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		 } );
		mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btn_pos, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				updateMiuiVideoApk(context, info);
			}
        });
		mAlertDialog.setCancelable(false);
		return mAlertDialog;
	}
	
	public void handleNotification(Context context, int percent){
		Intent intent = new Intent(context, MainActivity.class); 
		PendingIntent pIntent = PendingIntent.getActivity(context, PENDINGINTENT_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.update_apk_notification);
		remoteView.setTextViewText(R.id.content_view_text2, percent + "%");
		Notification notification = new Notification.Builder(context)
		.setTicker(context.getResources().getText(R.string.update_apk_statusbar))
		.setContent(remoteView)
		.setContentIntent(pIntent)
		.setSmallIcon(R.drawable.ic_launcher)
		.getNotification();
		if(percent == 100){
			notificationManager.cancel(notificationID);
		}else{
			notificationManager.notify(notificationID, notification);
		}
	}
}
