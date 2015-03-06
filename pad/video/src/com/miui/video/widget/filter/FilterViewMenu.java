package com.miui.video.widget.filter;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.widget.filter.FilterViewFilter.ButtonTag;

/**
 *@author tangfuling
 *
 */

public class FilterViewMenu extends LinearLayout {
	
	private Context mContext;
	private int mMenuItemHeight;
	private int mMenuItemInterval;
	
	private ArrayList<FilterViewMenuItem> mMenuItems = new ArrayList<FilterViewMenuItem>();
	
	public FilterViewMenu(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public FilterViewMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public FilterViewMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}
	
	public void addMenuItem(ButtonTag tag) {
		if(tag == null) {
			return;
		}
		FilterViewMenuItem menuItem = getMenuItemByParentId(tag.parentChannelId);
		if(menuItem == null) {
			menuItem = new FilterViewMenuItem(mContext);
			menuItem.setData(tag);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.height = mMenuItemHeight;
			if(mMenuItems.size() != 0) {
				params.leftMargin = mMenuItemInterval;
			}
			addView(menuItem, params);
			mMenuItems.add(menuItem);
		} else {
			menuItem.setData(tag);
		}
	}
	
	public void removeMenuItem(ButtonTag tag) {
		if(tag == null) {
			return;
		}
		FilterViewMenuItem menuItem = getMenuItemByParentId(tag.parentChannelId);
		if(menuItem != null) {
			mMenuItems.remove(menuItem);
			removeView(menuItem);
		}
	}
	
	//init
	private void init() {
		mMenuItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.filter_view_menu_item_height);
		mMenuItemInterval = mContext.getResources().getDimensionPixelSize(R.dimen.filter_view_menu_item_interval);
		setOrientation(HORIZONTAL);
	}
	
	private FilterViewMenuItem getMenuItemByParentId(int parentId) {
		for(int i = 0; i < mMenuItems.size(); i++) {
			FilterViewMenuItem menuItem = mMenuItems.get(i);
			if(menuItem != null) {
				ButtonTag tag = menuItem.getData();
				if(tag != null && parentId == tag.parentChannelId) {
					return menuItem;
				}
			}
		}
		return null;
	}
}
