package com.miui.videoplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;

public class AirkanDevicesAdapter extends BaseGroupAdapter<String> {

	private String playingDeviceName;
	
	public AirkanDevicesAdapter(Context context) {
		super(context);
	}
	
	public void setPlayingDeviceName(String deviceName) {
		this.playingDeviceName = deviceName;
	}

	private class ViewHolder {
		private TextView  itemTv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.vp_popup_airkan_device_item, null);
			vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_popup_device_name);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		String item = getItem(position);
		if(item != null) {
			vHolder.itemTv.setText(item);
			if(item.equals(playingDeviceName)) {
				vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_select_color));
			} else {
				vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_white));
			}
		}
		return convertView;
	}

}
