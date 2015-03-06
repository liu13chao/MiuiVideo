/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  TvMediaViewRow.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-10
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class TvMediaViewRow extends BaseMediaViewRow {
    
    public TvMediaViewRow(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TvMediaViewRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TvMediaViewRow(Context context) {
        super(context);
        init();
    }
    
    private void init(){
    }

    @Override
    protected int getColumnNum() {
        return 3;
    }
    
    @Override
    protected BaseMediaView inflateMediaView(ViewGroup parent) {
        return  (BaseMediaView)LayoutInflater.from(getContext()).inflate(R.layout.media_view_grid_tv, parent, false);
    }

    @Override
    protected int getColumnSpace() {
        return  getResources().getDimensionPixelOffset(
                R.dimen.recommend_cover_space);
    }

}
