/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  TvMediaView.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-10
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.miui.video.R;
import com.miui.video.api.ApiConfig;
import com.miui.video.model.ImageManager;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.TelevisionInfo;

/**
 * @author tianli
 *
 */
public class TvMediaView extends GridMediaView {

    private View mTvBg;
    private ImageView mTvIcon;
    
    private TelevisionInfo mTvInfo;

    public TvMediaView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public TvMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TvMediaView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTvBg = findViewById(R.id.tv_bg);
        View view = findViewById(R.id.tv_icon);
        if(view instanceof ImageView){
            mTvIcon = (ImageView)view;
        }
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(mediaInfo instanceof TelevisionInfo){
            mTvInfo = (TelevisionInfo)mediaInfo;
            refreshTvBg();
            refreshTvIcon();
        }
    }

    private void refreshTvBg(){
        if(mTvInfo != null && mTvBg != null){
            int tvColor = mTvInfo.backgroundcolor;
            switch (tvColor) {
            case ApiConfig.COLOR_ORANGE:
                mTvBg.setBackgroundResource(R.drawable.tv_bg_orange);
                break;
            case ApiConfig.COLOR_RED:
                mTvBg.setBackgroundResource(R.drawable.tv_bg_red);
                break;
            case ApiConfig.COLOR_GREEN:
                mTvBg.setBackgroundResource(R.drawable.tv_bg_green);
                break;
            case ApiConfig.COLOR_BLUE:
                mTvBg.setBackgroundResource(R.drawable.tv_bg_blue);
                break;
            default:
                mTvBg.setBackgroundResource(R.drawable.default_border_right_angle);
                break;
            }
        }
    }
    
    private void refreshTvIcon(){
        if(mTvIcon != null && mTvInfo != null){
            if(!ImageManager.isUrlDone(mTvInfo.getPosterInfo(), mTvIcon)){
                mTvIcon.setImageResource(R.drawable.transparent);
                ImageManager.getInstance().fetchImage(ImageManager.createTask(
                        mTvInfo.getPosterInfo(), null), mTvIcon);
            }
        }
    }
    
//    @Override
//    protected void refreshMediaDesc() {
//        super.refreshMediaDesc();
//        if(mName != null){
//            mName.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//        }
//        if(mSubtitle != null){
//            mSubtitle.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//        }
//    }
//
//    @Override
//    protected void refreshMediaStatus() {
//        super.refreshMediaStatus();
//        if(mStatus != null){
//            mStatus.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//        }
//    }
}
