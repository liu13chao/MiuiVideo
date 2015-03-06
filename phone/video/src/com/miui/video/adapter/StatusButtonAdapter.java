package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.miui.video.R;
import com.miui.video.widget.statusbtn.StatusBtn;
import com.miui.video.widget.statusbtn.StatusBtn.OnStatusBtnClickListener;
import com.miui.video.widget.statusbtn.StatusBtnItem;
import com.miui.video.widget.statusbtn.StatusBtnItemList;

/**
 * @author tangfuling
 * 
 */

public class StatusButtonAdapter extends BaseAdapter implements
		OnClickListener {

	private Context mContext;

	private StatusBtnItemList mStatusBtnItemList;
	private OnStatusBtnClickListener mOnStatusBtnClickListener;

	public StatusButtonAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(StatusBtnItemList statusBtnItemList) {
		mStatusBtnItemList = statusBtnItemList;
		notifyDataSetChanged();
	}

	public void setOnStatusBtnClickListener(OnStatusBtnClickListener listener) {
		this.mOnStatusBtnClickListener = listener;
	}

	public void refresh() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mStatusBtnItemList == null) {
			return 0;
		}
		return (int) Math.ceil(mStatusBtnItemList.size() / (float) 3);
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		public StatusBtn[] vEpisode;
		public View episodeDividerFirst;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.detail_episode_row,
					null);
			initViewHolder(vHolder, convertView);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		if (position != 0) {
			vHolder.episodeDividerFirst.setVisibility(View.GONE);
		} else {
			vHolder.episodeDividerFirst.setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < 3; i++) {
			StatusBtn statusBtn = vHolder.vEpisode[i];
			if (3 * position + i < mStatusBtnItemList.size()) {
				statusBtn.setVisibility(View.VISIBLE);
				statusBtn.setOnClickListener(this);
				
				int curCi = 3 * position + i + 1;
				StatusBtnItem statusBtnItem = mStatusBtnItemList.getStatusBtnItem(curCi);
				if(statusBtnItem != null) {
					String str = mContext.getResources().getString(R.string.di_count_ji);
					str = String.format(str, statusBtnItem.episode);
					statusBtnItem.text = str;
					statusBtn.setStatusBtnItem(statusBtnItem);
				}
			} else {
				statusBtn.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}
	
	private void initViewHolder(ViewHolder vHolder, View convertView) {
		vHolder.vEpisode = new StatusBtn[3];
		vHolder.vEpisode[0] = (StatusBtn) convertView.findViewById(R.id.episode1);
		vHolder.vEpisode[1] = (StatusBtn) convertView.findViewById(R.id.episode2);
		vHolder.vEpisode[2] = (StatusBtn) convertView.findViewById(R.id.episode3);
		vHolder.episodeDividerFirst = convertView
				.findViewById(R.id.episode_divider_first);
	}

	@Override
	public void onClick(View v) {
		if (mOnStatusBtnClickListener != null) {
			if(v instanceof StatusBtn) {
				StatusBtn statusBtn = (StatusBtn) v;
				mOnStatusBtnClickListener.onStatusBtnClick(statusBtn);
			}
		}
	}
}
