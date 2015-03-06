package com.miui.video.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.Channel;

public class ChannelFilterAdapter extends BaseAdapter{

	Channel mChannel;
	Context mContext;
	ArrayList<Channel> mIDs = new ArrayList<Channel>();
	
	public ChannelFilterAdapter(Context context, Channel channel) {
		mContext = context;
		mChannel = new Channel();
		mChannel.name = channel.name;
		mChannel.id = channel.id;
		mChannel.type = channel.type;
		mChannel.channeltype = channel.channeltype;
		if(channel.subfilter != null && channel.subfilter.length > 0){
			int subCounter = 0;
			Channel[] subs = new Channel[channel.subfilter.length];
			for(Channel c : channel.subfilter){
				if(c.type == 1){
					subs[subCounter++] = c;
				}
			}
			Channel[] mSubs = new Channel[subCounter];
			for(int i = 0 ; i < subCounter; i ++){
				mSubs[i] = subs[i];
			}
			mChannel.subfilter = mSubs;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.channel_grid_filter_layout, null);
			holder = new ViewHolder();
			holder.mNameTextView = (TextView) convertView.findViewById(R.id.channel_item_header_name);
			holder.mGridsGridLayout = (GridView) convertView.findViewById(R.id.channel_item_grids);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		Channel c = mChannel.subfilter[position];
		holder.mNameTextView.setText(c.name);
		
		FilterCheckItemAdapter mFilterAdapter = new FilterCheckItemAdapter(mContext);
		mFilterAdapter.setFilterCheckListener(mCheckListener);
		if(c.subfilter != null && c.subfilter.length > 0){
			mFilterAdapter.setFilterItems(c.subfilter);
		}
		holder.mGridsGridLayout.setAdapter(mFilterAdapter);
		return convertView;
	}

	FilterCheckListener mCheckListener = new FilterCheckListener() {
		
		@Override
		public void check(boolean checked, Channel c) {
			if(checked){
				mIDs.add(c);
			}else{
				mIDs.remove(c);
			}
		}
	};
	
	public Channel[] getIDs(){
		Channel[] result = new Channel[mIDs.size()];
		for(int i = 0; i < mIDs.size(); i ++){
			result[i] = mIDs.get(i);
		}
		return result;
	}
	
	public interface FilterCheckListener{
		public void check(boolean checked, Channel c);
	}
	
	private class ViewHolder {
		private TextView mNameTextView;
		private GridView mGridsGridLayout;
	}

	@Override
	public int getCount() {
		if(mChannel.subfilter == null){
			return 0;
		}
		return mChannel.subfilter.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
