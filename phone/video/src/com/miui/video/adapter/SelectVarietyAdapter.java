package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.widget.detail.ep.DetailEpItemVariety;
import com.miui.video.widget.detail.ep.SetInfoStatusVariety;

/**
 *@author tangfuling
 *
 */

public class SelectVarietyAdapter extends BaseGroupAdapter<SetInfoStatusVariety> {
	
	public SelectVarietyAdapter(Context context) {
		super(context);
	}
	
	private class ViewHolder {
		DetailEpItemVariety varietyItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(mContext, R.layout.select_variety_item, null);
			viewHolder.varietyItem = (DetailEpItemVariety) view.findViewById(R.id.select_variety_item_variety);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		SetInfoStatusVariety setInfoStatusVariety = getItem(position);
		if(setInfoStatusVariety != null) {
			viewHolder.varietyItem.setData(setInfoStatusVariety);
		}
		
		int size = getCount();
		if(size == 1) {
			convertView.setBackgroundResource(R.drawable.com_item_bg_full);
		} else {
			if(position == 0) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_up);
			} else if(position == size - 1) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_down);
			} else {
				convertView.setBackgroundResource(R.drawable.com_item_bg_mid);
			}
		}
		return convertView;
	}

}
