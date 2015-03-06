package com.miui.video;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.miui.video.base.BaseWebMediaActivity;
import com.miui.video.model.AppSettings;
import com.miui.video.util.DKLog;
import com.miui.videoplayer.common.AndroidUtils;
import miui.app.AlertDialog;

/**
 *@author tangfuling
 *
 */

public class WebMediaActivity extends BaseWebMediaActivity {
	
	public static final String TAG = WebMediaActivity.class.getName();

//	public static final String KEY_URL = "url";
//	public static final String KEY_INFORMATION_DATA = "information_data";
//	public static final String KEY_MEDIA_INFO = "mediaInfo";
//	public static final String KEY_SOURCE_PATH = "enterPath";
//	public static final String KEY_CI = "ci";
//	public static final String KEY_CLARITY = "calary";
//	public static final String KEY_SOURCE = "source";
//	public static final String KEY_MEDIA_SET_STYLE = "media_set_style";

//	//received data
//    private InformationData mInformationData;
//    private MediaInfo mMediaInfo;
//    private String mSourcePath;
//    private int mCi;
//    private int mClarity;
//    private int mSource;
//    private String mH5Url;
//    private int mMediaSetStyle = MediaConstantsDef.MEDIA_TYPE_VARIETY;
    
    //data from browser
//  	private Html5PlayUrlRetriever mUrlRetriever = null;
//  	private String mVideoUrl;
    
    //flags
//    private final Handler handler = new Handler();
//    private boolean mIsStopped = false;
//	private boolean mIsPlayerStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    setTheme(miui.R.style.Theme_Light_NoTitle);
		super.onCreate(savedInstanceState);
		if(AndroidUtils.isNetworkConncected(this) && !AndroidUtils.isFreeNetworkConnected(this) 
				&& DKApp.getSingleton(AppSettings.class).isOpenCellularPlayHint(this)){
			showUseDataStreamDialog();
		}else{
			loadHtml5();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void showUseDataStreamDialog() {
		View contentView = View.inflate(WebMediaActivity.this, R.layout.play_datastream_hint_view, null);
		String negativeStr = getResources().getString(R.string.datastream_alert_negative_button);
		String positiveStr = getResources().getString(R.string.datastream_alert_positive_button);
		AlertDialog dialog = new AlertDialog.Builder(this, miui.R.style.Theme_Light_Dialog_Alert).create();
		dialog.setTitle(R.string.datastream_alert_title);
        dialog.setView(contentView);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startSystemSettingActivity();
			}
		 } );
        
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveStr, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loadHtml5();
			}
        });
		dialog.setCancelable(false);
		
		try {
			dialog.show();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}
	
	private void startSystemSettingActivity() {
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		startActivity(intent);	
	}
}
