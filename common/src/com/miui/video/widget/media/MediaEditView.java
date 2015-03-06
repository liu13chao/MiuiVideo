/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MediaEditView.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-22
 */
package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class MediaEditView extends FrameLayout{

    private ImageView mSelector;
    
    protected boolean mIsInEditMode;
    
    public MediaEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediaEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaEditView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        mSelector = new ImageView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT);
        params.gravity =Gravity.RIGHT | Gravity.TOP;
        mSelector.setLayoutParams(params);
        mSelector.setImageResource(R.drawable.media_view_image_status);
        addView(mSelector);
        setBackgroundResource(R.drawable.media_view_image_selector_border);
        setPadding(0, 0, 0, 0);
    }

    public void setInEditMode(boolean inEditMode){
        mIsInEditMode = inEditMode;
        if(mIsInEditMode){
            setVisibility(View.VISIBLE);
        }else {
            setVisibility(View.INVISIBLE);
        }
    }
    
    public void setMediaInfo(BaseMediaInfo mediaInfo){
        if(mediaInfo != null && mediaInfo.mIsSelected){
            setSelected(true);
            mSelector.setSelected(true);
        }else{
            setSelected(false);
            mSelector.setSelected(false);
        }
    }
}
