package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miui.video.R;

public class FilterItemAdapter extends BaseAdapter{

	Context mContext;
	ArrayList<String> mFilters = new ArrayList<String>();
	LayoutInflater inflater=null;
	public FilterItemAdapter(Context context){
		mContext = context;
		inflater = LayoutInflater.from(context);
	}
	
	public void setFilterItems(List<String> filters){
		mFilters.clear();
		mFilters.addAll(filters);
	}
	
	@Override
	public int getCount() {
		return mFilters.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder; 
		if(convertView == null){
			convertView = inflater.inflate(R.layout.filter_item_layout, null);
			holder = new Holder();
			holder.mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
			convertView.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();  
		}
		if(position < mFilters.size()){
			holder.mFiter.setText(mFilters.get(position));
		}else{
			Drawable filDrawble = mContext.getResources().getDrawable(R.drawable.detail_screening_icon);
			filDrawble.setBounds(0, 0, filDrawble.getMinimumWidth(), filDrawble.getMinimumHeight());
			holder.mFiter.setCompoundDrawables(filDrawble, null, null, null);
			holder.mFiter.setText(R.string.filter);
		}
		return convertView;
	}

	private class Holder{
		public TextView mFiter;
	}
	
}
