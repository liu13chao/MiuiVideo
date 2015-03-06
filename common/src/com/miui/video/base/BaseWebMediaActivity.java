package com.miui.video.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.controller.PlaySession;
import com.miui.video.model.Html5PlayUrlRetriever;
import com.miui.video.model.Html5PlayUrlRetriever.PlayUrlListener;
import com.miui.video.statistic.MediaSourceTypeDef;
import com.miui.video.type.InformationData;
import com.miui.video.type.JSObject;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.PageProgressView;

/**
 *@author tangfuling
 *
 */

public class BaseWebMediaActivity extends BaseWebViewActivity {
	
	private static final String TAG = BaseWebMediaActivity.class.getName();

	public static final String KEY_URL = "url";
	public static final String KEY_INFORMATION_DATA = "information_data";
	public static final String KEY_MEDIA_INFO = "mediaInfo";
	public static final String KEY_SOURCE_PATH = "enterPath";
	public static final String KEY_CI = "ci";
	public static final String KEY_CLARITY = "calary";
	public static final String KEY_SOURCE = "source";
	public static final String KEY_MEDIA_SET_STYLE = "media_set_style";
	public static final String KEY_MEDIA_SET_NAME = "media_set_name";
	public static final String KEY_IS_MULTI_SET = "is_multiset";
    //UI
    private VideoWebChromeClient mWebChromeClient;
    private PageProgressView mProgressView;
    private ImageButton mBtnFullScreen;
    
    private static final int MAX_PROGRESS_VALUE = 100;
    
    //received data
    private InformationData mInformationData;
    private MediaInfo mMediaInfo;
//    private String mSourcePath;
    private int mCi;
    private int mClarity;
    private int mSource;
    private String mH5Url;
    private int mMediaSetStyle = MediaConstantsDef.MEDIA_TYPE_VARIETY;
    private String mMediaSetName = "";
    private boolean mIsMultiSet;
    //data from browser
  	private Html5PlayUrlRetriever mUrlRetriever = null;
  	private String mVideoUrl;
    
    //flags
    private final Handler mHandler = new Handler();
    private boolean mIsStopped = false;
	private boolean mIsPlayerStarted = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_media);
		init();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mIsStopped = false;
		mIsPlayerStarted = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mIsStopped = true;
		webView.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mUrlRetriever != null){
    		mUrlRetriever.release();
    	}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
			    try{
		             webView.destroy();
			    }catch(Exception e){
			    }
			}
		}, 500);
	}
	
	//init
	private void init() {
		initReceivedData();
		initUI();
		initWebView();
		mWebChromeClient = new VideoWebChromeClient();
		webView.setWebChromeClient(mWebChromeClient);
        webView.addJavascriptInterface(new WebPageObject(), "WebPage");
//		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
//		if(deviceInfo.hasConnectivity() && !deviceInfo.isWifiUsed()) {
//			showUseDataStreamDialog();
//		}else{
//			loadHtml5();
//		}
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		if(intent.getSerializableExtra(KEY_MEDIA_INFO) instanceof MediaInfo){
		      mMediaInfo = (MediaInfo) intent.getSerializableExtra(KEY_MEDIA_INFO);
		}
		if(intent.getSerializableExtra(KEY_INFORMATION_DATA) instanceof InformationData){
	        mInformationData = (InformationData) intent.getSerializableExtra(KEY_INFORMATION_DATA);		    
		}
//		mSourcePath = intent.getStringExtra(KEY_SOURCE_PATH);
		mCi = intent.getIntExtra(KEY_CI, -1);
		mClarity = intent.getIntExtra(KEY_CLARITY, -1);
		mSource = intent.getIntExtra(KEY_SOURCE, 0);
		mH5Url = intent.getStringExtra(KEY_URL);
		mMediaSetStyle = intent.getIntExtra(KEY_MEDIA_SET_STYLE, MediaConstantsDef.MEDIA_TYPE_VARIETY);
		mMediaSetName = intent.getStringExtra(KEY_MEDIA_SET_NAME);
		mIsMultiSet = intent.getBooleanExtra(KEY_IS_MULTI_SET, false);
	}
	
	private void initUI() {
		mProgressView = (PageProgressView) findViewById(R.id.web_progress);
		mBtnFullScreen = (ImageButton) findViewById(R.id.web_btn_fullscreen);
		mBtnFullScreen.setEnabled(false);
		mBtnFullScreen.setOnClickListener(mOnClickListener);
	}
	
	protected void loadHtml5() {
    	if(!Util.isEmpty(mH5Url)){
        	DKLog.i(TAG, "initial h5 url " + mH5Url);
            exeJs(mH5Url);
            startUrlRetriever();
        }
    }
	
