package com.miui.video.widget.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.controller.CoverBitmapCache;
import com.miui.video.controller.CoverBitmapFilter;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.model.ImageManager;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Category;
import com.miui.video.type.Channel;
import com.miui.video.type.ImageUrlInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewCategory extends RelativeLayout {

    private Context mContext;
//    private View mContentView;
    //UI
    //	private ImageView mPosterView;
//    private ImageView mIconView;
    private TextView mNameView;
    private TextView mCountView;
    private TextView mMediaView;

    private MediaPosterView mPosterView;
    private ImageView mCategoryIcon;

    private TextView mMeasureView;
    private int mCountLeftMargin;
//    private View mClickView;

    //data
    private Category mCategory;
    private Channel mChannel;

    private OnCategoryMediaClickListener mOnCategoryMediaClickListener;

    public MediaViewCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        //		mContext = context;
        //		init();
    }

    public MediaViewCategory(Context context) {
        super(context);
        //		mContext = context;
        //		init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = getContext();
        mPosterView = (MediaPosterView)findViewById(R.id.poster);
        mCategoryIcon = (ImageView)findViewById(R.id.category_icon);
        mNameView = (TextView)findViewById(R.id.category_media_desc_name);
        mCountView = (TextView)findViewById(R.id.category_media_desc_count);
        mMediaView = (TextView)findViewById(R.id.category_media_desc_media);
        setOnClickListener(mOnClickListener);
        mMeasureView = new TextView(mContext);
        mMeasureView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT));
        mMeasureView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().
                getDimensionPixelSize(R.dimen.font_size_48));
        mCountLeftMargin = getResources().getDimensionPixelSize(
                R.dimen.category_media_count_left_margin);
    }

    public void setCategory(Category category) {
        mCategory = category;
        mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mCategory.id);
        if(mChannel == null && mCategory != null){
            mChannel = new Channel();
            mChannel.id = mCategory.id;
            mChannel.name = mCategory.name;
            mChannel.channeltype = mCategory.channeltype;
        }
        refresh();
    }

    //init
    //	private void init() {
    //		int width = mContext.getResources().getDimensionPixelSize(R.dimen.category_media_view_width);
    //		
    //		mContentView = View.inflate(mContext, R.layout.media_view_category, null);
    //		LayoutParams contentParams = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
    //		addView(mContentView, contentParams);
    //		
    ////		mPosterView = (ImageView)mContentView.findViewById(R.id.category_media_poster);
    ////		mIconView = (ImageView) mContentView.findViewById(R.id.category_media_desc_icon);
    ////		mNameView = (TextView) mContentView.findViewById(R.id.category_media_desc_name);
    ////		mCountView = (TextView) mContentView.findViewById(R.id.category_media_desc_count);
    ////		mMediaView = (TextView) mContentView.findViewById(R.id.category_media_desc_media);
    ////		mClickView = mContentView.findViewById(R.id.category_media_click);
    //		mClickView.setOnClickListener(mOnClickListener);
    //	}

    private int measureNameText(CharSequence text){
        if(!TextUtils.isEmpty(text)){
            mMeasureView.setText(text);
            mMeasureView.measure(0, 0);
            return mMeasureView.getMeasuredWidth();
        }
        return 0;
    }
    
    //packaged method
    private void refresh() {
        if(mCategory == null) {
            return;
        }
        if(!TextUtils.isEmpty(mCategory.name)){
            mNameView.setText(mCategory.name);  
        }else if(mChannel != null){
            mNameView.setText(mChannel.name);
        }
        String countStr = mContext.getResources().getString(R.string.gong_count_bu);
        countStr = String.format(countStr, mCategory.totalcount);
        mCountView.setText(countStr);
        int width = measureNameText(mNameView.getText());
        mCountView.setTranslationX(width + mCountLeftMargin);
        StringBuffer sb = new StringBuffer();
        String[] nameList = mCategory.namelist;
        if(nameList != null) {
            for(int i = 0; i < nameList.length; i++) {
                sb.append(nameList[i]);
                if(i != nameList.length - 1) {
                    sb.append('、');
                }
            }
        }
        mMediaView.setText(sb.toString());
        
        mPosterView.setPosterType(MediaPosterView.POSTER_TYPE_UP_CORNER);
        mPosterView.setMediaInfo(new BaseMediaInfo() {
            private static final long serialVersionUID = 1L;
            @Override
            public ImageUrlInfo getPosterInfo() {
                return mCategory != null ? mCategory.poster : null;
            }
        });
        refreshDefaultIcon();
    }
    
    private void refreshDefaultIcon(){
        if(mCategoryIcon == null){
            return;
        }
        int width = getResources().getDimensionPixelSize(R.dimen.category_icon_width);
        int height = getResources().getDimensionPixelSize(R.dimen.category_icon_height);
        if(!ImageManager.isUrlDone(mCategory.icon, mCategoryIcon)){
            Bitmap b = DKApp.getSingleton(CoverBitmapCache.class).getDefaultCover(width, height, 
                    R.drawable.category_icon_default);
            mCategoryIcon.setImageBitmap(b);
            ImageManager.getInstance().fetchImage(ImageManager.createTask(
                    mCategory.icon, new CoverBitmapFilter(width, height)) , mCategoryIcon);
        }
    }

//    public void refreshDefaultPoster() {
//        ImageUrlInfo imageUrlInfo = null;
//        if(mCategory != null) {
//            imageUrlInfo = mCategory.poster;
//        }
//        int radius = mContext.getResources().getDimensionPixelSize(R.dimen.video_common_radius_9);
//        if(!ImageManager.isUrlDone(imageUrlInfo, mPosterView)) {
//            mPosterView.setImageResource(R.drawable.transparent);
//            ImageManager.getInstance().fetchImage(ImageManager.createTask(imageUrlInfo, 
//                    new CornerUpBitmapFilter(radius)),  mPosterView);
//        }
//    }
    
    public void setOnCategoryMediaClickListener(
            OnCategoryMediaClickListener listener) {
        this.mOnCategoryMediaClickListener = listener;
    }

    //UI callback
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnCategoryMediaClickListener != null) {
                mOnCategoryMediaClickListener.onCategoryMediaClick(MediaViewCategory.this, mChannel);
            }
        }
    };

    public interface OnCategoryMediaClickListener {
        public void onCategoryMediaClick(MediaViewCategory view, Channel channel);
    }
}
