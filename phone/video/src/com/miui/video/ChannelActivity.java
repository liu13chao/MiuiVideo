package com.miui.video;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.ChannelAllFragment;
import com.miui.video.fragment.ChannelChoiceFragment;
import com.miui.video.helper.OnTouchInterceptor;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;
import com.miui.video.widget.searchbox.SearchHintPopWindow.OnPerformSearchListener;

/**
 *@author tangfuling
 *
 */

public class ChannelActivity extends BaseFragmentActivity {

	public static String KEY_CHANNEL = "channel";
	public static String KEY_AUTO_INITDATA = "channel_auto_init_data";
	public static String KEY_CHANNEL_IDS = "channel_ids";
	public static String KEY_CHANNEL_NAMES = "channel_names";
	public static String KEY_ROOT_CHANNEL = "root_channel";
	public static String KEY_CATEGORY = "category";
	
	//UI
	private ChannelChoiceFragment mChoiceFragment;
	private ArrayList<ChannelAllFragment> mAllFragments = new ArrayList<ChannelAllFragment>();
	private View mTitleTop;
	private TextView mTitleName;
	
   //	private SearchHintPopWindow mSearchHintView;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private Fragment[] mPages;
	private CharSequence[] mPageTitles;
	
	private int PAGE_CHOICE = 0;
	
	//received data
	private Channel mChannel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);
		init();
	}
	
	private void initReceivedData() {
	    Intent intent = getIntent();
	    Object obj = intent.getSerializableExtra(KEY_CHANNEL);
	    if(obj instanceof Channel) {
	        mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
	        if(mChannel == null){
	            finish();
	        }
	    }
	}

	
	@Override
	protected void onResume() {
		super.onResume();
//		mSearchHintView.refreshDefaultSearchHint();
	}
	
	//init
	private void init() {
		initReceivedData();
		initFragment();
		initUI();
	}
	
	private void initFragment() {
		if(mChannel == null){
			return;
		}
		//根据channel name来判断界面结构
		int[] orders = null;
		int channelType = mChannel.getChannelType();
		switch (channelType) {
		case Channel.CHANNEL_TYPE_MOVIE:
			mPageTitles = new String[4];
			mPageTitles[0] = getResources().getString(R.string.channel_choice);
			mPageTitles[1] = getResources().getString(R.string.channel_hot);
			mPageTitles[2] = getResources().getString(R.string.channel_score);
			mPageTitles[3] = getResources().getString(R.string.channel_new);
			orders = new int[3];
			orders[0] = DKApi.ORDER_BY_HOT;
			orders[1] = DKApi.ORDER_BY_SCORE_DESC;
			orders[2] = DKApi.ORDER_BY_ISSUEDATE;
			break;
		case Channel.CHANNEL_TYPE_VARIETY:
			mPageTitles = new String[2];
			mPageTitles[0] = getResources().getString(R.string.channel_variety_choice);
			mPageTitles[1] = getResources().getString(R.string.channel_variety_hot);
			orders = new int[1];
			orders[0] = DKApi.ORDER_BY_HOT;
			break;
		default:
		    mPageTitles = new String[3];
		    mPageTitles[0] = getResources().getString(R.string.channel_choice);
		    mPageTitles[1] = getResources().getString(R.string.channel_hot);
		    mPageTitles[2] = getResources().getString(R.string.channel_new);
		    orders = new int[2];
		    orders[0] = DKApi.ORDER_BY_HOT;
		    orders[1] = DKApi.ORDER_BY_ISSUEDATE;
		    break;
		}
		
		mChoiceFragment = new ChannelChoiceFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_CHANNEL, mChannel);
		bundle.putInt(KEY_CATEGORY, channelType);
		mChoiceFragment.setArguments(bundle);
		
		if(orders != null){
			for(int i = 0; i < orders.length; i ++){
				ChannelAllFragment af = new ChannelAllFragment();
				bundle = new Bundle();
				bundle.putSerializable(ChannelAllFragment.KEY_CHANNEL, mChannel);
				bundle.putInt(ChannelAllFragment.KEY_SORT_NAME, orders[i]);
				bundle.putInt(ChannelAllFragment.KEY_CATEGORY, channelType);
				af.setArguments(bundle);
				mAllFragments.add(af);
			}
		}
	}
	
	private void initUI() {
		initDecorView();
		initTab();
		initPagerView();
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initTab() {
		initTopBtn();

		mTitleTop = findViewById(R.id.title_top);
		mTitleName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(mOnClickListener);
		
		if(mChannel != null) {
			mTitleName.setText(mChannel.name);
		}
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.channel_pager_view);
		mPages = new Fragment[mAllFragments.size() + 1];
		mPagerView.setTitle(mPageTitles);
		
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mPages[0] = mChoiceFragment;
		for(int i = 0 ; i < mAllFragments.size(); i ++){
			mPages[i + 1] = mAllFragments.get(i);
		}

		mViewPagerAdapter.setPages(mPages);
		mPagerView.setOffscreenPageLimit(3);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
		mPagerView.getPager().setOnTouchInterceptor(mOnTouchInterceptor);
	}
	
	private void initTopBtn() {
		findViewById(R.id.channel_search_btn).setOnClickListener(mOnClickListener);
		findViewById(R.id.channel_filte_btn).setOnClickListener(mOnClickListener);
	}
	
