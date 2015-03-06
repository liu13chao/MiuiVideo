/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ClassifyMediaListAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-18
 */
package com.miui.video.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.ClassifyListRowBuilder;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class ClassifyMediaListAdapter<T extends BaseMediaInfo> extends BaseRowBuilderAdapter<T> {

    public ClassifyListRowBuilder<T> mRowBuilder;
    
    public ClassifyMediaListAdapter(Context context, int column, int layout) {
        super(context);
        mRowBuilder = new ClassifyListRowBuilder<T>(context, column, layout);
    }
    
    public void setData(HashMap<String, List<T>> map){
        mRowBuilder.setClassifyMap(map);
        setGroup(mRowBuilder.build());
    }

    @Override
    protected BaseMediaRowBuilder getRowBuilder() {
        return mRowBuilder;
    }
}
