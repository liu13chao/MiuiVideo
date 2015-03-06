/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflineLoadingMediaView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-20
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.StringUtils;
import com.miui.video.util.Util;
import com.xiaomi.common.util.Strings;

/**
 * @author tianli
 *
 */
public class OfflineLoadingMediaView extends MixedPosterMediaView{

    private static final int MAX_PROGRESS = 1000;

    private OfflineMedia mOfflineMedia;

    private TextView mStatusText;
    private ImageView mStatusIcon;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private ProgressBar mProgressBar;

    public OfflineLoadingMediaView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public OfflineLoadingMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OfflineLoadingMediaView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view =findViewById(R.id.offline_loading_item_icon);
        mStatusIcon = Util.dynamicCast(view, ImageView.class);
        view = findViewById(R.id.offline_loading_item_title);
        mTitleView = Util.dynamicCast(view, TextView.class);
        view = findViewById(R.id.offline_loading_item_subtitle);
        mSubtitleView = Util.dynamicCast(view, TextView.class);
        view = findViewById(R.id.offline_loading_item_status);
        mStatusText = Util.dynamicCast(view, TextView.class);
        view = findViewById(R.id.offline_loading_item_progress);
        mProgressBar = Util.dynamicCast(view, ProgressBar.class);
        if(mProgressBar != null){
            mProgressBar.setMax(MAX_PROGRESS);
        }
        //        holder.flag = (ImageView) convertView.findViewById(R.id.offline_loading_item_flag);
        //        holder.line = convertView.findViewById(R.id.offline_loading_item_line);
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        mOfflineMedia = Util.dynamicCast(mediaInfo, OfflineMedia.class);
        super.setMediaInfo(mediaInfo);
        refreshOffline();
    }
    

    private void refreshOffline(){
        // title
        if(mOfflineMedia == null){
            return;
        }
        // refresh title
        refreshTitle(); 
        // refresh subtitle
        if(mSubtitleView != null){
            mSubtitleView.setText(mContext.getResources().getString(
                    R.string.download_with,
                    Strings.formatSize(mOfflineMedia.completeSize),
                    Strings.formatSize(mOfflineMedia.fileSize)));
        }
        // progress
        if(mProgressBar != null){
            mProgressBar.setProgress(getProgress(mOfflineMedia.getPercent()));
        }
        // icon
        if(mStatusIcon != null){
            mStatusIcon.setImageResource(getStatusIcon(mOfflineMedia));
        }
        if(mStatusText != null){
            mStatusText.setText(getStatusText(mOfflineMedia));
        }
    }

    private void refreshTitle(){
        if(mTitleView != null){
            if(TextUtils.isEmpty(mOfflineMedia.epName)){
                String name = mOfflineMedia.mediaName;
                if(mOfflineMedia.isMultiSetType()){
                    name += StringUtils.formatString(getResources().getString(R.string.series_ep),
                            mOfflineMedia.episode);
                }
                mTitleView.setText(name);
            }else{
                mTitleView.setText(mOfflineMedia.epName);
            }
        }
    }

    private int getStatusIcon(OfflineMedia media) {
        if (media == null || media.isError()) {
            return R.drawable.btn_offline_fail;
        } else if (media.isLoading()) {
            return R.drawable.btn_offline_loading;
        } else if (media.isPaused()) {
            return R.drawable.btn_offline_pause;
        } else if (media.isWaiting()) {
            return R.drawable.btn_offline_waiting;
        } else {
            return R.drawable.offline_finish_icon;
        }
    }

    private int getProgress(float percent) {
        return getProgress(percent, MAX_PROGRESS);
    }

    private int getProgress(float percent, int max) {
        int progress = Math.round(max / 100 * percent);
        if (progress > max) {
            progress = max;
        } else if (progress < 0) {
            progress = 0;
        }
        return progress;
    }

    private int getStatusText(OfflineMedia media) {
        if (media == null || media.isError()) {
            return R.string.offline_status_failed;
        } else if (media.isLoading()) {
            return R.string.offline_status_loading;
        } else if (media.isPaused()) {
            return R.string.offline_status_paused;
        } else if (media.isWaiting()) {
            return R.string.offline_status_waiting;
        } else {
            return R.string.offline_status_finished;
        }
    }

    @Override
    protected boolean isHorizontalPoster() {
        if(mOfflineMedia != null && mOfflineMedia.getPosterInfo() == null){
            return true;
        }else{
            return false;
        }
    }
}
