package com.miui.video.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class SearchHintAdapter extends BaseGroupAdapter<String> implements OnClickListener {

	private Context context;
	private OnSearchHintActionListener listener;
	
	private int MAX_ITEM_COUNT = 6;
	private int itemCount = 0;
	private String mSearchKey;
	
	public SearchHintAdapter(Context context) {
		super(context);
		this.context = context;
	}
	
	public void setOnSearchHintActionListener(OnSearchHintActionListener listener) {
		this.listener = listener;
	}
	
	public void setSearchKey(String search){
	    mSearchKey = search;
	    refresh();
	}
	
	public boolean isSearchHistory(){
	    return TextUtils.isEmpty(mSearchKey);
	}

	private class ViewHolder {
		TextView hintTv;
		View divider;
	    ImageView icon;
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
			viewHolder.divider = view.findViewById(R.id.divider);
			viewHolder.icon = (ImageView)view.findViewById(R.id.icon);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.position = position;
		String text = mGroup.get(position);
		if(isSearchHistory()){
		    viewHolder.hintTv.setText(text);
		    viewHolder.icon.setImageResource(R.drawable.history_time);
		}else{
		    if(!TextUtils.isEmpty(text) && !TextUtils.isEmpty(mSearchKey)){
		        text = text.replace(mSearchKey, "<font color='#f95f22'>" + mSearchKey + "</font>");
		    }
		    viewHolder.hintTv.setText(Html.fromHtml(text));
	          viewHolder.icon.setImageResource(R.drawable.icon_search);
		}
		int count = getCount();
		if(count == 1 && !isSearchHistory()) {
			convertView.setBackgroundResource(R.drawable.com_bg_white_corner);
		} else {
			if(position == 0) {
				convertView.setBackgroundResource(R.drawable.com_bg_white_corner_t);
			} else if(position == count - 1 && !isSearchHistory()) {
				convertView.setBackgroundResource(R.drawable.com_bg_white_corner_d);
			} else {
				convertView.setBackgroundResource(R.drawable.com_bg_white_corner_v_m);
			}
		}
		if(position == count - 1){
		    viewHolder.divider.setVisibility(View.INVISIBLE);
		}else{
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
