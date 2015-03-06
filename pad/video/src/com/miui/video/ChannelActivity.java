package com.miui.video;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.ChannelAllFragment;
import com.miui.video.fragment.ChannelChoiceFragment;
import com.miui.video.fragment.ChannelRankFragment;
import com.miui.video.statistic.BannerListStatisticInfo;
import com.miui.video.type.Channel;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;
import com.miui.video.widget.searchbox.SearchBox;
import com.miui.video.widget.searchbox.SearchHintPopWindow;
import com.miui.video.widget.searchbox.SearchHintPopWindow.OnPerformSearchListener;

/**
 *@author tangfuling
 *
 */

public class ChannelActivity extends BaseFragmentActivity {

	public static String KEY_CHANNEL = "channel";
	
	//UI
	private ChannelChoiceFragment mChoiceFragment;
	private ChannelRankFragment mRankFragment;
	private ChannelAllFragment mAllFragment;
	private View mTitleTop;
	private TextView mTitleName;
	private Button mChoiceBtn;
	private Button mRankBtn;
	private Button mAllBtn;
	
	private ImageButton mSearchBtn;
	private SearchBox mSearchBox;
	private SearchHintPopWindow mSearchHintView;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private Fragment[] mPages;
	
	private int PAGE_CHOICE = 0;
	private int PAGE_RANK = 1;
	private int PAGE_ALL = 2;
	
	//received data
	private Channel mChannel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);
		init();
		
		uploadChannelStatistic();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSearchHintView.refreshDefaultSearchHint();
		mSearchBox.setText("");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//init
	private void init() {
		initReceivedData();
		initFragment();
		initUI();
		initConfig();
	}
	
	private void initConfig() {
		int orientation = getResources().getConfiguration().orientation;
		refreshSearchBtn(orientation);
	}
	
	private void initFragment() {
		mChoiceFragment = new ChannelChoiceFragment();
		mRankFragment = new ChannelRankFragment();
		mAllFragment = new ChannelAllFragment();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_CHANNEL, mChannel);
		mChoiceFragment.setArguments(bundle);
		mRankFragment.setArguments(bundle);
		mAllFragment.setArguments(bundle);
	}
	
	private void initUI() {
		initDecorView();
		initTab();
		initPagerView();
		showChannelChoice();
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initTab() {
		initSearchBox();
		
		mTitleTop = findViewById(R.id.channel_title_top);
		mTitleName = (TextView) findViewById(R.id.channel_title_name);
		mChoiceBtn = (Button) findViewById(R.id.channel_btn_choice);
		mRankBtn = (Button) findViewById(R.id.channel_btn_rank);
		mAllBtn = (Button) findViewById(R.id.channel_btn_all);
		mTitleTop.setOnClickListener(mOnClickListener);
		mChoiceBtn.setOnClickListener(mOnClickListener);
		mRankBtn.setOnClickListener(mOnClickListener);
		mAllBtn.setOnClickListener(mOnClickListener);
		
		if(mChannel != null) {
			mTitleName.setText(mChannel.name);
		}
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.channel_pager_view);
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mPages = new Fragment[3];
		mPages[0] = mChoiceFragment;
		mPages[1] = mRankFragment;
		mPages[2] = mAllFragment;
		
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setOffscreenPageLimit(3);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
	}
	
	private void initSearchBox() {
		mSearchBtn = (ImageButton) findViewById(R.id.channel_search_btn);
		mSearchBtn.setOnClickListener(mOnClickListener);
		mSearchBox = (SearchBox) findViewById(R.id.channel_search_box);
		if(mSearchHintView == null) {
			mSearchHintView = new SearchHintPopWindow(this, mSearchBox);
			mSearchHintView.addListener(mOnPerformSearchListener);
		}
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_CHANNEL);
		if(obj instanceof Channel) {
			mChannel = (Channel) obj;
		}
	}
	
	//packaged method
	private void showChannelChoice() {
		setChoiceBtnSelected();
	}
	
	private void showChannelRank() {
		mRankFragment.onSelected();
		setRankBtnSelected();
	}
	
	private void showChannelAll() {
		mAllFragment.onSelected();
		setAllBtnSelected();
	}
	
	private void setChoiceBtnSelected() {
		mChoiceBtn.setSelected(true);
		mRankBtn.setSelected(false);
		mAllBtn.setSelected(false);
	}
	
	private void setRankBtnSelected() {
		mRankBtn.setSelected(true);
		mChoiceBtn.setSelected(false);
		mAllBtn.setSelected(false);
	}
	
	private void setAllBtnSelected() {
		mAllBtn.setSelected(true);
		mChoiceBtn.setSelected(false);
		mRankBtn.setSelected(false);
	}

	private void refreshSearchBtn(int orientation) {
		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mSearchBox.setVisibility(View.VISIBLE);
			mSearchBtn.setVisibility(View.GONE);
		} else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
			mSearchBox.setVisibility(View.GONE);
			mSearchBtn.setVisibility(View.VISIBLE);
		}
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.channel_title_top:
				ChannelActivity.this.finish();
				break;
			case R.id.channel_btn_choice:
				mPagerView.setCurPage(PAGE_CHOICE);
				break;
			case R.id.channel_btn_rank:
				mPagerView.setCurPage(PAGE_RANK);
				break;
			case R.id.channel_btn_all:
				mPagerView.setCurPage(PAGE_ALL);
				break;
			case R.id.channel_search_btn:
				Intent intent = new Intent();
				intent.setClass(ChannelActivity.this, SearchResultActivity.class);
				startActivity(intent);
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
			intent.putExtra(SearchResultActivity.SEARCH_KEY_WORD_TAG, keyWord);
			intent.putExtra(SearchResultActivity.SEARCH_KEY_SOURCE_TAG, keySource);
			intent.putExtra(SearchResultActivity.SEARCH_KEY_POSITION_TAG, position);
			intent.setClass(ChannelActivity.this, SearchResultActivity.class);
			startActivity(intent);
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			if(page == PAGE_CHOICE) {
				showChannelChoice();
			} else if(page == PAGE_RANK) {
				showChannelRank();
			} else if(page == PAGE_ALL) {
				showChannelAll();
			}
		}
	};
	
	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshSearchBtn(newConfig.orientation);
	}
	
	//statistic
	private void uploadChannelStatistic() {
		if(mChannel != null) {
			BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
			statisticInfo.cateogry = mChannel.name;
			DKApi.getBannerList(mChannel.id, statisticInfo.formatToJson(), null);
		}
	}
}
