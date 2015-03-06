/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MixedMediaView.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-10
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.controller.content.MixedMediaContentBuilder;

/**
 * @author tianli
 *
 */
public class MixedMediaView extends MixedPosterMediaView {

    private MixedMediaContentBuilder mMixedContentBuilder;

    public MixedMediaView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public MixedMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MixedMediaView(Context context) {
        super(context);
    }

    @Override
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder) {
        super.setMediaContentBuilder(contentBuilder);
        if(contentBuilder instanceof MixedMediaContentBuilder){
            mMixedContentBuilder = (MixedMediaContentBuilder)contentBuilder;
        }else{
            mMixedContentBuilder = null;
        }
    }

    @Override
    protected boolean isHorizontalPoster() {
        if(mMixedContentBuilder != null){
            if(mMixedContentBuilder.isHorizontalPoster()){
                return true;
            }
        }
        return false;
    }
}
