package com.miui.video.info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.SearchActivity;
import com.miui.video.base.BaseFragment;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.LoadingFragment;
import com.miui.video.info.InfoChannelFragmentLoader.InfoChannelFragmentListener;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.util.FragmentUtils;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;

public class InfoChannelActivity extends BaseFragmentActivity {
	
	public static String KEY_CHANNEL = "key_channel";
	
	public static final String TAG = InfoChannelActivity.class.getName();
	
	//UI
	private View mTitleTop, mBtnSearch;
	private TextView mTitleName;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private BaseFragment[] mPages;
	private InfoChannelFragmentLoader mFragmentLoader;
	
	private LoadingFragment mLoadingFragment;
	
	//received data
	private Channel mChannel;
//	private int mChannelCategory = Channel.CHANNEL_TYPE_UNKOWN;
	
//	// Data Store
//	private RecommendationLoader mRecomendLoader;
//	private ChannelInfoStore mChannelStore;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_channel);
		init();
//		uploadChannelStatistic();
	}
	
	//init
	private void init() {
		initReceivedData();
		initUI();
		initData();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_CHANNEL);
		if(obj instanceof Channel) {
			mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
		}
	}
	
	private void initUI() {
		initTitleTop();
		initPagerView();
	}
	
	private void initData(){
	    if(mChannel == null){
	        return;
	    }
        Log.d(TAG, "initData.");
	    mFragmentLoader = new InfoChannelFragmentLoader(mChannel);
	    mFragmentLoader.setListener(mFragmentLoaderListener);
	    loadData();
	}
	
	private void loadData(){
	    if(mFragmentLoader != null){
	        mFragmentLoader.load();
	    }
	}
	
	private void initTitleTop() {
		mTitleTop = findViewById(R.id.title_top);
		mTitleName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(mOnClickListener);
		if(mChannel != null) {
		    mTitleName.setText(mChannel.name);
		}
		mBtnSearch = findViewById(R.id.home_search);
		mBtnSearch.setOnClickListener(mOnClickListener);
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.channel_pager_view);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
		
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	
		initLoadingViews();
	}
	
	private void initLoadingViews(){
        mLoadingFragment = new LoadingFragment();
        mLoadingFragment.setLoadingView(View.inflate(this, R.layout.load_view, null));
//        mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
//        TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
//        emptyTitle.setText(getResources().getString(R.string.error_empty_title));
//        ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
//        emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
        RetryView retryView = new RetryView(this);
        retryView.setOnRetryLoadListener(new OnRetryLoadListener() {
            @Override
            public void OnRetryLoad(View vClicked) {
                loadData();
            }
        });
        mLoadingFragment.setRetryView(retryView);
        if(mPages == null || mPages.length == 0){
            FragmentUtils.addFragment(this, R.id.root, mLoadingFragment);
            mLoadingFragment.moveToState(LoadingFragment.STATE_LOADING);
        }
	}
	
	private void initFragments(BaseFragment[] fragments,  CharSequence[] names){
	    mPages = fragments;
	    if(mPages != null) {
	        mPagerView.setTitle(names);
	        mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	        mViewPagerAdapter.setPages(mPages);
	        mPagerView.setOffscreenPageLimit(mPages.length);
	    }
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == mTitleTop) {
				finish();
			} else if(v == mBtnSearch) {
				Intent intent = new Intent(InfoChannelActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int page) {
			mPages[page].onSelected();
		}
	};
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFragmentLoader != null){
            mFragmentLoader.release();
        }
    }
    
    private InfoChannelFragmentListener mFragmentLoaderListener = new InfoChannelFragmentListener() {
        @Override
        public void onFragmentLoad(BaseFragment[] fragments,
                CharSequence[] names) {
            if(fragments != null && fragments.length > 0){
                FragmentUtils.removeFragment(InfoChannelActivity.this, mLoadingFragment);
                initFragments(fragments, names); 
            }else{
                if(mLoadingFragment != null){
                    mLoadingFragment.moveToState(LoadingFragment.STATE_RETRY);
                }
            }
        }
    };
    
//	//statistic
//	private void uploadChannelStatistic() {
//		if(mChannel != null) {
//			BannerListStatisticInfo statisticInfo = new BannerListStatisticInfo();
//			statisticInfo.cateogry = mChannel.name;
//			DKApi.getBannerList(mChannel.id, statisticInfo.formatToJson(), null);
//		}
//	}
    
    
}
