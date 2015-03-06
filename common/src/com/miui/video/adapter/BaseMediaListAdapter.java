/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  EditableGroupAdapter.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-16
 */
package com.miui.video.adapter;

import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.MediaViewClickable;

import android.content.Context;

/**
 * @author tianli
 *
 */
public abstract class BaseMediaListAdapter<T> extends BaseGroupAdapter<T> implements MediaViewClickable{

    protected boolean mIsInEditMode;
    protected MediaViewClickListener mMediaViewClickListener;
    
    public BaseMediaListAdapter(Context context) {
        super(context);
    }

    public void setInEditMode(boolean inEditMode) {
        this.mIsInEditMode = inEditMode;
    }

    @Override
    public void setMediaViewClickListener(MediaViewClickListener listener) {
        mMediaViewClickListener = listener;
    }
    
}
