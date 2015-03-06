package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.widget.media.MediaPosterView;

/**
 *@author tangfuling
 *
 */
public class MediaViewFeature extends BaseMediaView {

	private TextView mDescView;
//	private TextView mFavView;
//	private View mClickView;
//	private OnFeatureMediaClickListener onFeatureMediaClickListener;

	public MediaViewFeature(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaViewFeature(Context context) {
		super(context);
	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(mPosterView != null){
            mPosterView.setPosterType(MediaPosterView.POSTER_TYPE_UP_CORNER);
        }
        View view = findViewById(R.id.feature_name);
        if(view instanceof TextView){
             mDescView = (TextView)view;  
        }
    }
	
    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(mediaInfo instanceof SpecialSubject) {
            refresh((SpecialSubject) mediaInfo);
        }
    }

//packaged method
	private void refresh(SpecialSubject subject) {
	    if(subject == null){
	        return;
	    }
		mDescView.setText(subject.name);
	}
}
