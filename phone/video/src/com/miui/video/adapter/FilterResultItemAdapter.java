package com.miui.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miui.video.ChannelFilterResultActivity.OnSelectedChannelChangedLinstener;
import com.miui.video.R;

public class FilterResultItemAdapter extends BaseAdapter{
	
	Context mContext;
	String[] mFilters;
	LayoutInflater inflater=null;
	OnSelectedChannelChangedLinstener mChannelListener = null;
	
	public FilterResultItemAdapter(Context context){
		mContext = context;
		inflater = LayoutInflater.from(context);
	}
	
	public void setFilterItems(String[] filters){
		mFilters = filters;
	}
	
	@Override
	public int getCount() {
		return mFilters.length;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder; 
		if(convertView == null){
			convertView = inflater.inflate(R.layout.filter_result_item_layout, null);
			holder = new Holder();
			holder.mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();  
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mChannelListener != null){
					mChannelListener.onChannelRemoved(position);
				}
			}
		});
		holder.mFiter.setText(mFilters[position]);
		return convertView;
	}

	public void setSelectedChannelChangeListener(OnSelectedChannelChangedLinstener listener){
		mChannelListener = listener;
	}
	
	private class Holder{
		public TextView mFiter;
	}
	
}
