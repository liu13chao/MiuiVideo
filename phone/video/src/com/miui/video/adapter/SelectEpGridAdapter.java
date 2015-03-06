package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.widget.detail.ep.DetailEpItemBtn;
import com.miui.video.widget.detail.ep.SetInfoStatusEp;

public class SelectEpGridAdapter extends BaseGroupAdapter<SetInfoStatusEp> {

	public SelectEpGridAdapter(Context context) {
		super(context);
	}
	
	private class ViewHolder {
		DetailEpItemBtn itemBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			DetailEpItemBtn itemBtn = new DetailEpItemBtn(mContext);
			viewHolder = new ViewHolder();
			viewHolder.itemBtn = itemBtn;
			convertView = itemBtn;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		SetInfoStatusEp setInfoStatusEp = getItem(position);
		viewHolder.itemBtn.setData(setInfoStatusEp);
		return convertView;
	}

}
