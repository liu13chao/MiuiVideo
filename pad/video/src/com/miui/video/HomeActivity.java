package com.miui.video;

import miui.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.LocalVideoFragment;
import com.miui.video.fragment.OnlineVideoFragment;
import com.miui.video.mipush.MiPushAdsProcess;
import com.miui.video.mipush.MiPushMediaProcess;
import com.miui.video.model.AuthenticateAccountManager;
import com.miui.video.model.loader.BootInfoLoader;
import com.miui.video.tv.TvEpgManager;
import com.miui.video.util.DKLog;
import com.miui.video.widget.bg.LocalBg;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;
import com.miui.video.widget.searchbox.SearchBox;
import com.miui.video.widget.searchbox.SearchHintPopWindow;
import com.miui.video.widget.searchbox.SearchHintPopWindow.OnPerformSearchListener;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 *@author tangfuling
 *
 */

public class HomeActivity extends BaseFragmentActivity {
	
	private static final String TAG = HomeActivity.class.getName();
	
	public final static String KEY_FROMNOTIFICATION = "fromNotification";
	
	//UI
	private OnlineVideoFragment mOnlineVideoFragment;
	private LocalVideoFragment mLocalVideoFragment;
	private Button mOnlineVideoBtn;
	private Button mLocalVideoBtn;
	private View mOnlineTabBg;
	private View mLocalTabBg;
	private View mOnlineBg;
	private View mLocalBg;
	private ViewGroup mDecorView;
	
	private SearchBox mSearchBox;
	private SearchHintPopWindow mSearchHintView;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private Fragment[] mPages;
	
	private int PAGE_ONLINE = 0;
	private int PAGE_LOCAL = 1;
	
	
	private boolean mCanAccessNet = true;
	
	//data supply
	private BootInfoLoader mBootInfoLoader;
	
	//flag
	private boolean mIsBoxChecked = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		init();
		
		initDeclaration();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(mAuthenticateAccountManager.isNoAccount()) {
			return;
		}
		if(mCanAccessNet) {
			showLoginTip();
		}
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
	
	public void hideTitleTop() {
		mOnlineVideoBtn.setVisibility(View.INVISIBLE);
		mLocalVideoBtn.setVisibility(View.INVISIBLE);
		mOnlineTabBg.setVisibility(View.INVISIBLE);
		mLocalTabBg.setVisibility(View.INVISIBLE);
		mSearchBox.setVisibility(View.INVISIBLE);
		
		mPagerView.disableTouchInterceptor(true);
	}
	
	public void showTitleTop() {
		mOnlineVideoBtn.setVisibility(View.VISIBLE);
		mLocalVideoBtn.setVisibility(View.VISIBLE);
		mOnlineTabBg.setVisibility(View.VISIBLE);
		mLocalTabBg.setVisibility(View.VISIBLE);
		mSearchBox.setVisibility(View.VISIBLE);
		
		mPagerView.disableTouchInterceptor(false);
		if(mPagerView.getCurPage() == PAGE_LOCAL) {
			showLocalVideo();
		} else if(mPagerView.getCurPage() == PAGE_ONLINE) {
			showOnlineVideo();
		}
	}
	//init
	private void init() {
		registerMiPushAdsProcess();
		clearNotificationCount();
		
		initFragment();
		initUI();
	}
	
	private void registerMiPushAdsProcess() {
		if(!DKApp.isMipushRegistered) {
			DKApp.getSingleton(MiPushAdsProcess.class).registerMiPushEnv();
	        MiPushClient.registerPush(DKApp.getAppContext(), DKApp.APP_ID, DKApp.APP_KEY);
	        DKApp.isMipushRegistered = true;
		}
	}
	
	private void initUI() {
		initDecorView();
		initTab();
		initPagerView();
		showOnlineVideo();
	}
	
	private void initDecorView() {
		mDecorView = (ViewGroup) getWindow().getDecorView();
		int tabBgWidth = getResources().getDimensionPixelSize(R.dimen.home_tab_bg_width);
		int tabBgHeight = getResources().getDimensionPixelSize(R.dimen.home_tab_bg_height);
		int onlineTabBgLeftMargin = getResources().getDimensionPixelSize(R.dimen.home_tab_bg_online_left_margin);
		int localTabBgLeftMargin = getResources().getDimensionPixelSize(R.dimen.home_tab_bg_local_left_margin);
		
		mOnlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mDecorView.addView(mOnlineBg, 0, onlineBgParams);
		
		mLocalBg = new LocalBg(this);
		LayoutParams localBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mDecorView.addView(mLocalBg, 1, localBgParams);
		
		mOnlineTabBg = new View(this);
		mOnlineTabBg.setBackgroundResource(R.drawable.tab_bg_pressed);
		LayoutParams onlineTabBgParams = new LayoutParams(tabBgWidth, tabBgHeight);
		onlineTabBgParams.leftMargin = onlineTabBgLeftMargin;
		mDecorView.addView(mOnlineTabBg, 2, onlineTabBgParams);
		
		mLocalTabBg = new View(this);
		mLocalTabBg.setBackgroundResource(R.drawable.tab_bg_normal);
		LayoutParams localTabBgParams = new LayoutParams(tabBgWidth, tabBgHeight);
		localTabBgParams.leftMargin = localTabBgLeftMargin;
		mDecorView.addView(mLocalTabBg, 3, localTabBgParams);
	}
	
