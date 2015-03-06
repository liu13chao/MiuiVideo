package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.widget.statusbtn.StatusBtnItem;
import com.miui.video.widget.statusbtn.StatusBtnItemList;
import com.miui.video.widget.statusbtn.StatusBtnLong;

/**
 *@author tangfuling
 *
 */

public class VarietyAdapter extends BaseGroupAdapter<MediaSetInfo> {
	
	public VarietyAdapter(Context context) {
		super(context);
	}
	
	private StatusBtnItemList mStatusBtnItemList;
	
	public void setData(StatusBtnItemList statusBtnItemList) {
		this.mStatusBtnItemList = statusBtnItemList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(mStatusBtnItemList == null) {
			return 0;
		}
		return mStatusBtnItemList.size();
	}
	
	private class ViewHolder {
		StatusBtnLong view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			StatusBtnLong view = new StatusBtnLong(mContext);
			viewHolder.view = view;
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(mStatusBtnItemList != null) {
			int curIndex = mStatusBtnItemList.size() - position - 1;
			if(curIndex >= 0) {
				StatusBtnItem statusBtnItem = mStatusBtnItemList.get(curIndex);
				viewHolder.view.setStatusBtnItem(statusBtnItem);
			}
		}
		return convertView;
	}

}
