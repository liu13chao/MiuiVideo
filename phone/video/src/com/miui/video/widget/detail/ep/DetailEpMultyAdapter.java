package com.miui.video.widget.detail.ep;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.adapter.BaseGroupAdapter;

public class DetailEpMultyAdapter extends BaseGroupAdapter<SetInfoStatusEp> {

	private Context mContext;
	
	public DetailEpMultyAdapter(Context context) {
		super(context);
		this.mContext = context;
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
