/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseMediaViewRow.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.widget.recommend;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.MediaViewClickable;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public abstract class BaseMediaViewRow extends LinearLayout implements MediaViewClickable{

    private static final int MAX_COLUMN = 3;
    private static final int MIN_COLUMN = 1;

    // View
    private BaseMediaView[] mMediaViews;

    // Data
    protected BaseMediaInfo[] mMediaGroup;
    
    public BaseMediaViewRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseMediaViewRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseMediaViewRow(Context context) {
        super(context);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }
    
    private void initMediaViews(){
        int column = getColumnNum();
        column = Math.min(MAX_COLUMN, column);
        column = Math.max(MIN_COLUMN, column);
        mMediaViews = new BaseMediaView[column];
        for(int i = 0; i < column; i++){
            if(i != 0){
                addView(createStubView());
            }
            BaseMediaView mediaView = inflateMediaView(this);
            addView(mediaView);
            mMediaViews[i] = mediaView;
        }
    }
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder) {
        viewChecker();
        for(BaseMediaView view : mMediaViews){
            view.setMediaContentBuilder(contentBuilder);
        }
    }

    private void viewChecker(){
        if(mMediaViews == null){
            initMediaViews();
        }
    }
    
    public void setMediaViewClickListener(MediaViewClickListener handler){
        viewChecker();
        for(BaseMediaView view : mMediaViews){
            view.setViewClickListener(handler);
        }
    }
    
    private View createStubView(){
        View view = new View(getContext());
        view.setLayoutParams(new LayoutParams(getColumnSpace(), 1));
        return view;
    }

    public void setInEditMode(boolean inEditMode){
        viewChecker();
        for(BaseMediaView view : mMediaViews){
            view.setInEditMode(inEditMode);
        }
    }
    
    public void setMediaInfoGroup(BaseMediaInfo[] group){
        viewChecker();
        mMediaGroup = group;
        refreshViews();
    }
    
    public void setMediaInfoGroup(List<BaseMediaInfo> group){
        setMediaInfoGroup(Util.list2Array(group, BaseMediaInfo.class));
    }

    public void refreshViews(){
        viewChecker();
        for(int i = 0; i < mMediaViews.length; i++){
            if(mMediaGroup != null && i < mMediaGroup.length && mMediaGroup[i] != null){
                mMediaViews[i].setMediaInfo(mMediaGroup[i]);
                if(mMediaViews[i].getVisibility() != View.VISIBLE){
                    mMediaViews[i].setVisibility(View.VISIBLE);
                }
            }else{
                if(mMediaViews[i].getVisibility() == View.VISIBLE){
                    mMediaViews[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    
    public BaseMediaView[] getMediaViews(){
        viewChecker();
        return mMediaViews;
    }
    
    public void setContentBackgroudResource(int resid){
        viewChecker();
        for(BaseMediaView view : mMediaViews){
            view.setBackgroundResource(resid);
        }
    }
    
    protected abstract int getColumnNum();

    protected abstract int getColumnSpace();

    protected abstract BaseMediaView inflateMediaView(ViewGroup parent);

}
