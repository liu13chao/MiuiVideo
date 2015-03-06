package com.miui.videoplayer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;
import com.miui.videoplayer.widget.SwitchButton;

public class SettingAdapter extends BaseGroupAdapter<String> {

	private Drawable[] mDrawables;
	private boolean[] mSelected;
	
	public SettingAdapter(Context context) {
		super(context);
	}
	
	private class ViewHolder {
		private TextView  itemTv;
		private SwitchButton slidingBtn;
	}

	public void setGroup(String[] names, Drawable[] drawables, boolean[] selected){
		super.setGroup(names);
		mSelected = selected;
		mDrawables = drawables;
		for(Drawable drawable : mDrawables){
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.vp_popup_setting_item, null);
			vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_popup_setting_item);
			SwitchButton sb = new SwitchButton(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_VERTICAL;
			vHolder.slidingBtn = sb;
			((ViewGroup)convertView).addView(sb, lp);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		String item = getItem(position);
		if(item != null) {
			vHolder.itemTv.setText(item);
			vHolder.itemTv.setCompoundDrawables(mDrawables[position], null, null, null);
			vHolder.slidingBtn.setId(position);
			vHolder.slidingBtn.setOnPerformCheckedChangeListener(mListener);
			vHolder.slidingBtn.setChecked(mSelected[position]);
		}
		return convertView;
	}
	
	private OnCheckedChangeListener mListener = null;
	
	public void setOnPerformCheckedChangelistener(OnCheckedChangeListener listener){
		mListener = listener;
	}
	
}
