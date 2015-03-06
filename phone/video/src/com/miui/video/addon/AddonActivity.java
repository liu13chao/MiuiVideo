package com.miui.video.addon;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.AddonAllFragment;
import com.miui.video.fragment.AddonInstalledFragment;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;

public class AddonActivity extends BaseFragmentActivity {

	//UI
	private View mTitleTop;
	private TextView mTitleName;
	
	private AddonAllFragment mAddonAllFragment;
	private AddonInstalledFragment mAddonInstalledFragment;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private Fragment[] mPages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addon);
		init();
	}
	
	//init
	private void init() {
		initFragment();
		initUI();
	}
	
	private void initFragment() {
		mAddonAllFragment = new AddonAllFragment();
		mAddonInstalledFragment = new AddonInstalledFragment();
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
		mTitleTop = findViewById(R.id.title_top);
		mTitleName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(mOnClickListener);
		mTitleName.setText(R.string.addon);
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.addon_pager_view);
//		int pagerTitleIntervalH = (int) getResources().getDimension(R.dimen.video_common_interval_40);
//		mPagerView.setPagerTitleIntervalH(pagerTitleIntervalH);
		
		String[] titles = new String[2];
		titles[0] = getResources().getString(R.string.addon_all);
		titles[1] = getResources().getString(R.string.installed);
		mPagerView.setTitle(titles);
		
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		
		mPages = new Fragment[2];
		mPages[0] = mAddonAllFragment;
		mPages[1] = mAddonInstalledFragment;
		
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setOffscreenPageLimit(2);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTitleTop) {
				AddonActivity.this.finish();
			}
		}
	};
}
