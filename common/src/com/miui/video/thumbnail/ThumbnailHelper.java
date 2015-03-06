/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ThumbnailTaskCreator.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-6
 */

package com.miui.video.thumbnail;

import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.miui.video.DKApp;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.storage.MediaItem;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class ThumbnailHelper {

    public final static String TAG = "ThumbnailHelper";
    
    public static  ThumbnailTaskInfo generateThumbnailTaskInfo(Object object) {
        if(object instanceof LocalMedia) {
            LocalMedia localMedia = (LocalMedia) object;
            return new LocalMediaTaskFactory(localMedia).create();
        }else if(object instanceof LocalMediaList) {
            LocalMediaList localMediaList = (LocalMediaList) object;
            List<LocalMedia> list = localMediaList.getLocalMediaList();
            if(list != null && list.size() > 0){
                return new LocalMediaTaskFactory(list.get(0)).create();  
            }
        }else if(object instanceof OfflineMedia) {
            OfflineMedia offlineMedia = (OfflineMedia) object;
            return new OfflineMediaTaskFactory(offlineMedia).create();
        }else if(object instanceof OfflineMediaList) {
            OfflineMediaList offlineMediaList = (OfflineMediaList) object;
            OfflineMedia offlineMedia = offlineMediaList.get(0);
            if(offlineMedia != null){
              return new OfflineMediaTaskFactory(offlineMedia).create();
            }
        }else if(object instanceof MediaItem) {
            MediaItem mediaItem = (MediaItem) object;
            return new MediaItemTaskFactory(mediaItem).create();
        }else if(object instanceof PlayHistory) {
            PlayHistory history = (PlayHistory) object;
            return new PlayHistoryTaskFactory(history).create();
        }
        return null;
    }

    public static class MediaItemTaskFactory extends ThumbnailTaskInfoFactory{
        MediaItem mMediaItem;

        public MediaItemTaskFactory(MediaItem media){
            mMediaItem = media;
        }
        @Override
        public ThumbnailTaskInfo create() {
            if(mMediaItem != null && !Util.isEmpty(mMediaItem.getMediaUrl())) {
                return  new ThumbnailTaskInfo(mMediaItem.getMediaUrl(), DEFAULT_POSITION);
            }
            return null;
        }
    }

    public static class OfflineMediaTaskFactory extends ThumbnailTaskInfoFactory{
        OfflineMedia mOfflineMedia;

        public OfflineMediaTaskFactory(OfflineMedia media){
            mOfflineMedia = media;
        }
        @Override
        public ThumbnailTaskInfo create() {
            if(mOfflineMedia != null && mOfflineMedia.isFinished() && !TextUtils.isEmpty(mOfflineMedia.localPath)){
                PlayHistoryManager mgr = DKApp.getSingleton(PlayHistoryManager.class);
                PlayHistory history = mgr.getPlayPositionByMeidaUrl(mOfflineMedia.localPath);
                if(history != null){
                    return new PlayHistoryTaskFactory(history).create();
                }else{
                    return  new ThumbnailTaskInfo(mOfflineMedia.localPath, DEFAULT_POSITION);
                }
            }
            return null;
        }
    }

    public static class LocalMediaTaskFactory extends ThumbnailTaskInfoFactory{
        LocalMedia mLocalMedia;

        public LocalMediaTaskFactory(LocalMedia media){
            mLocalMedia = media;
        }
        @Override
        public ThumbnailTaskInfo create() {
            Log.d(TAG, "LocalMediaTaskFactory .. ");
            if(mLocalMedia != null && !Util.isEmpty(mLocalMedia.mediaPath)) {
                PlayHistoryManager mgr = DKApp.getSingleton(PlayHistoryManager.class);
                PlayHistory history = mgr.getPlayPositionByMeidaUrl(mLocalMedia.mediaPath);
                if(history != null){
                    return new PlayHistoryTaskFactory(history).create();
                }else{
                    return  new ThumbnailTaskInfo(mLocalMedia.mediaPath, DEFAULT_POSITION);
                }
            }
            return null;
        }
    }

    public static class PlayHistoryTaskFactory extends ThumbnailTaskInfoFactory{

        PlayHistory mPlayHistory;
        public PlayHistoryTaskFactory(PlayHistory history){
            mPlayHistory = history;
        }

        @Override
        public ThumbnailTaskInfo create() {
            Log.d(TAG, "PlayHistoryTaskFactory .. ");
            if(mPlayHistory != null && !Util.isEmpty(mPlayHistory.getUrl())) {
                return  new ThumbnailTaskInfo(mPlayHistory.getUrl(), mPlayHistory.playPosition);
            }
            return null;
        }
    }

    public static abstract class ThumbnailTaskInfoFactory{

        static final int DEFAULT_POSITION = 3000;

        public abstract ThumbnailTaskInfo create();
    }
}
