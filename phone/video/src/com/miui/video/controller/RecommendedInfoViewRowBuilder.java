/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendedInfoViewRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-14
 */
package com.miui.video.controller;

import android.content.Context;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.ChannelRecommendationTab;

/**
 * @author tianli
 *
 */
public class RecommendedInfoViewRowBuilder extends RecommendedMediaViewRowBuilder {

    public RecommendedInfoViewRowBuilder(Context context,
            ChannelRecommendation recommendation) {
        super(context, recommendation);
    }

    private int getRowCount(ChannelRecommendationTab tab){
        if(tab != null){
            BaseMediaInfo[] medias = tab.getRecommendMedias();
            if(medias != null && medias.length > 0){
                return 3 + (medias.length - 1) / 2 + (medias.length - 1) % 2;
            }
        }
        return 0;
    }
    
    @Override
    protected void onBuildRows() {
        if(mRecommendation == null || mRecommendation.data == null || mCurPage < 0 ||
                mCurPage >= mRecommendation.data.length){
            return;
        }
        ChannelRecommendationTab tab = mRecommendation.data[mCurPage];
        if(tab== null || tab.getRecommendMedias() == null){
            return;
        }
        int count = getRowCount(tab);
        BaseMediaInfo[] medias = tab.getRecommendMedias();
        for(int i = 0; i < count; i++){
            MediaViewRowInfo rowInfo = new MediaViewRowInfo();
            mRows.add(rowInfo);
            rowInfo.mRowIndex = mRows.size();
            rowInfo.mRowBuilder = this;
            if(i == 0){
                if(mRecommendation.getRecommendTabCount() == 1){
                    rowInfo.mViewType = VIEW_TYPE_TITLE_HEADER;
                }else{
                    rowInfo.mViewType = VIEW_TYPE_TAB_HEADER;
                }
            }else if(i == count - 1){
                rowInfo.mViewType = VIEW_TYPE_FOOTER;
            }else if(i == 1){
                rowInfo.mViewType = VIEW_TYPE_INFO_BANNER;
                if(medias.length > 0){
                    rowInfo.mMediaList.add(medias[0]);
                }
            }else{
                rowInfo.mViewType = VIEW_TYPE_POSTER_H;
                int column = 2;
                int start = (i - 2) * column + 1;
                if(start < medias.length){
                    rowInfo.mMediaList.add(medias[start]);
                        }
                        if(start + 1  < medias.length){
                            rowInfo.mMediaList.add(medias[start + 1]);
                        }
            }
        }
    }
}
