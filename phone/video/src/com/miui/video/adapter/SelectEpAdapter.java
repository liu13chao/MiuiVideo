package com.miui.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.widget.detail.ep.SetInfoStatusEp;

/**
 *@author tangfuling
 *
 */

public class SelectEpAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	
	private List<String> mGroupData = null;
	private List<List<SetInfoStatusEp>> mChildData = null;
	
	private OnItemClickListener mOnItemClickListener;
	
	public SelectEpAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setData(List<String> groupData, List<List<SetInfoStatusEp>> childData) {
		this.mGroupData = groupData;
		this.mChildData = childData;
		notifyDataSetChanged();
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
	//group
	@Override
	public int getGroupCount() {
		if(mGroupData != null) {
			return mGroupData.size();
		}
		return 0;
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		if(mGroupData != null && groupPosition < mGroupData.size()) {
			return mGroupData.get(groupPosition);
		}
		return null;
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	private class ViewHolderGroup {
		View container;
		TextView textView;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolderGroup viewHolderGroup = null;
		if(convertView == null) {
			View view = View.inflate(mContext, R.layout.select_ep_group, null);
			viewHolderGroup = new ViewHolderGroup();
			viewHolderGroup.container = view.findViewById(R.id.select_ep_group_container);
			viewHolderGroup.textView = (TextView) view.findViewById(R.id.select_ep_group_title);
			convertView = view;
			convertView.setTag(viewHolderGroup);
		} else {
			viewHolderGroup = (ViewHolderGroup) convertView.getTag();
		}
		
		String title = mGroupData.get(groupPosition);
		viewHolderGroup.textView.setText(title);
		
		if(!isExpanded) {
			viewHolderGroup.container.setBackgroundResource(R.drawable.com_bg_white_corner);
		} else {
			viewHolderGroup.container.setBackgroundResource(R.drawable.com_bg_white_corner_t_n);
		}
		return convertView;
	}

	//child
	@Override
	public int getChildrenCount(int groupPosition) {
		if(mChildData != null && groupPosition < mChildData.size()) {
			List<SetInfoStatusEp> setInfoStatusEps = mChildData.get(groupPosition);
			if(setInfoStatusEps != null && setInfoStatusEps.size() > 0) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if(mChildData != null && groupPosition < mChildData.size()) {
			List<SetInfoStatusEp> list = mChildData.get(groupPosition);
			if(list != null && childPosition < list.size()) {
				return list.get(childPosition);
			}
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	private class ViewHolderChild {
		GridView gridView;
		SelectEpGridAdapter adapter;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolderChild viewHolderChild = null;
		if(convertView == null) {
			View view = View.inflate(mContext, R.layout.select_ep_child, null);
			viewHolderChild = new ViewHolderChild();
			viewHolderChild.gridView = (GridView) view.findViewById(R.id.select_ep_child_grid);
			viewHolderChild.adapter = new SelectEpGridAdapter(mContext);
			viewHolderChild.gridView.setAdapter(viewHolderChild.adapter);
			viewHolderChild.gridView.setOnItemClickListener(mOnItemClickListener);
			convertView = view;
			convertView.setTag(viewHolderChild);
		} else {
			viewHolderChild = (ViewHolderChild) convertView.getTag();
		}
		
		List<SetInfoStatusEp> setInfoStatusEps = mChildData.get(groupPosition);
		viewHolderChild.adapter.setGroup(setInfoStatusEps);
		
		convertView.setBackgroundResource(R.drawable.com_bg_white_corner_d_n);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}
