package com.miui.videoplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;

public class ScaleScreenAdapter extends BaseGroupAdapter<String> {

	private int selectedIndex;
	
	public ScaleScreenAdapter(Context context) {
		super(context);
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	private class ViewHolder {
		private TextView  itemTv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.vp_popup_scale_screen_item, null);
			vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_scale_screen_item);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		String item = getItem(position);
		if(item != null) {
			vHolder.itemTv.setText(item);
		}
		if(position == selectedIndex) {
			vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_blue));
		} else {
			vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_white));
		}
		return convertView;
	}
}
