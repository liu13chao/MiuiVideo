/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   VideoViewFactoryProvider.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-1
 */

package com.miui.videoplayer.videoview;

import android.app.Activity;

import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.MediaConfig;
import com.miui.videoplayer.model.OnlineUri;

/**
 * @author tianli
 *
 */
public class VideoViewFactoryProvider {
	
	public final static String TAG = VideoViewFactoryProvider.class.getName();
	
	public static VideoViewFactory getFactory(final BaseUri uri){
		if(uri instanceof OnlineUri){
			System.out.println("create OnlineVideoViewFactory");
			return new OnlineVideoViewFactory((OnlineUri)uri);
		}
		System.out.println("create DuoKanVideoViewFactory");
		return new DuoKanVideoViewFactory();
	}
	
	public static class OnlineVideoViewFactory extends VideoViewFactory{

		private OnlineUri mUri;
		public OnlineVideoViewFactory(OnlineUri uri){
			mUri = uri;
		}
		@Override
		public IVideoView create(Activity context) {
			if(!AndroidUtils.isUseSdk(mUri)){
				System.out.println("create DuoKanVideoView.");
				return new DuoKanVideoView(context);
			}
			if(mUri.getSource() == MediaConfig.MEDIASOURCE_IQIYI_PHONE_TYPE_CODE){
				System.out.println("create QiyiVideoView.");
				QiyiVideoView qiyi = new QiyiVideoView(context);
				return qiyi;
			}else if(mUri.getSource() == MediaConfig.MEDIASOURCE_SOHU_TYPE_CODE){
				System.out.println("create sohuVideoView.");
				SohuVideoView sohu = new SohuVideoView(context);
				return sohu;
			}else if(mUri.getSource() == MediaConfig.MEDIASOURCE_WOLE_PHONE_TYPE_CODE){
				System.out.println("create 56VideoView.");
				WoleVideoView wole = new WoleVideoView(context);
				return wole;
			}else if(mUri.getSource() == MediaConfig.MEDIASOURCE_PPTV_TYPE_CODE){
				System.out.println("create pptv videoview.");
				return new PPTVVideoView(context);
			}else if(mUri.getSource() == MediaConfig.MEDIASOURCE_IFENG_PHONE_TYPE_CODE){
		        return new IfengVideoView(context);
		    }
			System.out.println("create DuoKanVideoView.");
			return new DuoKanVideoView(context);
		}
	}
	
	public static class DuoKanVideoViewFactory extends VideoViewFactory{
		@Override
		public IVideoView create(Activity context) {
			return new DuoKanVideoView(context);
		}
	}
}