	private void initTab() {
		initSearchBox();
		mOnlineVideoBtn = (Button) findViewById(R.id.home_online_video);
		mLocalVideoBtn = (Button) findViewById(R.id.home_local_video);
		mOnlineVideoBtn.setOnClickListener(mOnClickListener);
		mLocalVideoBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.home_pager_view);
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mPages = new Fragment[2];
		mPages[0] = mOnlineVideoFragment;
		mPages[1] = mLocalVideoFragment;
		
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
		mPagerView.setOnPageChangedListener(mOnPageChangeListener);
	}
	
	private void initFragment() {
		mOnlineVideoFragment = new OnlineVideoFragment();
		mLocalVideoFragment = new LocalVideoFragment();
	}
	
	private void initSearchBox() {
		mSearchBox = (SearchBox) findViewById(R.id.home_search_box);
		if(mSearchHintView == null) {
			mSearchHintView = new SearchHintPopWindow(this, mSearchBox);
			mSearchHintView.addListener(mOnPerformSearchListener);
		}
	}
	
	private void initDeclaration() {
		boolean alertNetwork = DKApp.shouldAlertNetwork();
		if (alertNetwork) {
			disableAccessNet();
			showDeclaration();
		} else {
			uploadBootInfo();
			showLoginTip();
		}
	}
	
	private void clearNotificationCount() {
		Intent intent = getIntent();
		boolean bFromNotification = intent.getBooleanExtra(KEY_FROMNOTIFICATION, false);
		if(bFromNotification) {
			DKApp.getSingleton(MiPushMediaProcess.class).clearCurCount();
		}
	}
	
	//packaged method
	private void disableAccessNet() {
		mCanAccessNet = false;
		
		mOnlineVideoFragment.disableAccessNet();
		DKApp.getSingleton(TvEpgManager.class).disableAccessNet();
	}
	
	public void enableAccessNet() {
		mCanAccessNet = true;
		
		mOnlineVideoFragment.enableAccessNet();
		DKApp.getSingleton(TvEpgManager.class).enableAccessNet();
	}
	
	private void showOnlineVideo() {
		mOnlineBg.setVisibility(View.VISIBLE);
		mLocalBg.setVisibility(View.INVISIBLE);
		setOnlineVideoBtnSelected();
	}
	
	private void showLocalVideo() {
		mLocalBg.setVisibility(View.VISIBLE);
		mOnlineBg.setVisibility(View.INVISIBLE);
		setLocalVideoBtnSelected();
	}
	
	private void setOnlineVideoBtnSelected() {
		mOnlineVideoBtn.setSelected(true);
		mOnlineTabBg.setVisibility(View.VISIBLE);
		mLocalVideoBtn.setSelected(false);
		mLocalTabBg.setVisibility(View.INVISIBLE);
	}
	
	private void setLocalVideoBtnSelected() {
		mOnlineVideoBtn.setSelected(false);
		mOnlineTabBg.setVisibility(View.INVISIBLE);
		mLocalVideoBtn.setSelected(true);
		mLocalTabBg.setVisibility(View.VISIBLE);
	}
	
	private void showDeclaration() {		
		View contentView = View.inflate(HomeActivity.this, R.layout.declaration_view, null);
		TextView declarationView = (TextView) contentView.findViewById(R.id.declaration_content);
		declarationView.setMovementMethod(LinkMovementMethod.getInstance());
		
		CheckBox checkBox = (CheckBox) contentView.findViewById(R.id.no_longer_tips);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mIsBoxChecked = isChecked;
			}
		});
		
		final AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle(R.string.declaration)
        .setView(contentView)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				DKApp.setAlertNetworkPrefs(true);
				HomeActivity.this.finish();
			}
		 } )
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				DKApp.setAlertNetworkPrefs(!mIsBoxChecked);
				enableAccessNet();
				uploadBootInfo();
				showLoginTip();
			}
        })
        .create();
		dialog.setCancelable(false);
		
		try {
			dialog.show();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}
	
	private void showLoginTip() {
		if(mAuthenticateAccountManager.needAuthenticate()) {
			if(mAuthenticateAccountManager.isNoAccount()) {
				AlertDialog loginTipDlg = new AlertDialog.Builder(this)
		        .setCancelable(true).setTitle(R.string.login_tip_title)
		        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){ 	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				 } )
		        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mAuthenticateAccountManager.authAccount();
					}
		        })
		        .setMessage(R.string.login_tip_content)
		        .create();
				try {
					loginTipDlg.show();
				} catch(Exception e) {
					DKLog.d(TAG, e.getLocalizedMessage());
				}
			} else {
				mAuthenticateAccountManager.authAccount();
			}
		}
	}

	//UI callback
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && mPagerView.getCurPage() == PAGE_LOCAL) {
			mPagerView.setCurPage(PAGE_ONLINE);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mOnlineVideoBtn.getId()) {
				mPagerView.setCurPage(PAGE_ONLINE);
			} else if(id == mLocalVideoBtn.getId()) {
				mPagerView.setCurPage(PAGE_LOCAL);
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
			intent.setClass(HomeActivity.this, SearchResultActivity.class);
			startActivity(intent);
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			if(page == PAGE_ONLINE) {
				showOnlineVideo();
			} else if(page == PAGE_LOCAL) {
				showLocalVideo();
			}
		}
	};
	
	//auth callback
	private AuthenticateAccountManager mAuthenticateAccountManager = new AuthenticateAccountManager(this) {
		
		@Override
		protected void onAuthSuccess() {
			
		}
		
		@Override
		protected void onAuthNoAccount() {

		}
		
		@Override
		protected void onAuthFailed(String failedReason) {
			
		}
	};
	
	//statistic
	private void uploadBootInfo() {
		if(mBootInfoLoader == null) {
			mBootInfoLoader = new BootInfoLoader();
		}
		mBootInfoLoader.refreshBootResponseInfo();
	}
}