/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MixedPosterMediaView.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-10
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.media.MediaPosterView;

/**
 * @author tianli
 *
 */
public abstract class MixedPosterMediaView extends GridMediaView {

    private MediaPosterView mVerticalPoster;
    private MediaPosterView mHorizontalPoster;
    
    public MixedPosterMediaView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public MixedPosterMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MixedPosterMediaView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = findViewById(R.id.v_poster);
        if(view instanceof MediaPosterView){
            mVerticalPoster = (MediaPosterView)view;
        }
        view = findViewById(R.id.h_poster);
        if(view instanceof MediaPosterView){
            mHorizontalPoster = (MediaPosterView)view;
        }
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(isHorizontalPoster()){
            refreshHorizontalPoster();
        }else{
            refreshVerticalPoster();
        }
    }
    
    abstract protected boolean isHorizontalPoster();

    protected void refreshHorizontalPoster(){
        if(mVerticalPoster != null){
          mVerticalPoster.setDefaultPosterRes(R.drawable.default_poster_blur);
          mVerticalPoster.setMediaInfoWithDefaultCover(mMediaInfo);
      }
      if(mHorizontalPoster != null){
          mHorizontalPoster.setVisibility(View.VISIBLE);
          mHorizontalPoster.setDefaultPosterRes(R.drawable.transparent);
          mHorizontalPoster.setMediaInfo(mMediaInfo);
      }
    }
    
    protected void refreshVerticalPoster(){
        if(mVerticalPoster != null){
            mVerticalPoster.setVisibility(View.VISIBLE);
            mVerticalPoster.setDefaultPosterRes(R.drawable.default_cover);
            mVerticalPoster.setMediaInfo(mMediaInfo);
        }
        if(mHorizontalPoster != null){
            mHorizontalPoster.setVisibility(View.INVISIBLE);
        }
    }
}
