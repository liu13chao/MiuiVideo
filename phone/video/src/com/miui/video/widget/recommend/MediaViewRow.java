/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaViewRow.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-17
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MediaViewRow extends BaseMediaViewRow {

    public int mLayout;
    public int mSpace;
    public int mColumn;
    
    public MediaViewRow(Context context, int layout, int column) {
        this(context, layout, column, context.getResources().getDimensionPixelOffset(
                R.dimen.recommend_cover_space));
    }
    
    public MediaViewRow(Context context, int layout, int column, int space) {
        super(context);
        mSpace = space;
        mColumn = column;
        mLayout = layout;
    }

    @Override
    protected int getColumnNum() {
        return mColumn;
    }

    @Override
    protected int getColumnSpace() {
        return mSpace;
    }

    @Override
    protected BaseMediaView inflateMediaView(ViewGroup parent) {
        return  (BaseMediaView)LayoutInflater.from(getContext()).inflate(mLayout, parent, false);
    }
    
}
