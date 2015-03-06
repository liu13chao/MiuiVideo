package com.miui.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.miui.video.api.DKApi;
import com.miui.video.appstore.AppStoreConstants;
import com.miui.video.appstore.UpdateManager;
import com.miui.video.base.BaseFragment;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.CategoryFragment;
import com.miui.video.fragment.FeatureListFragment;
import com.miui.video.fragment.MyVideoFragment;
import com.miui.video.fragment.OnlineVideoFragment;
import com.miui.video.helper.OnTouchInterceptor;
import com.miui.video.mipush.MiPushManager;
import com.miui.video.model.AppConfig;
import com.miui.video.model.loader.BootInfoLoader;
import com.miui.video.model.loader.BootInfoLoader.OnDownloadSoInBootListener;
import com.miui.video.response.GetUpdateApkResponse;
import com.miui.video.type.AppStoreApkInfo;
import com.miui.video.util.DKLog;
import com.miui.video.widget.QuickEntryView;
import com.miui.video.widget.QuickEntryView.OnQuickEntryItemClickListener;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.download.SourceManager.OnDownloadSoListener;
import com.miui.videoplayer.model.MediaConfig;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;
import miui.app.AlertDialog;

/**
 *@author tangfuling
 *
 */

public class MainActivity extends BaseFragmentActivity {
	
	private static final String TAG = MainActivity.class.getName(); 
	
	//UI
	private OnlineVideoFragment mOnlineVideoFragment;
	private CategoryFragment mCategoryFragment;
	private FeatureListFragment mFeatureListFragment;
	private MyVideoFragment mMyVideoFragment;
	private View mBtnSearch;
	private View mBtnMenu;
	
//	private MenuPopupWindow mMenuPopupWindow;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private BaseFragment[] mPages;
	
//	private BannerView mBannerView;
//	private int mBannerViewTopMargin;
	
	private int PAGE_ONLINE_VIDEO = 0;
	private int PAGE_CATEGORY = 1;
	
	//data supply
	private BootInfoLoader mBootInfoLoader;
	private ServiceRequest getUpdateApkInfoRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();	
		if(getUpdateApkInfoRequest != null){
			getUpdateApkInfoRequest.setShowResultDesc(false);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void init() {
		initFragment();
		initUI();
		doStartup();
	}
	
	private void doStartup(){
        uploadBootInfo();
        GetUpdateApkInfo();
        DKApp.getSingleton(MiPushManager.class).registerMiPushEnv();
	}
	
	private void initUI() {
		initTab();
		initPagerView();
	}
	
	private void initTab() {
		mBtnSearch = findViewById(R.id.home_search);
		mBtnMenu = findViewById(R.id.home_menu);
		mBtnSearch.setOnClickListener(mOnClickListener);
		mBtnMenu.setOnClickListener(mOnClickListener);
	}
	
	private void initOnlinePagerView() {
	    String[] titles = new String[3];
	    titles[0] = getResources().getString(R.string.hot);
	    titles[1] = getResources().getString(R.string.category);
	    titles[2] = getResources().getString(R.string.choice);
	    mPagerView.setTitleWithPadding(titles, getResources().getDimensionPixelSize(R.dimen.
	            home_pager_title_padding));

	    mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
	    mPages = new BaseFragment[3];
	    mPages[0] = mOnlineVideoFragment;
	    mPages[1] = mCategoryFragment;
	    mPages[2] = mFeatureListFragment;

	    mViewPagerAdapter.setPages(mPages);
	    
	    mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	    mPagerView.setOnPageChangedListener(mOnPageChangeListener);
	    mPagerView.getPager().setOnTouchInterceptor(mOnTouchInterceptor);
//	    mPagerView.setOffscreenPageLimit(mpa);
	}

	private void initCmccPagerView() {
	    String[] titles = new String[1];
	    titles[0] = getResources().getString(R.string.video);
	    mPagerView.setTitle(titles);

	    mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
	    mPages = new BaseFragment[1];
	    mPages[0] = mMyVideoFragment;

	    mViewPagerAdapter.setPages(mPages);
	    mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	    
	    if(mBtnSearch != null){
	        mBtnSearch.setVisibility(View.INVISIBLE);
	    }
	    if(mBtnMenu != null){
	        mBtnMenu.setVisibility(View.INVISIBLE);
	    }
	}
	
	private void initPagerView() {
	    mPagerView = (PagerView) findViewById(R.id.home_pager_view);
//	    int pagerTitleIntervalH = (int) getResources().getDimension(R.dimen.video_common_interval_45);
//	    mPagerView.setPagerTitleIntervalH(pagerTitleIntervalH);
	    if(DKApp.getSingleton(AppConfig.class).isOnlineVideoOn()){
	        initOnlinePagerView();
	    }else{
	        initCmccPagerView();
	    }
	}
	
	private void initFragment() {
		mOnlineVideoFragment = new OnlineVideoFragment();
		mOnlineVideoFragment.setQuickEntryItemClickListener(mQuickEntryItemClickListener);
		mCategoryFragment = new CategoryFragment();
		mFeatureListFragment = new FeatureListFragment();
		mMyVideoFragment = new MyVideoFragment();
	}
	
	private void startSearchActivity() {
		Intent intent = new Intent();
		intent.setClass(this, SearchActivity.class);
		startActivity(intent);
	}
	
	private void startMyActivity() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }
	
