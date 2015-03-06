/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaViewRows.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-19
 */
package com.miui.video.widget.recommend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.controller.MediaListRowBuilder;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.MediaViewClickable;
import com.miui.video.controller.MediaViewRowInfo;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class MediaViewRows extends LinearLayout implements MediaViewClickable{

    List<MediaViewRow> mMediaRows = new ArrayList<MediaViewRow>();
    
    private MediaViewClickListener mClickListener;
    
    private MediaContentBuilder mContentBuilder;
    
    public MediaViewRows(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediaViewRows(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaViewRows(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        setOrientation(VERTICAL);
    }
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder){
        mContentBuilder = contentBuilder;
        for(MediaViewRow row : mMediaRows){
            if(row != null){
                row.setMediaContentBuilder(contentBuilder);
            }
        }
    }
    
    public void buildRows(BaseMediaInfo[] mMediaList, int column, int layout){
        removeAllViews();
        mMediaRows.clear();
        if(mMediaList != null){
            int space = getResources().getDimensionPixelSize(R.dimen.recommend_cover_space);
            MediaListRowBuilder<BaseMediaInfo> builder = new MediaListRowBuilder
                    <BaseMediaInfo>(getContext(), column, layout, space);
            builder.setDataList(mMediaList);
            List<MediaViewRowInfo> rows = builder.build();
            for(MediaViewRowInfo row : rows){
                View view = builder.getView(getContext(), null, this, row, false);
                if(view instanceof MediaViewRow){
                    MediaViewRow rowView = (MediaViewRow)view;
                    rowView.setMediaContentBuilder(mContentBuilder);
                    rowView.setMediaInfoGroup(row.mMediaList);
                    rowView.setMediaViewClickListener(mClickListener);
                    mMediaRows.add(rowView);
                    addView(view);
                }
            }
        }
    }

    @Override
    public void setMediaViewClickListener(MediaViewClickListener listener) {
        mClickListener = listener;
        for(MediaViewRow view : mMediaRows){
            if(view != null){
                view.setMediaViewClickListener(mClickListener);
            }
        }
    }
}
