package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.type.Category;
import com.miui.video.widget.media.MediaViewCategory;
import com.miui.video.widget.media.MediaViewCategory.OnCategoryMediaClickListener;

/**
 *@author tangfuling
 *
 */
public class CategoryListAdapter extends BaseGroupAdapter<Category> {
	
	private OnCategoryMediaClickListener mListener;
	
//	private HashMap<Integer, Channel> mChannelMap;
	
	public CategoryListAdapter(Context context) {
		super(context);
	}
	
	public void setOnCategoryMediaClickListener(
			OnCategoryMediaClickListener listener) {
		this.mListener = listener;
	}
//	
//	public void setChannelMap(HashMap<Integer, Channel> channelMap) {
//		mChannelMap = channelMap;
//	}
	
	private class ViewHolder {
		MediaViewCategory categoryMediaView;
		Category category;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(mContext, R.layout.category_list_item, null);
			viewHolder.categoryMediaView = (MediaViewCategory) 
			        view.findViewById(R.id.media_view_category);
			viewHolder.categoryMediaView.setOnCategoryMediaClickListener(mListener);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Category category = mGroup.get(position);
		if(viewHolder.category == category){
		    return convertView;
		}
		viewHolder.category = category;
		viewHolder.categoryMediaView.setCategory(category);
		return convertView;
	}
}
