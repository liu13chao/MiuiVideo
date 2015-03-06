/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ViewPagerAdapter.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-17 
 */
package com.miui.video.widget.pager;

import java.util.ArrayList;
import java.util.List;

import miui.view.PagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author tianli
 * 
 */

public class ViewFragmentPagerAdapter extends PagerAdapter {

	private FragmentManager mFragmentManager;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	
	public ViewFragmentPagerAdapter(FragmentManager fragmentManager) {
		this.mFragmentManager = fragmentManager;
	}
	
	public void refresh() {
		notifyDataSetChanged();
	}
	
	public void setPages(List<Fragment> pages) {
		this.mFragments.clear();
		this.mFragments.addAll(pages);
		refresh();
	}

	public void setPages(Fragment[] pages) {
		this.mFragments.clear();
		for (int i = 0; i < pages.length; i++) {
			this.mFragments.add(pages[i]);
		}
		refresh();
	}
	
	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mFragments.get(position).getView());
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = mFragments.get(position);
		if(!fragment.isAdded()) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getName());
			ft.commitAllowingStateLoss();
			mFragmentManager.executePendingTransactions();
		}
		
		if(fragment.getView().getParent() == null) {
			container.addView(fragment.getView());
		}
		return fragment.getView();
	}
	
	@Override
	public boolean hasActionMenu(int arg0) {
		return false;
	}
}