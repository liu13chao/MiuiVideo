package com.miui.video.mipush;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.AppSingleton;
import com.xiaomi.miui.pushads.sdk.BubbleAdsCell;
import com.xiaomi.miui.pushads.sdk.MiuiAdsListener;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;
import com.xiaomi.miui.pushads.sdk.NotifyAdsManagerNew;

public class MiPushAdsProcess extends AppSingleton{

	private Context context;
    private final String OPEN_APP = "open";
    private final String DOWNLOAD_APP = "app";
    private final String OPEN_URL = "web";
	
	public MiPushAdsProcess(Context context) {
		super(context);
		this.context = context;
	}
    
	public void registerMiPushEnv(){
		NotifyAdsManagerNew.open(context, mMiuiAdsListener, DKApp.getAppPackageName(), DKApp.APP_ID, DKApp.APP_KEY);
	}
	
	public void processAds(String adsJsonString) {
		if(NotifyAdsManagerNew.getInstance() != null)
			NotifyAdsManagerNew.getInstance().pushOneAdsRequest(adsJsonString, DKApp.getAppPackageName());
	}

	private MiuiAdsListener mMiuiAdsListener = new MiuiAdsListener() {
		
		@Override
		public boolean onNotifyReceived(NotifyAdsCell cell) {
			Uri uri = Uri.parse(cell.actionUrl);
			String force = uri.getQueryParameter("forcepush");
			if(DKApp.isReceiveAds() || force.equalsIgnoreCase("true")){
				return false;
			} else{
				return true;
			}
		}
		
		@Override
		public void onChannelInitialized(long resultCode, String regID) {
			
		}
		
		@Override
		public void onBubbleReceived(BubbleAdsCell cell) {
			
		}
		
		@Override
		public int getSmallIconID() {
			return R.drawable.ic_launcher;
		}
		
		@Override
		public PendingIntent getClickPendingIntent(NotifyAdsCell cell) {
			Uri uri = Uri.parse(cell.actionUrl);
			if(cell.type.equalsIgnoreCase(OPEN_URL)){
				Intent clickIntent = new Intent(Intent.ACTION_VIEW);
				String url = cell.actionUrl;
				if(url.contains("?forcepush=true")){
					clickIntent.setData(Uri.parse(url.replace("?forcepush=true", "")));
				}else{
					clickIntent.setData(Uri.parse(url));
				}
		        
		        PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0,
		                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		       return clickPendingIntent;
			} else if(cell.type.equalsIgnoreCase(OPEN_APP)){
				String actiontype = uri.getQueryParameter("actiontype");
				return PendingIntentFactory.createPendingIntent(context, cell, actiontype);
			} else if(cell.type.equalsIgnoreCase(DOWNLOAD_APP)){
		       return null;
			}
			return null;
		}
		
		@Override
		public PendingIntent getActionPendingIntent(NotifyAdsCell cell) {
			return null;
		}
	};
}
