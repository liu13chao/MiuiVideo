package com.miui.video.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.local.PlayHistory;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;
import com.miui.video.widget.media.MediaViewRows;

/**
 *@author tangfuling
 *
 */

public class PlayHistoryAdapter extends BaseGroupAdapter<Object> {

	private Context context;
	
	private OnMediaClickListener onMediaClickListener;
	private OnMediaLongClickListener onMediaLongClickListener;
	
	private boolean inEditMode = false;
	
	private List<String> playHisDateList;
	private HashMap<String, List<PlayHistory>> playHisListMap;
	private List<Object> selectedMedias;
	
	public PlayHistoryAdapter(Context context) {
		super(context);
		this.context = context;
	}
	
	public OnMediaClickListener getOnMediaClickListener() {
		return onMediaClickListener;
	}

	public void setOnMediaClickListener(
			OnMediaClickListener onMediaClickListener) {
		this.onMediaClickListener = onMediaClickListener;
	}
	
	public void setOnMediaLongClickListener(
			OnMediaLongClickListener onMediaLongClickListener) {
		this.onMediaLongClickListener = onMediaLongClickListener;
	}
	
	public void setData(List<String> playHisDateList, HashMap<String, List<PlayHistory>> playHisListMap,
			List<Object> selectedMedias) {
		this.playHisDateList = playHisDateList;
		this.playHisListMap = playHisListMap;
		this.selectedMedias = selectedMedias;
		refresh();
	}
	
	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
		refresh();
	}
	
	@Override
	public int getCount() {
		if(playHisDateList != null) {
			return playHisDateList.size();
		}
		return 0;
	}
	
	private class ViewHolder {
		TextView title;
		MediaViewRows mediaViewRows;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			
			View view = View.inflate(context, R.layout.play_his_item, null);
			
			viewHolder.title = (TextView) view.findViewById(R.id.play_his_item_title);
			viewHolder.mediaViewRows = (MediaViewRows) view.findViewById(R.id.play_his_item_rows);
			
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.mediaViewRows.setOnMediaClickListener(onMediaClickListener);
		viewHolder.mediaViewRows.setOnMediaLongClickListener(onMediaLongClickListener);
		
		if(playHisDateList != null && playHisListMap != null) {
			String date = playHisDateList.get(position);
			List<PlayHistory> playHisList = null;
			if(!Util.isEmpty(date)) {
				playHisList = playHisListMap.get(date);
				if(Util.isToday(date)) {
					String str = context.getResources().getString(R.string.today);
					viewHolder.title.setText(str);
				} else if(Util.isYesterday(date)) {
					String str = context.getResources().getString(R.string.yesterday);
					viewHolder.title.setText(str);
				} else {
					viewHolder.title.setText(date);
				}
			}
			
			if(playHisList != null) {
				Object[] mediaContentInfos = new Object[playHisList.size()];
				for(int i = 0; i < playHisList.size(); i++) {
					PlayHistory playHis = playHisList.get(i);
					if(playHis != null) {
						mediaContentInfos[i] = playHis;
					}
				}
				viewHolder.mediaViewRows.setMediaViewContents(mediaContentInfos, selectedMedias, inEditMode);
			}
		}

		return convertView;
	}

}
