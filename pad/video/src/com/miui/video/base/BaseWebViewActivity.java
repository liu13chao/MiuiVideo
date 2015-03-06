package com.miui.video.base;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.DeviceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *@author tangfuling
 *
 */

public class BaseWebViewActivity extends Activity {

	protected WebView webView;
	protected MyWebViewClient client = new MyWebViewClient();
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void initWebView() {
		webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptEnabled(true);
		String newUserAgentString = webView.getSettings().getUserAgentString() + " " + "MiuiVideo/1.0";
		webView.getSettings().setUserAgentString(newUserAgentString);
		webView.clearCache(false);
		 DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		 if (deviceInfo.isWapApnUsed()) {
			 webView.setHttpAuthUsernamePassword(deviceInfo.getProxyHost(),
				 deviceInfo.getProxyPort() + "", "", "");
		 } else {
			 webView.setHttpAuthUsernamePassword("", "", "", "");
		 }
		webView.setWebViewClient(client);
	}
	
	protected boolean onUrlLoading(WebView view, String url) {
		return false;
	}

	protected void onPageFinish(WebView view, String url) {

	}
	
	protected void onUpdateUrlLoading(WebView view, String newUrl) {
		
	}
	
	public class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!onUrlLoading(view, url)) {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			onPageFinish(view, url); 
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
}
