/**
 * 
 */
package com.miui.video.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.util.GenericCreator;
import com.miui.video.util.Util;
import com.miui.video.widget.recommend.MediaPagerTitleHeader;

/**
 * @author tianli
 *
 */
public class MediaPagerTitleBuilder extends MediaViewBuilder {

    private String mTitle;
    
    private Context mContext;
    
    public MediaPagerTitleBuilder(Context context, String title){
        mTitle = title;
        mContext = context;
    }
    
    @Override
    public View getView(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent) {
        MediaPagerTitleHeader view = Util.getObject(convertView, new GenericCreator<MediaPagerTitleHeader>() {
            @Override
            public MediaPagerTitleHeader create() {
                return (MediaPagerTitleHeader)LayoutInflater.from(mContext).
                      inflate(R.layout.recommend_card_title_header, parent, false);
            }
        }, MediaPagerTitleHeader.class);
        view.setTitle(mTitle);
        return view;
    }

}
