package com.miui.video.request;

import org.json.JSONArray;
import org.json.JSONObject;

import com.miui.video.response.MediaUrlInfoListResponse;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class MediaUrlInfoListRequest extends TvServiceRequest {
	
	public MediaUrlInfoListRequest(int mediaID, int ci, int source) {
		mPath = "/tvservice/getmediaurl";
		addParam("mediaid", String.valueOf(mediaID));
		addParam("ci", String.valueOf(ci));
		addParam("source", String.valueOf(source));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse parse(byte[] buf, String encode) {
			MediaUrlInfoListResponse response = new MediaUrlInfoListResponse();
			try {
				String json = new String(buf, 0, buf.length, "utf-8");
				if(!Util.isEmpty(json)) {
					JSONObject jsonObjectSrc = new JSONObject(json);
					JSONArray jsonUrlNormal = jsonObjectSrc.getJSONArray("normal");
					JSONArray jsonUrlHigh = jsonObjectSrc.getJSONArray("high");
					JSONArray jsonUrlSuper = jsonObjectSrc.getJSONArray("super");
					response.setStatus(jsonObjectSrc.getInt("status"));
					
					MediaUrlInfoList mediaUrlInfoList = new MediaUrlInfoList();
					mediaUrlInfoList.videoName = jsonObjectSrc.getString("videoname");
					
					mediaUrlInfoList.urlNormal = new MediaUrlInfo[jsonUrlNormal.length()];
					mediaUrlInfoList.urlHigh = new MediaUrlInfo[jsonUrlHigh.length()];
					mediaUrlInfoList.urlSuper = new MediaUrlInfo[jsonUrlSuper.length()];
					
					for(int i = 0; i < jsonUrlNormal.length(); i++) {
						JSONObject jsonObject = new JSONObject(jsonUrlNormal.getJSONObject(i).toString());
						MediaUrlInfo mediaUrlInfo = new MediaUrlInfo();
						mediaUrlInfo.isHtml = jsonObject.getInt("ishtml");
						mediaUrlInfo.mediaSource = jsonObject.getInt("source");
						mediaUrlInfo.mediaUrl = jsonObject.getString("playurl");
						if(jsonObject.has("sdkdisable")){
							mediaUrlInfo.sdkdisable = jsonObject.getBoolean("sdkdisable");
						}
						if(jsonObject.has("sdkinfo2")){
							mediaUrlInfo.sdkinfo2 = jsonObject.getString("sdkinfo2");
						}
						mediaUrlInfoList.urlNormal[i] = mediaUrlInfo;
					}
					for(int i = 0; i < jsonUrlHigh.length(); i++) {
						JSONObject jsonObject = new JSONObject(jsonUrlHigh.getJSONObject(i).toString());
						MediaUrlInfo mediaUrlInfo = new MediaUrlInfo();
						mediaUrlInfo.isHtml = jsonObject.getInt("ishtml");
						mediaUrlInfo.mediaSource = jsonObject.getInt("source");
						mediaUrlInfo.mediaUrl = jsonObject.getString("playurl");
						if(jsonObject.has("sdkdisable")){
							mediaUrlInfo.sdkdisable = jsonObject.getBoolean("sdkdisable");
						}
						if(jsonObject.has("sdkinfo2")){
							mediaUrlInfo.sdkinfo2 = jsonObject.getString("sdkinfo2");
						}
						mediaUrlInfoList.urlHigh[i] = mediaUrlInfo;
					}
					for(int i = 0; i < jsonUrlSuper.length(); i++) {
						JSONObject jsonObject = new JSONObject(jsonUrlSuper.getJSONObject(i).toString());
						MediaUrlInfo mediaUrlInfo = new MediaUrlInfo();
						mediaUrlInfo.isHtml = jsonObject.getInt("ishtml");
						mediaUrlInfo.mediaSource = jsonObject.getInt("source");
						mediaUrlInfo.mediaUrl = jsonObject.getString("playurl");
						if(jsonObject.has("sdkdisable")){
							mediaUrlInfo.sdkdisable = jsonObject.getBoolean("sdkdisable");
						}
						if(jsonObject.has("sdkinfo2")){
							mediaUrlInfo.sdkinfo2 = jsonObject.getString("sdkinfo2");
						}
						mediaUrlInfoList.urlSuper[i] = mediaUrlInfo;
					}
					response.urlList = mediaUrlInfoList;
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setStatus(ServiceResponse.STATUS_UNKOWN_ERROR);
	        return response;
		}
		
		@Override
		public ServiceResponse createResponse() {
			return new MediaUrlInfoListResponse();
		}
	}
}
