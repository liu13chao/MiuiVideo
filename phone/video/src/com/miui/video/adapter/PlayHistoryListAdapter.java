/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayHistoryListAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-16
 */
package com.miui.video.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.HistoryRowBuilder;
import com.miui.video.local.PlayHistory;

/**
 * @author tianli
 *
 */
public class PlayHistoryListAdapter extends BaseRowBuilderAdapter<PlayHistory>{

    private HistoryRowBuilder mRowBuilder;
    
    public PlayHistoryListAdapter(Context context) {
        super(context);
        mRowBuilder = new HistoryRowBuilder(mContext);
    }
    
    public void setPlayHistoryMap(HashMap<String, List<PlayHistory>> map){
        clear();
        mRowBuilder.setHistoryMap(map);
        mRowBuilder.build();
        setGroup(mRowBuilder.getRows());
    }

    @Override
    protected BaseMediaRowBuilder getRowBuilder() {
        return mRowBuilder;
    }
}
