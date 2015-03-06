/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflinePlayView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;
import com.xiaomi.common.util.Strings;

/**
 * @author tianli
 *
 */
public class OfflinePlayView extends BaseMediaView {

    private TextView mTitleView;
    private TextView mFileSizeView;
    private ImageView mPlayIcon;
    
    private OfflineMedia mOfflineMedia;
    
    private boolean mIsInEditMode = false;
    
    public OfflinePlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OfflinePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OfflinePlayView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = Util.dynamicCast(findViewById(R.id.offline_play_title), TextView.class);
        mFileSizeView = Util.dynamicCast(findViewById(R.id.offline_file_size), TextView.class);
        mPlayIcon = Util.dynamicCast(findViewById(R.id.play_icon), ImageView.class);
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        mOfflineMedia = Util.dynamicCast(mMediaInfo, OfflineMedia.class);
        refreshTitleView();
        refreshFileSizeView();
        refreshEditMode();
    }
    
    private void refreshTitleView(){
        if(mOfflineMedia != null && mTitleView != null){
            if(TextUtils.isEmpty(mOfflineMedia.epName)){
                mTitleView.setText(mOfflineMedia.mediaName + " " + mOfflineMedia.episode);
            }else{
                mTitleView.setText(mOfflineMedia.epName);
            }
        }
    }
    
    private void refreshFileSizeView(){
        if(mOfflineMedia != null && mFileSizeView != null){
            mFileSizeView.setText(Strings.formatSize(mOfflineMedia.fileSize));
        }
    }
    
    private void refreshEditMode(){
        if(mPlayIcon != null){
            if(mIsInEditMode && mMediaInfo.mIsSelected){
                mPlayIcon.setSelected(true);
            }else{
                mPlayIcon.setSelected(false);
            }
        }
    }

    @Override
    public void setInEditMode(boolean inEditMode) {
        super.setInEditMode(inEditMode);
        mIsInEditMode = inEditMode;
    }
    
}
