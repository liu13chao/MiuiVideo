/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   EmptyView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-18
 */
package com.miui.video.widget;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class EmptyView extends FrameLayout {

    public int mTitle;
    public int mSubtitle;
    public int mIcon;
    
    public TextView mTitleView;
    public TextView mSubtitleView;
    public ImageView mIconView;
    
    public EmptyView(Context context) {
        super(context);
        init();
    }
    
    public EmptyView(Context context, int title, int icon) {
        super(context);
        init();
        setTitle(title);
        setIcon(icon);
    }
    
    public EmptyView(Context context, int title, int subtitle, int icon) {
        super(context);
        init();
        setTitle(title);
        setIcon(icon);
        setSubtitle(subtitle);
    }
    
    private void init(){
        View.inflate(getContext(), R.layout.empty_view_media, this);
        mTitleView = (TextView)findViewById(R.id.empty_title);
        mSubtitleView = (TextView)findViewById(R.id.empty_sub_title);
        mIconView = (ImageView)findViewById(R.id.empty_icon);
    }

    public void setTitle(int title) {
        this.mTitle = title;
        mTitleView.setText(mTitle);
    }

    public void setSubtitle(int subtitle) {
        this.mSubtitle = subtitle;
        mTitleView.setText(mSubtitle);
    }

    public void setIcon(int icon) {
        this.mIcon = icon;
        mIconView.setImageResource(mIcon);
    }

}
