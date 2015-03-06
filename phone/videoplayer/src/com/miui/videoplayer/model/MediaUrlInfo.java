/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaUrlInfo.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-7
 */

package com.miui.videoplayer.model;

import org.json.JSONArray;
import org.json.JSONObject;

import com.miui.video.api.def.MediaConstantsDef;
import com.miui.videoplayer.common.AndroidUtils;


/**
 * @author tianli
 *
 */
public class MediaUrlInfo {

	public MediaUrlInfo(JSONObject json, String localPath){
		try{
			JSONArray array;
			array = json.getJSONArray("urlNormal");
			if(array != null && array.length() > 0){
				urlNormal = new UrlInfo[array.length()];
				for(int i = 0; i < array.length(); i++){
					urlNormal[i] = new UrlInfo(array.getJSONObject(i));
					urlNormal[i].resolution = MediaConstantsDef.CLARITY_NORMAL;
					urlNormal[i].offlinePath = localPath;
				}
			}
			array = json.getJSONArray("urlHigh");
			if(array != null && array.length() > 0){
				urlHigh = new UrlInfo[array.length()];
				for(int i = 0; i < array.length(); i++){
					urlHigh[i] = new UrlInfo(array.getJSONObject(i));
					urlHigh[i].resolution = MediaConstantsDef.CLARITY_HIGH;
					urlHigh[i].offlinePath = localPath;
				}
			}
			array = json.getJSONArray("urlSuper");
			if(array != null && array.length() > 0){
				urlSuper = new UrlInfo[array.length()];
				for(int i = 0; i < array.length(); i++){
					urlSuper[i] = new UrlInfo(array.getJSONObject(i));
					urlSuper[i].resolution = MediaConstantsDef.CLARITY_SUPPER;
					urlSuper[i].offlinePath = localPath;
				}
			}
			videoName = json.getString("videoName");
		}catch (Exception e) {
		}
	}
	
	
	public String videoName;
	public UrlInfo[] urlNormal;
	public UrlInfo[] urlHigh;
	public UrlInfo[] urlSuper;
	
	public UrlInfo[] getAll() {
		UrlInfo[] temp = AndroidUtils.concat(urlNormal, urlHigh);
		return AndroidUtils.concat(temp, urlSuper);
	}
	public boolean canSelectSource() {
		final UrlInfo[] all = getAll();
		return all != null && all.length > 1;
	}
	public static class UrlInfo{
		
		public UrlInfo(JSONObject json){
			try{
				mediaUrl = json.getString("mediaUrl");
				mediaSource = json.getInt("mediaSource");
				startOffset = json.getInt("startOffset");
				endOffset = json.getInt("endOffset");
				isHtml = json.getInt("isHtml");
				sdkinfo = json.getString("sdkinfo");
				sdkdisable = json.getBoolean("sdkdisable");
			}catch (Exception e) {
			}
		}
		public String sdkinfo;
		public String playUrl;
		public String mediaUrl;
		public int mediaSource;
		public int startOffset;
		public int endOffset;
		public int isHtml;
		public int resolution;
		public String offlinePath;	
		public boolean sdkdisable;
	}
	
}
