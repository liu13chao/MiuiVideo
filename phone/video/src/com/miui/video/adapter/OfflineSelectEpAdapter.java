package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.offline.OfflineMedia;

public class OfflineSelectEpAdapter extends BaseEditableGroupAdapter<OfflineMedia> {
	
	private int mCurrentEp = 1;
	private ArrayList<String> mEpNames = new ArrayList<String>();
	private boolean mShowEpDetail = false;
	
	public OfflineSelectEpAdapter(Context context) {
		super(context);
		enterEditMode();
	}
	
	public void showEpDetail(List<String> lists){
		mShowEpDetail = true;
		mEpNames.clear();
		mEpNames.addAll(lists);
	}
	
	public void setCurrentEpisode(int episode) {
		if (episode <= 0 || episode == mCurrentEp) {
			return;
		}
		mCurrentEp = episode;
		notifyDataSetChanged();
	}
	
	@Override
    public boolean areAllItemsEnabled() {
        return false;
    }

	@Override
    public boolean isEnabled(int position) {
		final OfflineMedia item = getItem(position);
		if (item != null && item.isNone()) {
			return true;
		}
        return false;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = View.inflate(mContext, R.layout.offline_select_ep_item, null);
			holder = new ViewHolder();

			holder.icon = (ImageView) convertView.findViewById(R.id.offline_select_ep_item_ci_icon);
			holder.ci = (TextView) convertView.findViewById(R.id.offline_select_ep_item_ci);
			if(mShowEpDetail){
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT, 
						mContext.getResources().getDimensionPixelSize(R.dimen.size_180)));
				holder.ci.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				holder.ci.setTextSize(TypedValue.COMPLEX_UNIT_PX, 
						mContext.getResources().getDimensionPixelSize(R.dimen.font_size_42));
			}else{
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT, 
						mContext.getResources().getDimensionPixelSize(R.dimen.size_100)));
			}
			convertView.setTag(holder);
		}
		
		final OfflineMedia item = getItem(position);
		if (item == null) {
			return convertView;
		}
		
		if(mShowEpDetail){
			try {
				holder.ci.setText(String.valueOf(mEpNames.get(position)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			holder.ci.setText(String.valueOf(item.episode));
		}
		if (item.episode == mCurrentEp) {
			holder.ci.setTextColor(mContext.getResources().getColor(R.color.orange));
		} else {
			holder.ci.setTextColor(mContext.getResources().getColor(R.color.text_color_deep_dark));
		}
		
		final boolean enabled = isEnabled(position);
		final boolean selected = isSelected(position);
		if (enabled) {
			if (selected) {
				holder.icon.setImageResource(R.drawable.offline_in_1_pic);
				holder.ci.setSelected(true);
			} else {
				holder.icon.setImageDrawable(null);
				holder.ci.setSelected(false);
			}
		} else {
			holder.icon.setImageResource(R.drawable.offline_already_pic);
			holder.ci.setSelected(false);
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView icon;
		TextView ci;
	}
}
