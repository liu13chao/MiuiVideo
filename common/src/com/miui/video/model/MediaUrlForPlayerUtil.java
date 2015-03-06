package com.miui.video.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.miui.video.DKApp;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.model.Html5PlayUrlRetriever.PlayUrlListener;
import com.miui.video.statistic.MediaSourceTypeDef;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

public class MediaUrlForPlayerUtil implements PlayUrlListener {
	private static final String TAG = MediaUrlForPlayerUtil.class.getName();
	
	private Context mContext;
	private WebView mWebView;
	
	Handler mHandler = null;
//	private static final int  GET_VIDEO_URL_INTERVAL_TENCENT = 200;         //ms
//    private static final int GET_VIDEO_URL_INTERVAL = 3000; 
	
	private int source = -1;
	private int clarity = -1;
	
//	private MediaUrlSqliteOpenHelper mMediaUrlOpenHelper;
	private int mediaId;
	private int mediaCi;
//	private String mediaUrlForPlayer;
	
//	private boolean mVideoReady = false;
//	private boolean mVideoUrlReady = false;
//	private boolean mReleased = false;
	
//	private static MediaUrlForPlayerUtil mMediaUrlForPalForPlayerUtil;
	private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
	
	private MediaUrlInfoList mMediaUrlInfoList;
	private MediaUrlInfo mActMediaUrlInfo;
	
//	private Object lock;
	
//	private PlayUrlMonitor mPlayUrlMonitor = new PlayUrlMonitor();
	private PlayUrlObserver mObserver;
	
    private Html5PlayUrlRetriever mUrlRetriever = null;
	
	public MediaUrlForPlayerUtil(Context context) {
		this.mContext = context.getApplicationContext();
		mHandler = new Handler(mContext.getMainLooper());
		mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
		mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
	}
	
	public void setPrefrenceSource(int source) {
		this.source = source;
	}
	
//	public synchronized static MediaUrlForPlayerUtil getInstance(Context context) {
//		if(mMediaUrlForPalForPlayerUtil == null) {
//			mMediaUrlForPalForPlayerUtil = new MediaUrlForPlayerUtil(context.getApplicationContext());
//		}
//		return mMediaUrlForPalForPlayerUtil;
//	}
	
    private synchronized void startUrlRetriever(){
        WebView webView = mWebView;
        if(webView != null){
    	    if(mUrlRetriever != null){
    	 	    mUrlRetriever.release();
    	    }
    	    mUrlRetriever = new Html5PlayUrlRetriever(webView, source);
    	    mUrlRetriever.setPlayUrlListener(this);
    	    mUrlRetriever.setSkipAd(true);
    	    mUrlRetriever.start();
        }
    }
	
//	public void getMediaUrlForPlayer(int mediaId, int ci, int source, String staticInfo) {
//		DKLog.d(TAG, "public get media url for player");
//		this.statisticInfo = staticInfo;
//		this.mediaId = mediaId;
//		this.mediaCi = ci;
//		this.source = source;
//		mMediaUrlInfoListUtil.setMediaUrlInfoListCompleteObserver(this);
//		mMediaUrlInfoListUtil.getMediaUrlInfoList(mediaId, ci, source);
//	}
	
	public synchronized void getMediaUrlForPlayer(int mediaId, int ci, int source, String staticInfo) {
		getMediaUrlForPlayer(mediaId, ci, source, -1, staticInfo);
	}
	
	public synchronized void getMediaUrlForPlayer(int mediaId, int ci, int source, int clarity, String staticInfo) {
		DKLog.d(TAG, "public get media url for player");
//		mReleased = false;
//		this.lock = lock;
		tearDown();
		this.mediaId = mediaId;
		this.mediaCi = ci;
		this.source = source;
		this.clarity = clarity;
		mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
		mMediaUrlInfoListSupply.getMediaUrlInfoList(mediaId, ci, source);
		
		mHandler.removeCallbacks(mCancelGetUrlForPlayerRunnalbe);
		mHandler.postDelayed(mCancelGetUrlForPlayerRunnalbe, 35000);
	}
	
	public synchronized void getMediaUrlForPlayer(String mediaUrl) {
		getPlayerUrl(mediaUrl);
		mHandler.removeCallbacks(mCancelGetUrlForPlayerRunnalbe);
		mHandler.postDelayed(mCancelGetUrlForPlayerRunnalbe, 35000);
	}
	
