/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseMediaView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.widget.media.MediaEditView;
import com.miui.video.widget.media.MediaPosterView;

/**
 * @author tianli
 *
 */
public class BaseMediaView extends RelativeLayout {

    public static final String TAG = "BaseMediaView";
    
    protected Context mContext;
    protected BaseMediaInfo mMediaInfo;
    protected MediaPosterView mPosterView;
    protected MediaEditView mEditView;
    protected MediaContentBuilder mContentBuilder;
    protected View mMaskView;
    
    private MediaViewClickListener mViewClickListener;
    
    public BaseMediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseMediaView(Context context) {
        super(context);
        init();
    }
     
    private void init(){
        mContext = getContext();
        setOnClickListener(mOnClickListener);
        setOnLongClickListener(mOnLongClickListener);
    }
    
    public void setInEditMode(boolean inEditMode){
        if(mEditView != null){
            mEditView.setInEditMode(inEditMode);
        }
    }
    
    public void setMediaInfo(BaseMediaInfo mediaInfo){
        mMediaInfo = mediaInfo;
        if(mContentBuilder != null){
            mContentBuilder.setMediaInfo(mediaInfo);
        }
        if(mPosterView != null){
            mPosterView.setMediaInfo(mediaInfo);
        }
        if(mEditView != null){
            mEditView.setMediaInfo(mediaInfo);
        }
        refreshMask();
    }
    
    private void refreshMask(){
        if(mContentBuilder != null && mContentBuilder.isMaskVisible()){
            if(mMaskView != null){
                mMaskView.setVisibility(View.VISIBLE);
            }
        }else{
            if(mMaskView != null){
                mMaskView.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder) {
        mContentBuilder = contentBuilder;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        DKLog.d(TAG, "onLayout");
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = findViewById(R.id.poster);
        if(view instanceof MediaPosterView){
            mPosterView = (MediaPosterView)view;
        }
        view = findViewById(R.id.edit_selector);
        if(view instanceof MediaEditView){
            mEditView = (MediaEditView)view;
        }
        mMaskView = findViewById(R.id.mask);
    }

    public void setViewClickListener(
            MediaViewClickListener viewClickHandler) {
        this.mViewClickListener = viewClickHandler;
    }
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
//            Log.d(TAG, "onClick");
            View mediaView = BaseMediaView.this;
            if(mViewClickListener == null){
                return;
            }
            if(view == mediaView){
                mViewClickListener.onMediaClick(mediaView, mMediaInfo);
            }
        }
    };
    
    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            View mediaView = BaseMediaView.this;
            if(mViewClickListener != null){
                if(view == mediaView){
                    mViewClickListener.onMediaLongClick(mediaView, mMediaInfo);
                }
            }
            return true;
        }
    };
    
    final protected void setText(TextView view, String text){
        if(view != null){
            if(view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
            view.setText(text);
        }
    }
    
}