	//UI callback
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && mPagerView != null && mPagerView.getCurPage() != PAGE_ONLINE_VIDEO) {
			mPagerView.setCurPage(PAGE_ONLINE_VIDEO);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == mBtnSearch) {
				startSearchActivity();
			} else if(v == mBtnMenu) {
			    startMyActivity();
			}
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		public void onPageSelected(int page) {
			mPages[page].onSelected();
			if(page == PAGE_ONLINE_VIDEO) {
			    mOnlineVideoFragment.startBannerIndicator();
			} else {
	             mOnlineVideoFragment.stopBannerIndicator();
			}
		};
	};
	
	private OnTouchInterceptor mOnTouchInterceptor = new OnTouchInterceptor() {
		@Override
		public boolean onIntercept(int scrollDirection, MotionEvent event) {
			if ((scrollDirection == OnTouchInterceptor.SCROLL_LEFT || 
			        scrollDirection == OnTouchInterceptor.SCROLL_RIGHT)
			            && mOnlineVideoFragment.isHitBannerView(event)) {
			        return true;
			}
			return false;
		}

		@Override
		public void onPreIntercept(MotionEvent event) {
		}
	};
	
	private OnQuickEntryItemClickListener mQuickEntryItemClickListener = new OnQuickEntryItemClickListener() {
		@Override
		public void onQuickEntryItemClick(int position) {
			if(position == QuickEntryView.POSITION_ALL) {
				mPagerView.setCurPage(PAGE_CATEGORY);
			}
		}
	};
	
	//statistic
	private void uploadBootInfo() {
		if(mBootInfoLoader == null) {
			mBootInfoLoader = new BootInfoLoader();
		}
		mBootInfoLoader.setDownloadSoListener(downloadListener);
		mBootInfoLoader.refreshBootResponseInfo();
	}
	private OnDownloadSoInBootListener downloadListener = new OnDownloadSoInBootListener() {
		@Override
		public void downloadSohuSo() {
			DKLog.d(TAG, "downloadSohuLib() ........");
			DKApp.getSingleton(SourceManager.class).downloadSo(MediaConfig.MEDIASOURCE_SOHU_TYPE_CODE, new OnDownloadSoListener() {
				@Override
				public void onSoReady(String path) {
				}
				@Override
				public void onSoNotReady() {
				}
				@Override
				public void onSoDownloadStart() {
					DKApp.getSingleton(SourceManager.class).downloadSoWithoutLoading();
				}
				@Override
				public void onSoDownloadProgress(int completed, int total) {
				}
				@Override
				public void onSoDownloadError(int error) {
				}
				@Override
				public void onSoDownloadComplete() {					
				}
			});				
		}
	};
	
	private void GetUpdateApkInfo(){
		DKLog.d(TAG, "enter in GetUpdateApkInfo()");
		if(UpdateManager.getInstance().isUpdateApkInfoExpired()){
			DKLog.d(TAG, "getUpdateApkInfoRequest .........");
			getUpdateApkInfoRequest = DKApi.GetUpdateApkInfo(AppStoreConstants.VERSION_MIUI, String.valueOf(DKApp.getSingleton(AppConfig.class).getVersionCode()), mUpdateApkInfoObserver);
		}
	}

	
	private void UpdateApk(AppStoreApkInfo info){
		DKLog.d(TAG, "UpdateApk() ........");
		if(AndroidUtils.isWifiConnected(this) && UpdateManager.getInstance().isShowUpdateDialog(this, info)){
			UpdateManager.getInstance().saveUpdateApkTime();
			AlertDialog dialog = UpdateManager.getInstance().createUpdateApkDialog(this, info);
			dialog.show();
		}		
	}
	
	private Observer mUpdateApkInfoObserver = new Observer() {
		
		@Override
		public void onRequestCompleted(ServiceRequest request,
				ServiceResponse response) {
				if (response.isSuccessful()) {
					DKLog.d(TAG, "GetUpdateApkResponse");
					GetUpdateApkResponse myResponse = (GetUpdateApkResponse)response;
					AppStoreApkInfo data = myResponse.data;
					UpdateApk(data);
			}
		}
		
		@Override
		public void onProgressUpdate(ServiceRequest request, int progress) {
		}
	};
}
