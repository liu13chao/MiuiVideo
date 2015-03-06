/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MediaPosterView.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-8
 */
package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.controller.BitmapFilter;
import com.miui.video.controller.CornerBitmapFilter;
import com.miui.video.controller.CornerUpBitmapFilter;
import com.miui.video.controller.CoverBitmapFilter;
import com.miui.video.model.ImageManager;
import com.miui.video.storage.BaseDevice;
import com.miui.video.thumbnail.ThumbnailHelper;
import com.miui.video.thumbnail.ThumbnailManager;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.ViewUtils;

/**
 * @author tianli
 *
 */
public class MediaPosterView extends FrameLayout {

    //UI
    protected Context mContext;
    protected View mPoster;
//    protected View mSelector;
    protected View mBorder;
    
    protected BaseMediaInfo mMediaInfo;
    protected BitmapFilter mCornerFilter;    
    protected int mDefaultPoster = R.drawable.default_cover;
    
    public static final int POSTER_TYPE_NO_CORNER = 0;
    public static final int POSTER_TYPE_FULL_CORNER = 1;
    public static final int POSTER_TYPE_UP_CORNER = 2;
    
    protected int mPosterType = POSTER_TYPE_NO_CORNER;
    
    protected int mRadius;
    
    public MediaPosterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediaPosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaPosterView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        mContext = getContext();
        View.inflate(mContext, R.layout.media_cover_view, this);
        mPoster = findViewById(R.id.poster_image);
        mBorder = findViewById(R.id.poster_border);
        
        mRadius = getResources().getDimensionPixelSize(R.dimen.cover_corner_radius);
    }
    
    public void setPosterType(int posterType){
        mPosterType = posterType;
        if(mPosterType == POSTER_TYPE_FULL_CORNER){
            mBorder.setBackgroundResource(R.drawable.poster_border_full_corner);
            mCornerFilter = new CornerBitmapFilter(mRadius);
        }else if(mPosterType == POSTER_TYPE_UP_CORNER){
            mBorder.setBackgroundResource(R.drawable.poster_border_up_corner);
            mCornerFilter = new CornerUpBitmapFilter(mRadius);
        }else{
            mBorder.setBackgroundResource(R.drawable.poster_border_no_corner);
            mCornerFilter = null;
        }
    }
    
    public void setDefaultPosterRes(int defaultPoster){
        mDefaultPoster = defaultPoster;
    }

    public void setMediaInfo(BaseMediaInfo mediaInfo){
        mMediaInfo = mediaInfo;
        refreshContent();
    }
    
    public void setMediaInfoWithDefaultCover(BaseMediaInfo mediaInfo){
        mMediaInfo = mediaInfo;
        setDefaultCover();
    }
    
    private void refreshContent(){
        if(mMediaInfo != null){
            drawCover();
        }
    }

    protected void drawCover() {
        int width = ViewUtils.getViewWidth(this);
        int height = ViewUtils.getViewHeight(this);
        if(isDevice()){
            drawDeviceIcon();
        }else if(isThumbnail()){
            drawThumbnail(width, height);
        }else if(isPoster()){
            drawPoster(width, height);
        }else{
            setDefaultCover(0, 0);
        }
    }
    
    private void drawPoster(int width, int height){
        if(!ImageManager.getInstance().fetchImage(ImageManager.createTask(
                mMediaInfo.getPosterInfo(), new CoverBitmapFilter(width, height, mCornerFilter)) , mPoster)){
            setDefaultCover(width, height);
        }
    }
    
    public void drawDeviceIcon(){
        mPoster.setBackgroundResource(R.drawable.poster_device);
    }
    
    public void drawThumbnail(int width, int height){
        if(!DKApp.getSingleton(ThumbnailManager.class).fetchThumbnail(
                ThumbnailHelper.generateThumbnailTaskInfo(mMediaInfo), mPoster)){
            setDefaultCover(width, height);
        }
    }

    protected void setDefaultCover(int width, int height){
//        if(width > 0 && height > 0){
//            Bitmap b = DKApp.getSingleton(CoverBitmapCache.class).
//                    getDefaultCover(width, height, mDefaultPoster);
//            mPoster.setBackgroundDrawable(new BitmapDrawable(b));
//        }else{
            mPoster.setBackgroundResource(mDefaultPoster);
//        }
    }
    
    public void setDefaultCover(){
//        int width = ViewUtils.getViewWidth(this);
//        int height = ViewUtils.getViewHeight(this);
        setDefaultCover(0, 0);
        mPoster.setTag(null);
    }
    
    private boolean isThumbnail(){
        if(mMediaInfo != null && mMediaInfo.getPosterInfo() == null){
            return true;
        }
        return false;
    }
    
    private boolean isPoster(){
        if(mMediaInfo != null && mMediaInfo.getPosterInfo() != null){
            return true;
        }
        return false;
    }
    
    private boolean isDevice(){
        if(mMediaInfo instanceof BaseDevice){
            return true;
        }
        return false;
    }

}
