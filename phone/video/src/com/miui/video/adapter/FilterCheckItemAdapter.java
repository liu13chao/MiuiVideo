package com.miui.video.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.ChannelFilterAdapter.FilterCheckListener;
import com.miui.video.type.Channel;

public class FilterCheckItemAdapter extends BaseAdapter{

	Context mContext;
	ArrayList<Item> mFilters = new ArrayList<Item>();
	LayoutInflater inflater=null;
	FilterCheckListener mFilterCheckListener = null;
	View mLastCheckedView;
	int mLastCheckedItem = -1;
	
	public FilterCheckItemAdapter(Context context){
		mContext = context;
		inflater = LayoutInflater.from(context);
	}
	
	public void setFilterItems(Channel[] channels){
		mFilters.clear();
		for(Channel c : channels){
			mFilters.add(new Item(c));
		}
	}
	
	@Override
	public int getCount() {
		return mFilters.size();
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
			convertView = inflater.inflate(R.layout.filter_check_item_layout, null);
			holder = new Holder();
			holder.mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
			holder.mIcon = (ImageView) convertView.findViewById(R.id.channel_filter_selected);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();  
		}
		if(mFilters.get(position).check){
			holder.mFiter.setTextColor(0xf95f22);
			holder.mFiter.setBackgroundResource(R.drawable.editable_title_com_btn_bg_s);
		}else{
			holder.mFiter.setTextColor(mContext.getResources().getColor(R.color.text_color_deep_dark));
			holder.mFiter.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView textV = ((Holder)v.getTag()).mFiter;
				ImageView imageV = ((Holder)v.getTag()).mIcon;
				mFilters.get(position).check = !mFilters.get(position).check;
				if(mFilters.get(position).check){
					if(mLastCheckedView != null){
						TextView textV2 = ((Holder)mLastCheckedView.getTag()).mFiter;
						ImageView imageV2 = ((Holder)mLastCheckedView.getTag()).mIcon;
						textV2.setTextColor(Color.BLACK);
						textV2.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
						imageV2.setVisibility(View.INVISIBLE);
					}
					if(mLastCheckedItem >= 0){
						mFilters.get(mLastCheckedItem).check = false;
					}
					if(mFilterCheckListener != null){
						if(mLastCheckedItem >= 0){
							mFilterCheckListener.check(false, mFilters.get(mLastCheckedItem).channel);
						}
						mFilterCheckListener.check(true, mFilters.get(position).channel);
					}
					textV.setTextColor(0xfff95f22);
					textV.setBackgroundResource(R.drawable.editable_title_com_btn_bg_s);
					imageV.setVisibility(View.VISIBLE);
					mLastCheckedView = v;
					mLastCheckedItem = position;
				}else{
					mLastCheckedView = null;
					mLastCheckedItem = -1;
					if(mFilterCheckListener != null){
						mFilterCheckListener.check(false, mFilters.get(position).channel);
					}
					textV.setTextColor(Color.BLACK);
					textV.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
					imageV.setVisibility(View.INVISIBLE);
				}
			}
		});
		holder.mFiter.setText(mFilters.get(position).channel.name);
		return convertView;
	}

	public void setFilterCheckListener(FilterCheckListener listener){
		mFilterCheckListener = listener;
	}
	
	private class Item{
		public Item(Channel channel){
			this.check = false;
			this.channel = channel;
		}
		public boolean check = false;
		public Channel channel;
	}
	
	private class Holder{
		public TextView mFiter;
		public ImageView mIcon;
	}
	
}
