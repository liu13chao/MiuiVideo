/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  ThumbnailTaskFactory.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-20
 */
package com.miui.video.local;

import com.miui.video.thumbnail.ThumbnailTaskInfo;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class ThumbnailTaskFactory {
	
	public static ThumbnailTaskBuilder createBuilder(BaseMediaInfo mediaInfo){
		if(mediaInfo instanceof Favorite){
			return new FavoriteTaskBuilder((Favorite)mediaInfo);
		}
		return new ThumbnailTaskBuilder(mediaInfo);
	}
	
	public static class ThumbnailTaskBuilder{
		protected BaseMediaInfo mMedia;
		protected volatile ThumbnailTaskInfo mTaskInfo;
		
		public ThumbnailTaskBuilder(BaseMediaInfo mediaInfo){
			mMedia = mediaInfo;
		}

		public ThumbnailTaskInfo getThumbnailTask(){
			if(mTaskInfo == null){
				ThumbnailTaskInfo taskInfo = new ThumbnailTaskInfo(mMedia.getUrl(), 0);
				mTaskInfo = taskInfo;
			}
			return mTaskInfo;
		}
	}
	
	public static class FavoriteTaskBuilder extends ThumbnailTaskBuilder{
		
		public FavoriteTaskBuilder(Favorite favorite){
			super(favorite);
		}
	}
}
