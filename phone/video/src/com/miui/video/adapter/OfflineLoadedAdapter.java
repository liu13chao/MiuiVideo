package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.item.VerticalPosterItemView;
import com.miui.video.offline.OfflineMediaList;

public class OfflineLoadedAdapter extends BaseEditableGroupAdapter<OfflineMediaList> {

	public OfflineLoadedAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final VerticalPosterItemView view;
		if(convertView instanceof VerticalPosterItemView) {
			view = (VerticalPosterItemView) convertView;
		} else {
			view = new VerticalPosterItemView(mContext);
		}
		
		final OfflineMediaList item = getItem(position);
		if (item == null) {
			return view;
		}
		
		view.setOfflineMediaList(item);
		
		if (isInEditMode()) {
			if (isSelected(position)) {
				view.setPosterBackgroudColor(0xfff95f22);
				view.setFlag(R.drawable.offline_edito_pressed_pic);
			} else {
				view.setPosterBackgroudColor(0xffcdcdcd);
				view.setFlag(R.drawable.offline_edito_pic);
			}
		} else {
			view.setPosterBackgroud(null);
			view.setFlag(null);
		}
		return view;
	}

}
