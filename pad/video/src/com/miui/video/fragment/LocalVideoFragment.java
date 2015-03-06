package com.miui.video.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.DeviceManager.DeviceObserver;
import com.miui.video.util.DKLog;
/**
 *@author dz
 *
 */

public class LocalVideoFragment extends Fragment {
	
	private final String TAG = LocalVideoFragment.class.getName();
	private final int MENU_ITEMS_MAX = 5;
	
	private WeakReference<Fragment>[] mChildFragments = new WeakReference[MENU_ITEMS_MAX];

	private View mContentView;
	private TextView mMenu[] = new TextView[MENU_ITEMS_MAX];
	private CharSequence mMenuText[] = new CharSequence[MENU_ITEMS_MAX];
	
	private LinearLayout menuListView;
	
	private Fragment mCurFragment;
	private int mCurSelect = 0;
	
	//data from net
	private DeviceManager mDeviceManager;
	private DLNAMediaManager mDLNAMediaManager;
	private List<Object> mAllDevices = new ArrayList<Object>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DKLog.i(TAG, "onCreateView");
		mContentView = inflater.inflate(R.layout.local_video, null);
		
		initDataSupply();
		intMenuList();
		switchFragment(0);
		
		return mContentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		DKLog.i(TAG, "onResume");
		getDevices();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDeviceManager.removeObserver(mDeviceObserver);
	}

	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshUI(true);
	}
	
	//init
	private void intMenuList() {
		menuListView = (LinearLayout) mContentView.findViewById(R.id.menu_list);
		
		mMenu[0] = (TextView) mContentView.findViewById(R.id.play_history);
		mMenu[1] = (TextView) mContentView.findViewById(R.id.my_favorite);
		mMenu[2] = (TextView) mContentView.findViewById(R.id.local_video);
		mMenu[3] = (TextView) mContentView.findViewById(R.id.offline_video);
		mMenu[4] = (TextView) mContentView.findViewById(R.id.shared_devices);
		for (int i=0; i<MENU_ITEMS_MAX; i++) {
			mMenu[i].setTag(i);
			mMenu[i].setOnClickListener(mOnClickListener);
			mMenuText[i] = mMenu[i].getText();
		}
		refreshUI(true);
		mMenu[0].setSelected(true);
		mCurSelect = 0;
	}
	
	private void initDataSupply() {
		mDeviceManager = DKApp.getSingleton(DeviceManager.class);
		mDeviceManager.addObserver(mDeviceObserver);
		mDLNAMediaManager = DKApp.getSingleton(DLNAMediaManager.class);
	}
	
	//get data
	private void getDevices() {
		mAllDevices.clear();
		List<BaseDevice> devices = mDeviceManager.getDevices();
		if(devices != null) {
			mAllDevices.addAll(devices);
		}
		refreshUI(false);
		
		mDeviceManager.scan();
	}
	
	//packaged method
	private void refreshUI(boolean isOrientationChanged) {
		if(mAllDevices.size() == 0) {
			mMenu[4].setVisibility(View.GONE);
		} else {
			mMenu[4].setVisibility(View.VISIBLE);
		}
		
		if (isOrientationChanged) {
			boolean isLand = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
			for (int i=0; i<MENU_ITEMS_MAX; i++) {
				if (isLand) {
					mMenu[i].setText(mMenuText[i]);
				} else {
					mMenu[i].setText("");
				}
			}
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					getResources().getDimensionPixelSize(R.dimen.local_video_menu_item_width), LayoutParams.MATCH_PARENT);
			layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.local_video_menu_margin_right);
			menuListView.setLayoutParams(layoutParams);
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v instanceof TextView) {
				int index = (Integer) v.getTag();
				switchFragment(index);
			}
		}
	};
		
	private void switchFragment(int index) {		
		if (index < 0 || index >= MENU_ITEMS_MAX)
			return;
		
		if (mChildFragments[index] == null || mChildFragments[index].get() == null) {
			switch(index) {
			case 0:
				mChildFragments[index] = new WeakReference<Fragment>(new PlayHistoryFragment());
				break;
			case 1:
				mChildFragments[index] = new WeakReference<Fragment>(new MyFavoriteFragment());
				break;
			case 2:
				mChildFragments[index] = new WeakReference<Fragment>(new LocalMediaFragment());
				break;
			case 3:
				mChildFragments[index] = new WeakReference<Fragment>(new OfflineMediaFragment());
				break;
			case 4:
				mChildFragments[index] = new WeakReference<Fragment>(new SharedDevicesFragment());
				break;
			default:
				return;
			}
		}
		
		mMenu[mCurSelect].setSelected(false);
		mMenu[index].setSelected(true);
		
		mCurSelect = index;
		mCurFragment = mChildFragments[mCurSelect].get();
		
		repalceFragment(mCurFragment);
	}
	
	@SuppressLint("NewApi")
	private void repalceFragment(Fragment fragment) {		
		FragmentManager manager = getChildFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (manager.findFragmentById(R.id.contents_view) == null) {
			ft.add(R.id.contents_view, fragment);			
		} else {
			ft.replace(R.id.contents_view, fragment);
		}
		ft.commitAllowingStateLoss();
		manager.executePendingTransactions();
	}
	
	//data callback
	private DeviceObserver mDeviceObserver = new DeviceObserver() {
		
		@Override
		public void onDeviceRemoved(BaseDevice device) {
			mAllDevices.clear();
			List<BaseDevice> devices = mDeviceManager.getDevices();
			if(devices != null) {
				mAllDevices.addAll(devices);
			}
			refreshUI(false);
		}
		
		@Override
		public void onDeviceAdded(BaseDevice device) {
			mDLNAMediaManager.browseDevice(device);
			
			mAllDevices.clear();
			List<BaseDevice> devices = mDeviceManager.getDevices();
			if(devices != null) {
				mAllDevices.addAll(devices);
			}
			refreshUI(false);
		}
	};
}
