package com.miui.video.widget.filter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;

public class MediaFilterAdapter extends BaseGroupAdapter<CategoryDetailInfo> {

	private String mCurCategoryName;
	
	private class ViewHolder {
		View icon;
		TextView text;
	}
	
	public MediaFilterAdapter(Context context) {
		super(context);
	}
	
	public void setCurCategoryName(String categoryName) {
		this.mCurCategoryName = categoryName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(mContext, R.layout.media_filter_view_item, null);
			viewHolder.icon = view.findViewById(R.id.media_filter_view_item_icon);
			viewHolder.text = (TextView) view.findViewById(R.id.media_filter_view_item_text);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		CategoryDetailInfo categoryDetailInfo = getItem(position);
		if(categoryDetailInfo != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(categoryDetailInfo.categoryName);
			sb.append("(");
			sb.append(categoryDetailInfo.mediaCount);
			sb.append(")");
			viewHolder.text.setText(sb.toString());
			
			if(mCurCategoryName != null 
					&& mCurCategoryName.equals(categoryDetailInfo.categoryName)) {
				viewHolder.icon.setVisibility(View.VISIBLE);
				viewHolder.text.setTextColor(mContext.getResources().getColor(R.color.orange));
			} else {
				viewHolder.icon.setVisibility(View.INVISIBLE);
				viewHolder.text.setTextColor(mContext.getResources().getColor(R.color.p_70_black));
			}
		}
		return convertView;
	}
}
