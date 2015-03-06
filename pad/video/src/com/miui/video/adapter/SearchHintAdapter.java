package com.miui.video.adapter;

import com.miui.video.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class SearchHintAdapter extends BaseGroupAdapter<String> implements OnClickListener {

	private Context context;
	private OnSearchHintActionListener listener;
	
	private int MAX_ITEM_COUNT = 5;
	private int itemCount = 0;
	
	public SearchHintAdapter(Context context) {
		super(context);
		this.context = context;
	}
	
	public void setOnSearchHintActionListener(OnSearchHintActionListener listener) {
		this.listener = listener;
	}

	private class ViewHolder {
		TextView hintTv;
		View divider;
		int position;
	}
	
	@Override
	public int getCount() {
		if(mGroup != null) {
			itemCount = mGroup.size();
			if(itemCount > MAX_ITEM_COUNT) {
				itemCount = MAX_ITEM_COUNT;
			}
		}
		return itemCount;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(context, R.layout.search_hint_item, null);
			view.setOnClickListener(this);
			viewHolder.hintTv = (TextView) view.findViewById(R.id.search_hint_tv);
			viewHolder.divider = view.findViewById(R.id.search_hint_divider);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.hintTv.setText(mGroup.get(position));
		viewHolder.position = position;
		if(position == itemCount - 1) {
			viewHolder.divider.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.divider.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if(tag instanceof ViewHolder) {
			ViewHolder viewHolder = (ViewHolder) tag;
			if(listener != null) {
				listener.onSelect(viewHolder.hintTv.getText().toString(), viewHolder.position);
			}
		}
	}
	
	public interface OnSearchHintActionListener {
		public void onSelect(String hint, int position);
	}
}
