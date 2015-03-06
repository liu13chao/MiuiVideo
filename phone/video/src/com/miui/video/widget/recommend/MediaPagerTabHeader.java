/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MediaPagerTabHeader.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-10
 */
package com.miui.video.widget.recommend;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.widget.recommend.MediaPagerTab.OnTitleSelectedListener;

/**
 * @author tianli
 *
 */
public class MediaPagerTabHeader extends LinearLayout{

    private MediaPagerTab mTabView;
    
    public MediaPagerTabHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MediaPagerTabHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaPagerTabHeader(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        View view = findViewById(R.id.recommend_tabs);
        if(view instanceof MediaPagerTab){
            mTabView = (MediaPagerTab)view;
        }
    }
    
    public void setTabTitleSelectListener(OnTitleSelectedListener listener){
        if(mTabView != null){
            mTabView.setOnTitleSelectedListener(listener);
        }
    }
    
    public void setTabs(List<String> tabs){
        if(mTabView != null){
            mTabView.setTitle(tabs);
        }
    }
    
    public void setCurPage(int page){
        if(mTabView != null){
            mTabView.setCurPage(page);
        }
    }

}
