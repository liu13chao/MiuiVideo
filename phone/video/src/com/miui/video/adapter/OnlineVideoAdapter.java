package com.miui.video.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.type.Channel;
import com.miui.video.widget.media.MediaPagerViewBase;


/**
 *@author tangfuling
 *
 */

public class OnlineVideoAdapter extends BaseGroupAdapter<Object> {

	private RecommendationLoader mRecommendLoader;
	private ArrayList<Channel> mRecommendChannels;
	
	private OnItemClickListener mOnItemClickListener;
	
	public OnlineVideoAdapter(Context context) {
		super(context);
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
	public void setRecommendChannels(ArrayList<Channel> recommendChannels, RecommendationLoader recommendLoader) {
		this.mRecommendLoader = recommendLoader;
		this.mRecommendChannels = recommendChannels;
		notifyDataSetChanged();
	}
	
	@Override
	public int getItemViewType(int position) {
		if(mRecommendChannels != null) {
			Channel channel = mRecommendChannels.get(position);
			if(channel != null) {
				return channel.listtype;
			}
		}
		return MediaViewHelper.MEDIA_CLASSIFY_TYPE_V;
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getCount() {
		if(mRecommendChannels != null && mRecommendChannels.size() > 0) {
			return mRecommendChannels.size();
		}
		return 0;
	}
	
	private class ViewHolder {
		MediaPagerViewBase mediaPagerView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		int itemViewType = getItemViewType(position);
		
		if(convertView == null) {
			if(itemViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_H) {
				viewHolder = new ViewHolder(); 
				View view = View.inflate(mContext, R.layout.online_video_item_h, null);
				viewHolder.mediaPagerView = (MediaPagerViewBase) view.findViewById(R.id.online_video_media_pager_view);
				viewHolder.mediaPagerView.setShowSubTitle(false);
				viewHolder.mediaPagerView.setOnItemClickListener(mOnItemClickListener);
				convertView = view;
				convertView.setTag(viewHolder); 
			} else if(itemViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_TV) {
				viewHolder = new ViewHolder(); 
				View view = View.inflate(mContext, R.layout.online_video_item_tv, null);
				viewHolder.mediaPagerView = (MediaPagerViewBase) view.findViewById(R.id.online_video_media_pager_view);
				viewHolder.mediaPagerView.setOnItemClickListener(mOnItemClickListener);
				convertView = view;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = new ViewHolder(); 
				View view = View.inflate(mContext, R.layout.online_video_item_v, null);
				viewHolder.mediaPagerView = (MediaPagerViewBase) view.findViewById(R.id.online_video_media_pager_view);
				viewHolder.mediaPagerView.setOnItemClickListener(mOnItemClickListener);
				convertView = view;
				convertView.setTag(viewHolder);
			}
		} else {
            viewHolder = (ViewHolder) convertView.getTag();
		}
		if(mRecommendChannels != null && mRecommendChannels.size() > position) {
			Channel channel = mRecommendChannels.get(position);
			if(channel != null) {
                viewHolder.mediaPagerView.setRecommendationLoader(mRecommendLoader);
//                viewHolder.mediaPagerView.setChannelRecommendation(getItem(position));
			}
		}
		
		return convertView;
	}
}
