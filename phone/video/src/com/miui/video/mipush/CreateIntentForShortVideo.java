package com.miui.video.mipush;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.WebMediaActivity;
import com.miui.video.controller.MediaConfig;
import com.miui.video.db.DBUtil;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.Util;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

public class CreateIntentForShortVideo implements IntentCreator {

	private NotifyAdsCell cell;
	public static final String ACTION_PLAY = "duokan.intent.action.VIDEO_PLAY";
	
	public CreateIntentForShortVideo(Context context, NotifyAdsCell cell) {
		this.cell = cell;
	}

	@Override
	public Intent creatIntent() {
		Uri uri = Uri.parse(cell.actionUrl);
		String mediaid = uri.getQueryParameter("mediaid");
		String url = uri.getQueryParameter("url");
		String sdkinfo = uri.getQueryParameter("sdkinfo");
		String videoname = uri.getQueryParameter("videoname");
		String posterurl = uri.getQueryParameter("posterurl");
	    int source = Integer.valueOf(uri.getQueryParameter("source"));
		int playtype = Integer.valueOf(uri.getQueryParameter("playtype"));
		MediaInfo mediainfo = buildMediaInfo(mediaid, videoname, posterurl);
		Intent clickIntent = null;
		if(!TextUtils.isEmpty(sdkinfo)){
			clickIntent = createSdkIntent(mediainfo, url, sdkinfo, source);
		}else if(playtype == 0){
			clickIntent = createWebIntent(mediainfo, url, source);
		}else if(playtype == 2){
			clickIntent = createDirectPlayIntent(mediainfo, url, source);
		}
		return clickIntent;
	}

	public Intent createSdkIntent(MediaInfo mediaInfo, String html5Url,  String sdkInfo, int source){
		Uri uri = Uri.parse(html5Url);
		//TODO:
		Intent intent = new Intent(ACTION_PLAY, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(DBUtil.KEY_MEDIA_TITLE, mediaInfo.medianame);
		intent.putExtra(DBUtil.KEY_MEDIA_SDKINFO, sdkInfo);
		intent.putExtra(DBUtil.KEY_MEDIA_SDKDISABLE, false);
		intent.putExtra(DBUtil.MEDIA_ID, mediaInfo.mediaid);
		intent.putExtra(DBUtil.MEDIA_POSTER_URL, mediaInfo.posterurl);
		intent.putExtra(DBUtil.MEDIA_SOURCE,  source);
		intent.putExtra(DBUtil.MEDIA_HTML5_URL, html5Url);
		intent.putExtra(DBUtil.VIDEO_TYPE, mediaInfo.videoType);
		intent.putExtra(DBUtil.PLAY_TYPE, MediaConfig.PLAY_TYPE_SDK);
		return intent;
	}
	
	public Intent createWebIntent(MediaInfo mediaInfo, String html5Url, int source){
		Intent intent = new Intent(DKApp.getAppContext(), WebMediaActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(WebMediaActivity.KEY_MEDIA_INFO, mediaInfo);
		intent.putExtra(WebMediaActivity.KEY_SOURCE,  source);
		intent.putExtra(WebMediaActivity.KEY_URL, formatUrl(html5Url));
		return intent;
	}
	public Intent createDirectPlayIntent(MediaInfo mediaInfo, String playUrl, int source){
		Uri uri = Uri.parse(playUrl);
		Intent intent = new Intent(ACTION_PLAY, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(DBUtil.KEY_MEDIA_TITLE, mediaInfo.medianame);
		intent.putExtra(DBUtil.MEDIA_ID, mediaInfo.mediaid);
		intent.putExtra(DBUtil.MEDIA_POSTER_URL, mediaInfo.posterurl);
		intent.putExtra(DBUtil.MEDIA_SOURCE, source);
		intent.putExtra(DBUtil.MEDIA_HTML5_URL, playUrl);
		intent.putExtra(DBUtil.VIDEO_TYPE, mediaInfo.videoType);
		intent.putExtra(DBUtil.PLAY_TYPE, MediaConfig.PLAY_TYPE_DIRECT);
		return intent;
	}
	
	public MediaInfo buildMediaInfo(String mediaid, String videoName,  String posterurl){
		MediaInfo mediaInfo = new MediaInfo();
		mediaInfo.mediaid = Integer.valueOf(mediaid);
//		mediaInfo.source = Integer.valueOf(source);
		mediaInfo.medianame = videoName;
//		mediaInfo.sdkinfo2 = sdkinfo;
		mediaInfo.posterurl = posterurl;
		mediaInfo.videoType = MediaConfig.MEDIA_TYPE_SHORT;
		return mediaInfo;
	}
	
	private String formatUrl(String url) {
		if (!Util.isEmpty(url)) {
			int pos = url.lastIndexOf("http://");
			if (pos >= 0) {
				url = url.substring(pos, url.length());
			}
		}
		return url;
	}
}
