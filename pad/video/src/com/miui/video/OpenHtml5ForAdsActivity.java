package com.miui.video;

import com.miui.video.base.BaseWebViewActivity;

import android.content.Intent;
import android.os.Bundle;

public class OpenHtml5ForAdsActivity extends BaseWebViewActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.web_push);
		Intent intent = getIntent();
		initWebView();
		webView.loadUrl(intent.getStringExtra("url"));
	}
}
