/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKHttpRequest.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.miui.video.request;

import java.util.Random;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.ApiConfig;
import com.miui.video.model.AppConfig;
import com.miui.video.model.DeviceInfo;
import com.miui.video.model.LoginManager;
import com.miui.video.model.UserManager.UserAccount;
import com.miui.video.response.TvServiceResponse;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.security.Security;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


/**
 * @author tianli
 *
 */
public abstract class TvServiceRequest extends ServiceRequest {

	protected String mPath = "";

	private boolean mShowResultDesc = true;

	@Override
	protected void onRequestInBackground() {
		if(isSecurity()){
			mUrl = "https://" + ApiConfig.SERVER_URL + mPath;
		}else{
			mUrl = "http://" + ApiConfig.SERVER_URL + mPath;
		}
		doRequest(System.currentTimeMillis()/1000);
		if(mResponse.getStatus() == ServiceResponse.STATUS_SYNC_TS){
			// sync timeStamp
			doRequest(mResponse.getTs());
		}
	}

	private void doRequest(long ts){
		appendCommonParams(ts);
		super.onRequestInBackground();
	}

	protected String token(){
		return ApiConfig.API_TOKEN;
	}
	
	protected String key(){
		return ApiConfig.API_KEY;
	}
	
	private String accountName() {
		LoginManager loginManager = DKApp.getSingleton(LoginManager.class);
		UserAccount account = loginManager.getUserAccount();
		if(account != null && account.accountName != null) {
			return account.accountName;
		}
		return "";
	}
	
	private String nonce() {
		StringBuilder strBuilder = new StringBuilder();
		Random random = new Random(System.currentTimeMillis());
		strBuilder.append(random.nextInt());
		return strBuilder.toString();
	}
	
	private String getDefaultId(){
		return Util.getMD5("userId");
	}

	private void appendCommonParams(long ts) {
		String nonce = nonce();
		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		AppConfig conf = DKApp.getSingleton(AppConfig.class);
		String userId = accountName();
		if(needUserId() && !TextUtils.isEmpty(userId)){
			addParam(ApiConfig.PARAM_DEVICEID, getDefaultId());
			addParam(ApiConfig.PARAM_USERID, userId);
		}else{
			addParam(ApiConfig.PARAM_DEVICEID, deviceInfo.getUID());
		}
		addParam(ApiConfig.PARAM_APIVER, conf.getApiVer());
		addParam(ApiConfig.PARAM_VER, conf.getVersion());
		addParam(ApiConfig.PARAM_MIUI_VER, conf.getMiuiVer());
		addParam(ApiConfig.PARAM_DEVICE_TYPE, deviceInfo.getDeviceType() +"");
		addParam(ApiConfig.PARAM_PLATFORM, deviceInfo.getPlatform() + "");
		addParam(ApiConfig.PARAM_TIMESTAMP, ts + "");
		addParam(ApiConfig.PARAM_NONCE, nonce);
		String sig = mPath + "?" + URLEncodedUtils.format(getParams(), HTTP.UTF_8);
//		Log.d("11111111111", "token = " + token());
		sig = sig + "&" + ApiConfig.PARAM_TOKEN + "=" + token();
//	      Log.d("11111111111", "key = " + key());
		String opaque = null;
		try {
			byte[] key = key().getBytes();
			opaque = Security.signature(sig.getBytes(), key);
		}catch (Exception e) {
		}
		addParam(ApiConfig.PARAM_OPAQUE, opaque);
	}


	@Override
	protected void onPostRequest() {
		 if(mResponse instanceof TvServiceResponse) {
			 TvServiceResponse response = (TvServiceResponse) mResponse;
			 response.completeData();
		 }
		super.onPostRequest();
		if (mShowResultDesc && !TextUtils.isEmpty(mResponse.getDesc())) {
			AlertMessage.show(mResponse.getDesc());
		}
	}

	@Override
	public void onCancelReuqest() {
		super.onCancelReuqest();
		mShowResultDesc = false;
	}

	public void setShowResultDesc(boolean showResultDesc) {
		mShowResultDesc = showResultDesc;
	}
	
	public boolean needUserId(){
		return false;
	}
}
