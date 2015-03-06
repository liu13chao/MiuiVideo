package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.widget.media.MyVideoView;
import com.miui.video.widget.media.MyVideoView.OnMyVideoClickListener;

public class MyVideoAdapter extends BaseGroupAdapter<MyVideoItem> {

	private Context mContext;
	private OnMyVideoClickListener mListener;
	
	public MyVideoAdapter(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setOnMyVideoClickListener(OnMyVideoClickListener listener) {
		this.mListener = listener;
	}
	
	private class ViewHolder {
		MyVideoView myVideoView1;
		MyVideoView myVideoView2;
	}
	
	@Override
	public int getCount() {
		int rows = (int) Math.ceil(mGroup.size() / 2f);
		return rows;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			View view = View.inflate(mContext, R.layout.my_video_item, null);
			viewHolder = new ViewHolder();
			viewHolder.myVideoView1 = (MyVideoView) view.findViewById(R.id.my_video_item_1);
			viewHolder.myVideoView2 = (MyVideoView) view.findViewById(R.id.my_video_item_2);
			viewHolder.myVideoView1.setOnMyVideoClickListener(mListener);
			viewHolder.myVideoView2.setOnMyVideoClickListener(mListener);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MyVideoItem myVideoItem1 = null;
		MyVideoItem myVideoItem2 = null;
		if(position * 2 < mGroup.size()) {
			myVideoItem1 = mGroup.get(position * 2);
		}
		if(position * 2 + 1 < mGroup.size()) {
			myVideoItem2 = mGroup.get(position * 2 + 1);
		}
		viewHolder.myVideoView1.setItem(myVideoItem1);
		viewHolder.myVideoView2.setItem(myVideoItem2);
		
		return convertView;
	}
}
