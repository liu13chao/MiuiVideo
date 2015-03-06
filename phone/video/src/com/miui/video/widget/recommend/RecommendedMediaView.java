/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendedMediaView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.InformationData;
import com.miui.video.util.Util;
import com.miui.video.util.ViewUtils;

/**
 * @author tianli
 *
 */
public class RecommendedMediaView extends GridMediaView {

    protected TextView mInfoName;
    
    public RecommendedMediaView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecommendedMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendedMediaView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInfoName = Util.dynamicCast(findViewById(R.id.info_name));
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(mediaInfo instanceof InformationData){
            refreshInfoData((InformationData)mediaInfo);
        }else{
            refreshMediaInfo();
        }
    }
    
    private void refreshInfoData(InformationData infoData){
        ViewUtils.hideView(mName);
        ViewUtils.hideView(mSubtitle);
        ViewUtils.showView(mInfoName);
        if(mInfoName != null && infoData != null){
            mInfoName.setText(infoData.getName());
        }
    }
    
    private void refreshMediaInfo(){
        ViewUtils.hideView(mInfoName);
        ViewUtils.showView(mName);
        ViewUtils.showView(mSubtitle);
    }

}
