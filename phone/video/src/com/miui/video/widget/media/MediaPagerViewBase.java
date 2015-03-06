package com.miui.video.widget.media;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.ChannelActivity;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.info.InfoChannelActivity;
import com.miui.video.live.TvChannelActivity;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.widget.media.MediaPagerTitle.OnTitleSelectedListener;
/**
 *@author tangfuling
 *
 */
public abstract class MediaPagerViewBase extends LinearLayout {

	private Context mContext;
	
	private View mContentView;
	private MediaViewGrid mMediaViewGrid;
	private MediaPagerTitle mMediaPagerTitle;
	private View mMediaPagerTabBg;
	private TextView mMoreView;
	
	private RecommendationLoader mRecommendationLoader;
	private ChannelRecommendation mChannelRecommend;
//	private Channel mChannel;
//	private Channel[] mRecsubChannels;
	
	private List<String> mPagerTitles = new ArrayList<String>();
//	private List<Channel> mPagerChannels = new ArrayList<Channel>();
	
	private int mMediaPagerTabWidth;
	private int mMediaPagerTabCornerWidth;
	private int mMediaPagerTabShadowLength;
	
	private int mCurPage = 0;
	private int maxPage = 3;
	
	public MediaPagerViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MediaPagerViewBase(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setRecommendationLoader(RecommendationLoader recommendationLoader) {
		this.mRecommendationLoader = recommendationLoader;
		buildData();
		refresh();
	}
	
	public void setChannelRecommendation(ChannelRecommendation recommend){
	    mChannelRecommend = recommend;
	    if(mChannelRecommend != null && mRecommendationLoader != null){
	        buildData();
	        refresh();
	    }
	}
	
//	public void setChannel(Channel channel) {
//		this.mChannel = channel;
//		if(mChannel != null) {
////			mRecsubChannels = mChannel.recsub;
//			if(mChannel.isInformationType()){
//				Channel[] sub = mChannel.sub;
//				List<Channel> list = new ArrayList<Channel>();
//				for(int i = 0; i < sub.length; i++) {
//					list.add(sub[i]);
//				}
//				InfoChannelDataFactory.getInstance().init(list);
//			}
//		}
//		buildData();
//		refresh();
//	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mMediaViewGrid.setOnItemClickListener(onItemClickListener);
	}
	
	public void setShowSubTitle(boolean showSubTitle) {
		mMediaViewGrid.setShowSubTitle(showSubTitle);
	}
	
	//init
	private void init() {
		setOrientation(VERTICAL);
		mMediaPagerTabWidth = mContext.getResources().getDimensionPixelSize(R.dimen.media_pager_tab_bg_width);
		mMediaPagerTabCornerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.media_pager_tab_bg_corner_width);
		mMediaPagerTabShadowLength = mContext.getResources().getDimensionPixelSize(R.dimen.media_pager_tab_bg_shadow_length);
		mContentView = View.inflate(mContext, getContentViewRes(), null);
		addView(mContentView);
		mMediaPagerTitle = (MediaPagerTitle) mContentView.findViewById(R.id.media_pager_title);
		mMediaPagerTitle.setOnTitleSelectedListener(mOnTitleSelectedListener);
		mMediaViewGrid = (MediaViewGrid) mContentView.findViewById(R.id.media_pager_content);
		mMediaPagerTabBg = mContentView.findViewById(R.id.media_pager_tab_bg);
		mMoreView = (TextView) mContentView.findViewById(R.id.media_pager_btn_more);
		mMoreView.setOnClickListener(mOnClickListener);
	}
	
	//packaged method
	private void buildData() {
	    if(mChannelRecommend != null && mRecommendationLoader != null){
	        mPagerTitles.clear();
	        mPagerTitles.addAll(mRecommendationLoader.getChannelRecommendTabs(mChannelRecommend));
	    }
//		mPagerTitles.addAll(mRecommendationLoader.getChannelRecommendTabs(mChannel));
//		mPagerChannels.clear();
//		if(mRecsubChannels == null || mRecommendationLoader == null) {
//			return;
//		}
//		for(int i = 0; i < mRecsubChannels.length; i++) {
//			if(mRecsubChannels[i] != null) {
//				mPagerTitles.add(mRecsubChannels[i].name);
//				mPagerChannels.add(mRecsubChannels[i]);
//			}
//		}
	}
	
