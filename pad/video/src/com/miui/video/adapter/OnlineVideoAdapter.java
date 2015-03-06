package com.miui.video.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.type.Channel;
import com.miui.video.type.ShowBaseInfo;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaViewRow;


/**
 *@author tangfuling
 *
 */

public class OnlineVideoAdapter extends BaseGroupAdapter<Object> {

	private Context context;
	
	private RecommendationLoader recommendLoader;
	private ArrayList<Channel> recommendChannels;
	
	private OnMediaClickListener onMediaClickListener;
	private OnClickListener onClickListener;
	
	private int sizePerRow;
	
	public OnlineVideoAdapter(Context context, int sizePerRow) {
		super(context);
		this.context = context;
		this.sizePerRow = sizePerRow;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
	
	public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
		this.onMediaClickListener = onMediaClickListener;
	}
	
	public void setSizePerRow(int sizePerRow) {
		this.sizePerRow = sizePerRow;
	}
	
	public void setRecommendChannels(ArrayList<Channel> recommendChannels, RecommendationLoader recommendLoader) {
		this.recommendLoader = recommendLoader;
		this.recommendChannels = recommendChannels;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(recommendChannels != null && recommendChannels.size() > 0) {
			return recommendChannels.size();
		}
		return 0;
	}
	
	private class ViewHolder {
		Button btnMore;
		TextView categoryName;
		MediaViewRow mediaViewRow;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(context, R.layout.online_video_item, null);
			
			viewHolder.btnMore = (Button) view.findViewById(R.id.online_video_btn_more);
			viewHolder.categoryName = (TextView) view.findViewById(R.id.online_video_category_name);
			viewHolder.btnMore.setOnClickListener(onClickListener);
			
			viewHolder.mediaViewRow = (MediaViewRow) view.findViewById(R.id.online_video_item_row);
			viewHolder.mediaViewRow.setOnMediaClickListener(onMediaClickListener);
			
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(recommendChannels != null && recommendChannels.size() > position && recommendLoader != null) {
			Channel channel = recommendChannels.get(position);
			if(channel != null) {
				viewHolder.categoryName.setText(channel.name);
				viewHolder.btnMore.setTag(channel);
				ShowBaseInfo[] showBaseInfos = recommendLoader.getRecommendation(channel);
				if(showBaseInfos != null && showBaseInfos.length > 0) {
					Object[] mediaContentInfos = new Object[sizePerRow];
					for(int i = 0; i < sizePerRow; i++) {
						if(i < showBaseInfos.length) {
							mediaContentInfos[i] = showBaseInfos[i];
						}
					}
					viewHolder.mediaViewRow.setMediaViewContents(mediaContentInfos);
				}
			}
		}
		
		return convertView;
	}
}
