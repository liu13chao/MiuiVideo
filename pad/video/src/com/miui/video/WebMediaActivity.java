package com.miui.video;

import miui.app.AlertDialog;

import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseWebViewActivity;
import com.miui.video.common.PlaySession;
import com.miui.video.model.DeviceInfo;
import com.miui.video.model.Html5PlayUrlRetriever;
import com.miui.video.model.Html5PlayUrlRetriever.PlayUrlListener;
import com.miui.video.statistic.MediaSetTypeDef;
import com.miui.video.statistic.MediaSourceTypeDef;
import com.miui.video.statistic.OpenMediaStatisticInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.PageProgressView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;

/**
 *@author tangfuling
 *
 */

public class WebMediaActivity extends BaseWebViewActivity {
	
	private static final String TAG = WebMediaActivity.class.getName();
	
	public static final String KEY_URL = "url";
    public static final String KEY_MEDIA_INFO = "mediaInfo";
    public static final String KEY_SOURCE_PATH = "enterPath";
    public static final String KEY_CI = "ci";
    public static final String KEY_CLARITY = "calary";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_MEDIA_SET_STYLE = "media_set_style";
	
    //UI
    private VideoWebChromeClient mWebChromeClient;
    private PageProgressView mProgressView;
    private ImageButton mBtnFullScreen;
    
    private static final int MAX_PROGRESS_VALUE = 100;
    
    //received data
    private String mSourcePath;
    private MediaInfo mMediaInfo;
    private int mCi;
    private int mClarity;
    private int mSource;
    private String mH5Url;
    private int mMediaSetStyle = MediaConstantsDef.MEDIA_TYPE_VARIETY;
    
    //data from browser
  	private Html5PlayUrlRetriever mUrlRetriever = null;
  	private String mVideoUrl;
    
    //flags
    private final Handler handler = new Handler();
    private boolean mIsStopped = false;
    private boolean mIsActivityNewCreate = false;
	private boolean mIsPlayerEpisodeChanged = false;
	private boolean mIsPlayerStarted = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_media);
		if(savedInstanceState == null) {
			mIsActivityNewCreate = true;
    	}
		onCreateInit();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
//		if(mIsStopped){
//			ViewGroup parent = (ViewGroup)findViewById(R.id.webmedia_webwrap);
//			boolean hasWebView = false;
//			for(int i = 0; i < parent.getChildCount(); i++){
//				if(parent.getChildAt(i) == webView){
//					hasWebView = true;
//					break;
//				}
//			}
//			if(!hasWebView){
//				parent.addView(webView);
//			}
//			webView.onResume();
//		}
		mIsStopped = false;
		mIsPlayerStarted = false;
		onStartInit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mIsStopped = true;
		webView.onPause();
        mIsActivityNewCreate = false;
//		ViewGroup parent = (ViewGroup)findViewById(R.id.webmedia_webwrap);
//		parent.removeView(webView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mUrlRetriever != null){
    		mUrlRetriever.release();
    	}
		
		handler.postDelayed(new Runnable() {
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
	private void onCreateInit() {
		initReceivedData();
		initUI();
		initWebView();
		mWebChromeClient = new VideoWebChromeClient();
		webView.setWebChromeClient(mWebChromeClient);
		
		uploadWebMediaStatistic();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		mMediaInfo = (MediaInfo) intent.getSerializableExtra(KEY_MEDIA_INFO);
		mSourcePath = intent.getStringExtra(KEY_SOURCE_PATH);
		mCi = intent.getIntExtra(KEY_CI, -1);
		mClarity = intent.getIntExtra(KEY_CLARITY, -1);
		mSource = intent.getIntExtra(KEY_SOURCE, 0);
		mH5Url = intent.getStringExtra(KEY_URL);
		mMediaSetStyle = intent.getIntExtra(KEY_MEDIA_SET_STYLE, MediaConstantsDef.MEDIA_TYPE_VARIETY);
	}
	
	private void initUI() {
		mProgressView = (PageProgressView) findViewById(R.id.web_progress);
		mBtnFullScreen = (ImageButton) findViewById(R.id.web_btn_fullscreen);
		mBtnFullScreen.setEnabled(false);
		mBtnFullScreen.setOnClickListener(mOnClickListener);
	}
	
	private void onStartInit() {
		if(mIsPlayerEpisodeChanged || mIsActivityNewCreate) {
			DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
			if(deviceInfo.hasConnectivity()) {
				if(deviceInfo.isWifiUsed()) {
					loadHtml5();
				} else {
					if (DKApp.isUseCellular()) {
						loadHtml5();
					} else {
						showUseDataStreamDialog();
					}
				}
			} else {
				loadHtml5();
			}
		}
	}
	
	//packaged method
	private void loadHtml5() {
    	if(!Util.isEmpty(mH5Url)){
        	DKLog.i(TAG, "initial h5 url " + mH5Url);
            exeJs(mH5Url);
        }
    	startUrlRetriever();
    }
	
	private void showUseDataStreamDialog() {
		new AlertDialog.Builder(this)
		.setCancelable(true).setTitle(R.string.userdata_alert_title)
		.setPositiveButton(R.string.userdata_alert_ok, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loadHtml5();
			}
		})
		.setNegativeButton(R.string.setting, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startSystemSettingActivity();
			}
		})
		.setMessage(R.string.userdata_alert_content)
		.create().show();
	}
	
	private void startSystemSettingActivity() {
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		startActivity(intent);	
	}
	
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
			PlaySession playSession = DKApp.getSingleton(PlaySession.class);
			playSession.startPlayerOnline(mMediaInfo, mCi, mSource, mClarity, mVideoUrl, mH5Url, mMediaSetStyle);
//			this.finish();
		}
	}
	
	private void hideProgressView() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mProgressView.setVisibility(View.INVISIBLE);
			}
		}, 500);
	}
	
	private void showProgressView() {
		mProgressView.setVisibility(View.VISIBLE);
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
						if(mUrlRetriever.isAutoPlay()) {
							startPlayer();
						}
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
			int id = v.getId();
			switch (id) {
			case R.id.web_btn_fullscreen:
				startPlayer();
				break;

			default:
				break;
			}
		}
	};
	
	//self def class
	public class VideoWebChromeClient extends WebChromeClient {
		
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation,
                CustomViewCallback callback) {
            super.onShowCustomView(view, requestedOrientation, callback);
        }

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
	
	//statistic
	private void uploadWebMediaStatistic() {
		DKApi.getMediaInfoByH5Url(mSource, mH5Url, 
				prepareWebMediaStatistic(), null);
	}
	
	private String prepareWebMediaStatistic() {
		OpenMediaStatisticInfo  openMediaStatisticInfo = new OpenMediaStatisticInfo();
		if(mMediaInfo != null) {
			openMediaStatisticInfo.mediaId = mMediaInfo.mediaid;
		}
		openMediaStatisticInfo.ci = mCi;
		openMediaStatisticInfo.sourcePath = mSourcePath;
		openMediaStatisticInfo.mediaSourceType = mSource;
		openMediaStatisticInfo.mediaSetType = MediaSetTypeDef.getMediaSetType(this, mMediaInfo);
		return openMediaStatisticInfo.formatToJson();
	}
}
