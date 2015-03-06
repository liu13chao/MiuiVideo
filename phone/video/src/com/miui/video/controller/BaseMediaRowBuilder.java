/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  BaseMediaRowBuilder.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-9
 */
package com.miui.video.controller;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.controller.content.MediaContentBuilder;

/**
 * @author tianli
 *
 */
public abstract class BaseMediaRowBuilder implements MediaViewClickable{
    
    protected Context mContext;

    protected ArrayList<MediaViewRowInfo> mRows =  new ArrayList<MediaViewRowInfo>();
    
    protected MediaViewClickListener mViewClickListener;
   
    protected MediaContentBuilder mContentBuilder;
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder){
        mContentBuilder = contentBuilder;
    }
    
    public BaseMediaRowBuilder(Context context){
        mContext = context;
    }
    
    public List<MediaViewRowInfo> build(){
        mRows.clear();
        onBuildRows();
        return mRows;
    }
    
    public void setMediaViewClickListener(MediaViewClickListener viewClickHandler) {
        this.mViewClickListener = viewClickHandler;
    }
    
    public abstract int getViewTypeCount();
    
    protected abstract void onBuildRows();
    
//    protected abstract boolean isRowDirty(Object tag);
//    
//    protected abstract Object getRowTag();

    protected abstract View getViewOfType(MediaViewRowInfo rowInfo, View convertView, 
            ViewGroup parent, boolean inEditMode);

    public List<MediaViewRowInfo> getRows(){
        return mRows;
    }
    
    public View getView(Context context, View convertView, ViewGroup parent , 
            MediaViewRowInfo row,  boolean inEditMode) {
        if(row == null){
            return null;
        }
        ViewHolder holder;
        if(convertView != null && convertView.getTag()  instanceof ViewHolder){
            holder = (ViewHolder)convertView.getTag();
        }else{
            holder = new ViewHolder();
        }
//        if(holder.mRowBuilder == this && !isRowDirty(holder.mTag)){
//            return convertView;
//        }
        holder.mRowBuilder = this;
//        holder.mTag = getRowTag();
        View cacheView = holder.mViewCache.get(row.mViewType);
        cacheView = getViewOfType(row, cacheView, parent, inEditMode);
        holder.mViewCache.put(row.mViewType, cacheView);
        cacheView.setTag(holder);
        return cacheView;
    }
    
    public static class ViewHolder{
        SparseArray<View> mViewCache = new SparseArray<View>();
        Object mTag;
        BaseMediaRowBuilder mRowBuilder;
    }
}