//	private BaseMediaInfo[] getCurRecommendMedias() {
//		if(mCurPage < mPagerChannels.size()) {
//			Channel channel = mPagerChannels.get(mCurPage);
//			if(channel != null && mRecommendationLoader != null) {
//				return mRecommendationLoader.getRecommendation(channel);
//			}
//		}
//		return null;
//	}
	
	public int getCurrentPage(){
	    return mCurPage;
	}
	
	private void refresh() {
		refreshBtnMore();
		refreshPagerTitle();
		refreshMediaViewRows();
		refreshPagerTab();
	}
	
	private void refreshBtnMore() {
		if(mChannelRecommend == null) {
			return;
		}
		String str = mContext.getResources().getString(R.string.more);
		String name = mChannelRecommend.name;
		if(TextUtils.isEmpty(name)){
            Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannelRecommend.id);
            if(channel != null){
                name = channel.name;
            }
		}
		if(TextUtils.isEmpty(name)){
		    name = "";
		}
		str = String.format(str, name);
		mMoreView.setText(str);
	}
	
	private void refreshPagerTitle() {
		mMediaPagerTitle.setTitle(mPagerTitles);
	}
	
	private void refreshPagerTab() {
		int pagerTabWidth = mMediaPagerTabWidth;
		int frome = (int) mMediaPagerTabBg.getTranslationX();
		int to = mCurPage * mMediaPagerTabWidth;
		if(mCurPage == 0) {
			pagerTabWidth += mMediaPagerTabCornerWidth;
			mMediaPagerTabBg.setBackgroundResource(R.drawable.media_pager_tab_left);
			to += mMediaPagerTabShadowLength;
		} else if(mCurPage == maxPage - 1) {
			pagerTabWidth += mMediaPagerTabCornerWidth;
			mMediaPagerTabBg.setBackgroundResource(R.drawable.media_pager_tab_mid);
		} else {
			pagerTabWidth += mMediaPagerTabShadowLength;
			mMediaPagerTabBg.setBackgroundResource(R.drawable.media_pager_tab_mid);
			to += mMediaPagerTabShadowLength;
		}
		
		ViewGroup.LayoutParams params = mMediaPagerTabBg.getLayoutParams();
		params.width = pagerTabWidth;
		mMediaPagerTabBg.setLayoutParams(params);
		ObjectAnimator animator = ObjectAnimator.ofFloat(mMediaPagerTabBg, "translationX", frome, to);
		animator.setDuration(0);
		animator.start();
	}
	
	private void refreshMediaViewRows() {
	    BaseMediaInfo[] group = mRecommendationLoader.getRecommendationMedia(
	            mChannelRecommend, mCurPage);
	    if(group != null){
	        mMediaViewGrid.setGroup(group);
	    }
	}
	
	//UI callback
	private OnTitleSelectedListener mOnTitleSelectedListener = new OnTitleSelectedListener() {
		@Override
		public void onTitleSelected(int position) {
			mCurPage = position;
			refreshMediaViewRows();
			refreshPagerTab();
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mChannelRecommend == null) {
				return;
			}
			Channel channel = mChannelRecommend.buildChannel();
			if(channel.isTvChannel()) {
				Intent intent = new Intent();
				intent.setClass(mContext, TvChannelActivity.class);
				mContext.startActivity(intent);
			} else if(channel.isInformationType()) {
				Intent intent = new Intent();
				intent.putExtra(InfoChannelActivity.KEY_CHANNEL, channel);
				intent.setClass(mContext, InfoChannelActivity.class);
				mContext.startActivity(intent);
			} else {
				Intent intent = new Intent();
				intent.putExtra(ChannelActivity.KEY_CHANNEL, channel);
				intent.setClass(mContext, ChannelActivity.class);
				mContext.startActivity(intent);
			}
		}
	};

	protected abstract int getContentViewRes();
}
