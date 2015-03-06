package com.miui.video.adapter;

import android.widget.BaseExpandableListAdapter;

/**
 *@author tangfuling
 *
 */
public abstract class BaseClassifyAdapter extends BaseExpandableListAdapter {

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
