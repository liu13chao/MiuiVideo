/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   DetailAction.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-18
 */
package com.miui.video.controller.action;

import android.app.Activity;
import android.content.Intent;

import com.miui.video.MediaDetailActivity;
import com.miui.video.type.MediaInfo;

/**
 * @author tianli
 *
 */
public class DetailAction extends LauncherAction {

    public String mSourcePath;
    public MediaInfo mMediaInfo;
    public int mMediaId;
    
    public DetailAction(Activity activity, MediaInfo mediaInfo, String sourcePath) {
        super(activity);
        mSourcePath = sourcePath;
        mMediaInfo = mediaInfo;
    }
    
    public DetailAction(Activity activity, int mediaId, String sourcePath) {
        super(activity);
        mSourcePath = sourcePath;
        mMediaId = mediaId;
    }
    
    @Override
    public Intent getIntent() {
        Intent intent = new Intent(mActivity, MediaDetailActivity.class);
        if(mMediaInfo != null){
            intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, mMediaInfo);
        }else{
            intent.putExtra(MediaDetailActivity.KEY_MEDIA_ID, mMediaId);
        }
        intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH,  mSourcePath);
        return intent;
    }

}