//	private void stopIndicateTask() {
//		BannerView bannerView = mChoiceFragment.getBannerView();
//		if(bannerView != null) {
//			bannerView.stopIndicateTask();
//		}
//	}
//	
//	private void startIndicateTask() {
//		BannerView bannerView = mChoiceFragment.getBannerView();
//		if(bannerView != null) {
//			bannerView.startIndicateTask();
//		}
//	}

//	private void refreshSearchBtn(int orientation) {
//		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			mSearchBox.setVisibility(View.VISIBLE);
//			mSearchBtn.setVisibility(View.GONE);
//		} else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
//			mSearchBox.setVisibility(View.GONE);
//			mSearchBtn.setVisibility(View.VISIBLE);
//		}
//	}
	
	private void startSearchActivity() {
		Intent intent = new Intent();
		intent.setClass(this, SearchActivity.class);
		startActivity(intent);
	}

	private void startFilterActivity(){
	    mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannel);
		if(mChannel != null && mChannel.subfilter != null){
			Intent intent = new Intent(this, ChannelFilterActivity.class);
			intent.putExtra(ChannelActivity.KEY_CHANNEL, mChannel);
			startActivity(intent);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.title_top:
				ChannelActivity.this.finish();
				break;
			case R.id.channel_search_btn:
				startSearchActivity();
				break;
			case R.id.channel_filte_btn:
				startFilterActivity();
				break;
			default:
				break;
			}
		}
	};
	
	private OnPerformSearchListener mOnPerformSearchListener = new OnPerformSearchListener() {
		@Override
		public void onPerformSearch(String keyWord, String keySource,
				int position) {
			Intent intent = new Intent();
			intent.putExtra(SearchActivity.SEARCH_KEY_WORD_TAG, keyWord);
			intent.putExtra(SearchActivity.SEARCH_KEY_SOURCE_TAG, keySource);
			intent.putExtra(SearchActivity.SEARCH_KEY_POSITION_TAG, position);
			intent.setClass(ChannelActivity.this, SearchActivity.class);
			startActivity(intent);
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			if(page == PAGE_CHOICE) {
			    if(mChoiceFragment != null){
			        mChoiceFragment.startBannerIndicator();
			    }
			} else if(page > 0 && page < mAllFragments.size() + 1){
				mAllFragments.get(page - 1).onSelected();
                if(mChoiceFragment != null){
                    mChoiceFragment.stopBannerIndicator();
                }
			}
		}
	};
	
	private OnTouchInterceptor mOnTouchInterceptor = new OnTouchInterceptor() {
		@Override
		public boolean onIntercept(int scrollDirection, MotionEvent event) {
		    if ((scrollDirection == OnTouchInterceptor.SCROLL_LEFT || 
		            scrollDirection == OnTouchInterceptor.SCROLL_RIGHT)
		            && mChoiceFragment != null && mChoiceFragment.isHitBannerView(event)) {
		        return true;
		    }
		    return false;
		}

		@Override
		public void onPreIntercept(MotionEvent event) {
		}
	};
	
	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
//		refreshSearchBtn(newConfig.orientation);
	}
	
//	private void refreshBannerList() {
//		if(mChannel != null) {
//			BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
//			statisticInfo.cateogry = mChannel.name;
//			DKApi.getBannerList(mChannel.id, statisticInfo.formatToJson(), null);
//		}
//	}
}
