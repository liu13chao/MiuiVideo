package com.miui.video.widget.banner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.base.BasePagerAdapter;
import com.miui.video.widget.media.MediaViewBanner;
import com.miui.video.widget.media.MediaViewBanner.OnBannerMediaClickListener;

public class BannerPagerAdapter extends BasePagerAdapter {

	private Context mContext;
	private List<Object> mMediaContents = new ArrayList<Object>();
	
//    private Hashtable<Integer, View> mViewCache = new Hashtable<Integer, View>();
	
	private OnBannerMediaClickListener mListener;
	
	private View mViewFirst;
	private View mViewLast;
	
	public BannerPagerAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setData(List<Object> mediaContents) {
		this.mMediaContents.clear();
		this.mMediaContents.addAll(mediaContents);
		notifyDataSetChanged();
	}
	
	public void setData(Object[] mediaContents) {
		this.mMediaContents.clear();
		if(mediaContents != null) {
			for(int i = 0; i < mediaContents.length; i++) {
				this.mMediaContents.add(mediaContents[i]);
			}
		}
		notifyDataSetChanged();
	}
	
	public void setOnBannerMediaClickListener(OnBannerMediaClickListener listener) {
		this.mListener = listener;
	}
	
	@Override
	public int getCount() {
	    if(mMediaContents.size() > 1){
	        return mMediaContents.size() + 1;
	    }
		return mMediaContents.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    if(object instanceof View){
	        View view = (View)object;
	        container.removeView(view);
	        if(position == 0 ){
	            mViewFirst = view;
	        }else if(position == getCount() - 1){
	            mViewLast = view;
	        }
	    }
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int bannerSize = mMediaContents.size();
		MediaViewBanner bannerMediaView = null;
		int bannerPosition = position % bannerSize;
		if(position == 0 && mViewFirst != null){
		    // avoid screen flicker
		    View view = mViewFirst;
		    container.addView(view, 0);
		    return view;
		}else if(position == getCount() - 1 && mViewLast != null){
	          // avoid screen flicker
		    View view = mViewLast;
            container.addView(view);
            return view;
		}
		bannerMediaView = new MediaViewBanner(mContext);
		bannerMediaView.setOnBannerMediaClickListener(mListener);
		bannerMediaView.setBanner(mMediaContents.get(bannerPosition));
		container.addView(bannerMediaView);
		return bannerMediaView;
	}
}
