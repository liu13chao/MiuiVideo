package com.miui.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.miui.video.base.BaseWebViewActivity;
import com.miui.video.util.Util;

public class OpenHtml5ForAdsActivity extends BaseWebViewActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.web_push);
		Intent intent = getIntent();
		initWebView();
		String url = intent.getStringExtra("url");
		Uri uri = Uri.parse(url);
		String scheme = uri.getScheme();
		if(!Util.isEmpty(scheme) && scheme.equalsIgnoreCase("http")){
			webView.loadUrl(url);
		}
	}
}
