package com.miui.video.widget.banner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.miui.video.FeatureMediaActivity;
import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.base.BaseViewPager;
import com.miui.video.live.TvPlayManager;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.media.MediaViewBanner;
import com.miui.video.widget.media.MediaViewBanner.OnBannerMediaClickListener;
import com.miui.video.widget.pager.ViewPagerBanner;

public class BannerView extends RelativeLayout {

	private Context mContext;
	
	//UI
	private ViewPagerBanner mViewPager;
	private BannerPagerAdapter mPagerAdapter;
	private BannerIndicateView mIndicateView;
	
	//data
	private List<Object> mMediaViewContents = new ArrayList<Object>();
	
	private Handler mHandler = new Handler();
	private int mIndicatePeriod = 8000;
	
	public OnBannerSelectListener mListener;
	
	public BannerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public BannerView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setListener(OnBannerSelectListener listener) {
		this.mListener = listener;
	}
	
	public void setBanners(List<Object> banners) {
		mMediaViewContents.clear();
		if(banners != null && banners.size() > 0) {
			mMediaViewContents.addAll(banners);
		}
		mPagerAdapter.setData(mMediaViewContents);
		mIndicateView.setIndicateCount(mMediaViewContents.size());
		startIndicateTask();
	}
	
	public void setBanners(Object[] banners) {
		mMediaViewContents.clear();
		if(banners != null && banners.length > 0) {
			for(int i = 0; i < banners.length; i++) {
				mMediaViewContents.add(banners[i]);
			}
		}
		mPagerAdapter.setData(mMediaViewContents);
		mIndicateView.setIndicateCount(mMediaViewContents.size());
		startIndicateTask();
	}
	
	public void stopIndicateTask() {
		mHandler.removeCallbacks(mBannerIndicateRunnable);
	}
	
	public void startIndicateTask() {
		stopIndicateTask();
		mHandler.postDelayed(mBannerIndicateRunnable, mIndicatePeriod);
	}

	//init
	private void init() {
		mViewPager = new ViewPagerBanner(mContext);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mPagerAdapter = new BannerPagerAdapter(mContext);
		mPagerAdapter.setOnBannerMediaClickListener(mOnBannerMediaClickListener);
		mViewPager.setAdapter(mPagerAdapter);
		addView(mViewPager);
		
		mIndicateView = new BannerIndicateView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.banner_indicate_view_bottom_margin);
		addView(mIndicateView, params);
	}
	
	//packaged method	
	private void incCurIndicate() {
		int curIndex = mViewPager.getCurrentItem();
		int pageCount = mPagerAdapter.getCount();
		if(pageCount > 1) {
		    int index = (curIndex + 1) % pageCount;
			mViewPager.setCurrentItem(index);
			startIndicateTask();
		}
	}
	
	private void notifyBannerSelecte(int position) {
		if(mListener != null) {
			mListener.onBannerSelected(position);
		}
	}
	
	//UI callback
	private OnBannerMediaClickListener mOnBannerMediaClickListener = new OnBannerMediaClickListener() {
		
		@Override
		public void onBannerMediaClick(MediaViewBanner view, Object contentInfo) {
			if(contentInfo instanceof TelevisionInfo) {
				TelevisionInfo televisionInfo = (TelevisionInfo) contentInfo;
				TvPlayManager.playChannel(mContext, televisionInfo, SourceTagValueDef.PHONE_V6_BANNER_VALUE);
			} else if(contentInfo instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(mContext, MediaDetailActivity.class);
				intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)contentInfo);
				intent.putExtra(MediaDetailActivity.KEY_IS_BANNER, true);
				intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_BANNER_VALUE);
				mContext.startActivity(intent);
			} else if(contentInfo instanceof SpecialSubject) {
				SpecialSubject specialSubject = (SpecialSubject) contentInfo;
				Intent intent = new Intent();
				intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
				intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_BANNER_VALUE);
				intent.setClass(mContext, FeatureMediaActivity.class);
				mContext.startActivity(intent);
			}
		}
	};

	private BaseViewPager.OnPageChangeListener mOnPageChangeListener = new BaseViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageScrollStateChanged(int state) {
			if(state == BaseViewPager.SCROLL_STATE_DRAGGING) {
				stopIndicateTask();
			} else if(state == BaseViewPager.SCROLL_STATE_IDLE) {
			    startIndicateTask();
				int curPage = mViewPager.getCurrentItem();
				int pageCount = mPagerAdapter.getCount();
				if(pageCount > 1){
	                if(curPage == pageCount - 1) {
	                    mViewPager.setCurrentItem(0, false);
	                } else if(curPage == 0) {
	                    mViewPager.setCurrentItem(pageCount - 1, false);
	                }
				}
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			mIndicateView.setCurIndex(position);
			notifyBannerSelecte(position);
		}
	};
	
	private Runnable mBannerIndicateRunnable = new Runnable() {
		
		@Override
		public void run() {
			incCurIndicate();
		}
	};
	
	//self def class
	public interface OnBannerSelectListener {
		public void onBannerSelected(int position);
	}
}
