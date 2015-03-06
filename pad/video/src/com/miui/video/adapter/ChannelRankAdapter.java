package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.RankInfo;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaViewRows;

/**
 *@author tangfuling
 *
 */

public class ChannelRankAdapter extends BaseGroupAdapter<RankInfo> {

	private Context context;
	
	private OnMediaClickListener onMediaClickListener;
	
	public ChannelRankAdapter(Context context) {
		super(context);
		this.context = context;
	}
	
	public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
		this.onMediaClickListener = onMediaClickListener;
	}
	
	private class ViewHolder {
		TextView categoryName;
		MediaViewRows mediaViewRows;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(context, R.layout.channel_rank_item, null);
			
			viewHolder.categoryName = (TextView) view.findViewById(R.id.channel_rank_category_name);
			
			viewHolder.mediaViewRows = (MediaViewRows) view.findViewById(R.id.channel_rank_item_rows);
			viewHolder.mediaViewRows.setOnMediaClickListener(onMediaClickListener);
			
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		RankInfo rankInfo = (RankInfo) mGroup.get(position);
		if(rankInfo != null) {
			viewHolder.categoryName.setText(rankInfo.name);
			
			MediaInfo[] mediaInfos = rankInfo.data;
			if(mediaInfos != null && mediaInfos.length > 0) {
				viewHolder.mediaViewRows.setMediaViewContents(mediaInfos);
			}
		}
		
		return convertView;
	}

}