	public synchronized void tearDown() {
		DKLog.d(TAG, "tear down");
//		mReleased = true;
		mHandler.removeCallbacks(mCancelGetUrlForPlayerRunnalbe);
		if(mMediaUrlInfoListSupply != null) {
			mMediaUrlInfoListSupply.removeListener(mMediaUrlInfoListListener);
		}
		if(mUrlRetriever != null){
			mUrlRetriever.release();
		}
		if(mObserver != null) {
			mObserver.onReleaseLock();
		}
		if(mWebView != null) {
			final WebView webView = mWebView;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					webView.destroy();
				}
			}, 500);
			mWebView = null;		
		}
	}
	
	private Runnable mCancelGetUrlForPlayerRunnalbe = new Runnable() {
		
		@Override
		public void run() {
			onGetPlayerUrlCancel();
		}
	};
	
	private synchronized void onGetPlayerUrlFinish(String playUrl, String html5Url) {
		if(mObserver != null){
			//TODO: use right mediaId and ci.
			mObserver.onUrlUpdate(mediaId, mediaCi, playUrl, html5Url);
		}
		tearDown();
	}
	
	private synchronized void onGetPlayerUrlError() {
		if(mObserver != null){
			mObserver.onError();
		}
		tearDown();
	}
	
	private synchronized void onGetPlayerUrlCancel() {
		if(mObserver != null){
			mObserver.onError();
		}
		tearDown();
	}
	
	public synchronized void getPlayerUrl(String url) {
		DKLog.d(TAG, "get player url ");
		initWebView();
		mWebView.loadUrl(url);
		startUrlRetriever();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private synchronized void initWebView() {
		if(mWebView == null) {
			mWebView = new WebView(mContext);
		}
		mWebView.getSettings().setJavaScriptEnabled(true);
		String newUserAgentString = mWebView.getSettings().getUserAgentString() + " " + "MiuiVideo/1.0";
		mWebView.getSettings().setUserAgentString(newUserAgentString);
		mWebView.clearCache(false);
		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		if (deviceInfo.isWapApnUsed()) {
			mWebView.setHttpAuthUsernamePassword(deviceInfo.getProxyHost(),
				deviceInfo.getProxyPort() + "", "", "");
		} else {
			mWebView.setHttpAuthUsernamePassword("", "", "", "");
		}

		mWebView.setWebViewClient(new MyWebViewClient());
	}
	
	public class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			startUrlRetriever();
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			DKLog.d(TAG, "on page finish");
			if(source == MediaSourceTypeDef.MEDIASOURCE_IQIYI_TYPE_CODE) {
				mUrlRetriever.startQiyiLoop();
	    	} 
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			return super.shouldOverrideKeyEvent(view, event);
		}
	}
	
	//data callback
	private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {
		
		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			if(!isError) {
				mMediaUrlInfoList = mediaUrlInfoList;
				DKLog.d(TAG, "get url list done");
				mActMediaUrlInfo = MediaUrlInfoListSupply.filterMediaUrlInfoList(mMediaUrlInfoList, source, clarity);
				if(mActMediaUrlInfo != null) {
					DKLog.d(TAG, "filter url list act url: " +mActMediaUrlInfo.mediaUrl);
					source = mActMediaUrlInfo.mediaSource;
					DKLog.d(TAG, "source is: " +source);
					if(mActMediaUrlInfo.isHtml == 1){
						getPlayerUrl(mActMediaUrlInfo.mediaUrl);
						return;
					}else{
//		        		mediaUrlForPlayer = mActMediaUrlInfo.mediaUrl;
		        		onGetPlayerUrlFinish(mActMediaUrlInfo.mediaUrl, "");
		        		return;
					}
				}
			} else {
				onGetPlayerUrlError();
			}
		}
	};

	@Override
	public void onUrlUpdate(final String htmlUrl, final String playUrl) {
		DKLog.d(TAG, "url for player getting res: " + playUrl);
    	if (!Util.isEmpty(playUrl)) {
			DKLog.d(TAG, "url for player done: " + playUrl);
			onGetPlayerUrlFinish(playUrl, htmlUrl);
		}
	}
	
	public void setObserver(PlayUrlObserver observer) {
		this.mObserver = observer;
	}

	public interface PlayUrlObserver {
		public void onUrlUpdate(int mediaId, int ci, String playUrl, String html5Url);
		public void onError();
		public void onReleaseLock();
	}
}
