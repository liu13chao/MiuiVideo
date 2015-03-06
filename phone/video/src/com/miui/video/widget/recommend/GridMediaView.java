/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GridMediaView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-20
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.content.BaseGridContentBuilder;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;
import com.miui.video.util.ViewUtils;

/**
 * @author tianli
 *
 */
public class GridMediaView extends BaseMediaView {

    protected TextView mStatusExtra;
    protected TextView mStatus;
    protected TextView mSubtitle;
    protected TextView mName;
    
    private int mStatusPadding;
    
    protected BaseGridContentBuilder mGridContentBuilder;
    
    public GridMediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GridMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridMediaView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mStatus = Util.dynamicCast(findViewById(R.id.media_status), 
                TextView.class);
        mStatusExtra = Util.dynamicCast(findViewById(R.id.media_status_extra), 
                TextView.class);
        mSubtitle = Util.dynamicCast(findViewById(R.id.media_subtitle), 
                TextView.class);
        mName = Util.dynamicCast(findViewById(R.id.media_name), 
                TextView.class);
        mStatusPadding = getResources().getDimensionPixelSize(R.dimen.media_view_status_left_margin);
    }
    
    @Override
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder) {
        super.setMediaContentBuilder(contentBuilder);
        if(contentBuilder instanceof BaseGridContentBuilder){
            mGridContentBuilder = (BaseGridContentBuilder)contentBuilder;
        }else{
            mGridContentBuilder = null;
        }
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        refreshSubtitle();
        refreshStatus();
        refreshName();
        refreshStatusExtra();
    }
    
    private void refreshName() {
        if(mGridContentBuilder != null && mName != null){
            setText(mName, mGridContentBuilder.getName());
            if(mGridContentBuilder.isNameInCenter()){
                mName.setGravity(getVerticalGravity(mName.getGravity()) | Gravity.CENTER_HORIZONTAL);
            }else{
                mName.setGravity(getVerticalGravity(mName.getGravity())  | Gravity.LEFT);
            }
            //TODO:  find out another method to avoid that singleLine effects fling. 
            if(mGridContentBuilder.isSinglelineOK()){
                mName.setSingleLine(true);
            }
        }else{
            setText(mName, "");
        }
    }
    
    private int getVerticalGravity(int gravity){
        if((gravity & Gravity.BOTTOM) == 0){
            return Gravity.BOTTOM;
        }else if((gravity & Gravity.TOP) == 0){
            return Gravity.TOP;
        }else{
            return Gravity.CENTER_VERTICAL;
        }
    }
    
    private void refreshSubtitle() {
        if(mGridContentBuilder != null && mSubtitle != null){
            setText(mSubtitle, mGridContentBuilder.getSubtitle());
            if(mGridContentBuilder.isSubtitleInCenter()){
                mSubtitle.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            }else{
                mSubtitle.setGravity(Gravity.TOP | Gravity.LEFT);
            }
        }else{
            setText(mSubtitle, "");
        }
    }
    
    private void refreshStatus() {
        if(mGridContentBuilder != null && mStatus != null){
            setText(mStatus, mGridContentBuilder.getStatus());
            if(mGridContentBuilder.isStatusInCenter()){
                mStatus.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                mStatus.setPadding(0, mStatus.getPaddingTop(), mStatus.getPaddingRight(), 
                        mStatus.getPaddingBottom());
            }else{
                mStatus.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                mStatus.setPadding(mStatusPadding, mStatus.getPaddingTop(), mStatus.getPaddingRight(), 
                        mStatus.getPaddingBottom());
            }
        }else{
            setText(mStatus, "");
        }
    }
    
    private void refreshStatusExtra() {
        if(mStatusExtra != null){
            if(mGridContentBuilder != null && !TextUtils.isEmpty(mGridContentBuilder.getStatusExtra())){
                setText(mStatusExtra, mGridContentBuilder.getStatusExtra());
            }else{
                ViewUtils.hideView(mStatusExtra);
            }
        }
    }
    
}