//	private void showUseDataStreamDialog() {
//		String msgStr = getResources().getString(R.string.userdata_alert_content);
//		String negativeStr = getResources().getString(R.string.setting);
//		String positiveStr = getResources().getString(R.string.userdata_alert_ok);
//		
//		BaseAlertDialog dialog = new BaseAlertDialog(this);
//		dialog.setCancelable(true);
//		dialog.setTitle(R.string.userdata_alert_title);
//		dialog.setMessage(msgStr);
//		dialog.setButton(BaseAlertDialog.BUTTON_POSITIVE, positiveStr, new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				loadHtml5();
//			}
//		});
//		dialog.setButton(BaseAlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener(){ 	
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				startSystemSettingActivity();
//			}
//		});
//		dialog.show();
//	}
	
//	private void startSystemSettingActivity() {
//		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//		startActivity(intent);	
//	}
	
	private void exeJs(String js){
    	try {
    		webView.loadUrl(js);
		} catch (Exception e) {
		}
    }
	
	private void startUrlRetriever(){
    	if(mUrlRetriever != null){
    		mUrlRetriever.release();
    	}
    	mUrlRetriever = new Html5PlayUrlRetriever(webView, mSource);
    	mUrlRetriever.setPlayUrlListener(mPlayUrlListener);
    	mUrlRetriever.start();
    }
	
	private void startPlayer() {
		if(!mIsPlayerStarted && !mIsStopped) {
			mIsPlayerStarted = true;
			exeJs(JS_ENTER_FULLSCREEN);
			if(mMediaInfo != null) {
				new PlaySession(this).startPlayerOnlineByWeb(mMediaInfo, mCi, mSource, 
				        mClarity, mIsMultiSet, mMediaSetName, mMediaSetStyle, mVideoUrl, mH5Url);
			} else if(mInformationData != null) {
			    new PlaySession(this).startPlayerInfoByWeb(mInformationData, mVideoUrl, mH5Url);
			}
		}
	}
	
	private void hideProgressView() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mProgressView.setVisibility(View.INVISIBLE);
			}
		}, 500);
	}
	
	private void showProgressView() {
		mProgressView.setVisibility(View.VISIBLE);
	}
	
	public class WebPageObject implements JSObject {
		@JavascriptInterface
        public boolean isPaused() {
			return mIsStopped;
        }
    }
	
	private static final String JS_ENTER_FULLSCREEN = "javascript:(function() {" +
            "var pageUrl = window.location.href;" +
            "var videoTags = document.getElementsByTagName('video');" +
            "if(videoTags == null || videoTags == undefined || videoTags.length == 0){" +
            "    return;" +
            "}" +
            "videoTags[0].webkitEnterFullscreen();" +
			"videoTags[0].addEventListener('play',function() {" + 
			"	var videoTags = document.getElementsByTagName('video');" +
			"	if(videoTags == null || videoTags == undefined || videoTags.length == 0){" +
			"   	 return;" +
			"	}" +
			"	if(window.WebPage == undefined || window.WebPage.isPaused == undefined || " +
			"		window.WebPage.isPaused()){" +
			"		videoTags[0].pause();" +
			"	}" +
			"}, false); " +
			
			"videoTags[0].addEventListener('seeked',function() {" + 
			"	var videoTags = document.getElementsByTagName('video');" +
			"	if(videoTags == null || videoTags == undefined || videoTags.length == 0){" +
			"  		return;" +
			"	}" +
			"	if(window.WebPage == undefined || window.WebPage.isPaused == undefined || " +
			"		window.WebPage.isPaused()){" +
			"		videoTags[0].pause();" +
			"	}" +
			"}, false); " +
            "})()";
	
	//data callback
	private PlayUrlListener mPlayUrlListener = new PlayUrlListener() {
		@Override
		public void onUrlUpdate(String htmlUrl, final String url) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mVideoUrl = url;
					if(!mBtnFullScreen.isEnabled()) {
					    startPlayer();
						mBtnFullScreen.setEnabled(true);
					}
				}
			});
		}
	};
	
	//UI callback
	@Override
    protected void onPageFinish(WebView view, final String url) {
        super.onPageFinish(view, url);
        if(mSource == MediaSourceTypeDef.MEDIASOURCE_IQIYI_TYPE_CODE) {
        	if(mUrlRetriever != null){
        		mUrlRetriever.startQiyiLoop();
        	}
    	}
    }
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		    if(v == mBtnFullScreen){
	              startPlayer();
		    }
		}
	};
	
	//self def class
	public class VideoWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
        
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressView.setProgress(newProgress * 100);
            if(newProgress == MAX_PROGRESS_VALUE) {
            	hideProgressView();
            } else {
            	showProgressView();
            }
        }
    }
}
