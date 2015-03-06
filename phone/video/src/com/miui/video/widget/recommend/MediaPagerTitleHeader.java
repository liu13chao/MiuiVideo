/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaPagerTitleHeader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MediaPagerTitleHeader extends FrameLayout{

    private TextView mTitleView;
    
    public MediaPagerTitleHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MediaPagerTitleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaPagerTitleHeader(Context context) {
        super(context);
    }
    
    @Override
    protected void onFinishInflate() {
        mTitleView = (TextView)findViewById(R.id.name);
    }

    public void setTitle(String title){
        mTitleView.setText(title);
    }
    
}
