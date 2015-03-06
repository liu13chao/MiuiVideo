/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   FeatureListAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.R;
import com.miui.video.type.SpecialSubject;

/**
 * @author tianli
 *
 */
public class FeatureListAdapter extends MediaRowInfoAdapter<SpecialSubject> {

    public FeatureListAdapter(Context context) {
        super(context, 2, R.layout.media_view_feature, 
                context.getResources().getDimensionPixelSize(R.dimen.media_view_feature_space));
    }

}
