package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.MediaInfo;

public class ChannelChoiceAdapter extends BaseGroupAdapter<ChannelRecommendation> {
	
//	private static final int COLUMN_COUNT_VERT_POSTER = 3;
//	private static final int COLUMN_COUNT_HORI_POSTER = 2;

//	private static final int VIEW_TYPE_VERT_POSTER = 0;
//	private static final int VIEW_TYPE_HORI_POSTER = 1;
	private static final int VIEW_TYPE_START = 0;
	private static final int VIEW_TYPE_END = 1;
	
//	private OnMediaInfoSelectListener mListener;
//	private final Channel mChannel;

	public ChannelChoiceAdapter(Context context, Channel channel) {
		super(context);
//		if (channel == null) {
//			throw new IllegalArgumentException("Channel should not be null");
//		}
//		mChannel = channel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    //TODO:
//	a	final ChannelRecommendation item = getItem(position);
//		if (item == null || item.getRecommendCount() == 0) {
//			return convertView;
//		}
//		final int dataLength = item.getRecommendCount();
//		final int type = getItemViewType(position);
//		final ViewHolder holder;
//		if (shouldInflate(convertView, type)) {
//			convertView = View.inflate(mContext, R.layout.channel_choice_item, null);
//			if (VIEW_TYPE_HORI_POSTER == type) {
//				holder = new HoriPosterViewHolder();
//				holder.mNameTextView = (TextView) convertView.findViewById(R.id.channel_item_header_name);
//				holder.mGridsGridLayout = (GridLayout) convertView.findViewById(R.id.channel_item_grids);
//				holder.mGridsGridLayout.setColumnCount(COLUMN_COUNT_HORI_POSTER);
//				holder.padding = convertView.findViewById(R.id.channel_item_padding);
//				for (int i = 0; i < dataLength; i++) {
//					holder.mGridsGridLayout.addView(new HorizontalPosterItemView(mContext));
//				}
//			} else {
//				holder = new VertPosterViewHolder();
//				holder.mNameTextView = (TextView) convertView.findViewById(R.id.channel_item_header_name);
//				holder.mGridsGridLayout = (GridLayout) convertView.findViewById(R.id.channel_item_grids);
//				holder.mGridsGridLayout.setColumnCount(COLUMN_COUNT_VERT_POSTER);
//				holder.padding = convertView.findViewById(R.id.channel_item_padding);
//				for (int i = 0; i < dataLength; i++) {
//					holder.mGridsGridLayout.addView(new VerticalPosterItemView(mContext));
//				}
//			}
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//			final int viewLength = holder.mGridsGridLayout.getChildCount();
//			if (viewLength < dataLength) {
//				if (VIEW_TYPE_HORI_POSTER == type) {
//					for (int i = viewLength; i < dataLength; i++) {
//						holder.mGridsGridLayout.addView(new HorizontalPosterItemView(mContext));
//					}
//				} else {
//					for (int i = viewLength; i < dataLength; i++) {
//						holder.mGridsGridLayout.addView(new HorizontalPosterItemView(mContext));
//					}
//				}
//			} else {
//				holder.mGridsGridLayout.removeViews(dataLength, viewLength - dataLength); 
//			}
//		}
//		if(mChannel.recsub != null && position < mChannel.recsub.length){
//			holder.mNameTextView.setText(mChannel.recsub[position].name);
//		}else{
//			holder.mNameTextView.setText(String.valueOf(item.id));
//		}
//        final BaseMediaInfo[] info = item.getRecommendMedias();
//		for (int i = 0; i < dataLength; i++) {
//			final ItemView child = (ItemView) holder.mGridsGridLayout.getChildAt(i);
//			if (child != null && info != null && i < info.length) {
//				child.setMediaInfo(mChannel.getVarietyChannelType(), info[i]);
//				child.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
////						if (mListener != null) {
////							mListener.OnMediaInfoSelect(info.buildMediaInfo());
////						}
//					}
//				});
//			}
//		}
//		int size = getCount();
//		if(size > 1 && position == size - 1){
//			holder.padding.setVisibility(View.VISIBLE);
//		}else{
//			holder.padding.setVisibility(View.GONE);
//		}
		return convertView;
	}
	
//	private boolean shouldInflate(View convertView, int type) {
//		if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
//			return true;
//		}
//		ViewHolder holder = (ViewHolder) convertView.getTag();
//		return holder.getViewType() != type;
//	}
//
//	@Override
//	public int getItemViewType(int position) {
//		ChannelRecommendation item = getItem(position);
//		//TODO:
////		if (item != null && item.listtype == ChannelRecommendation.LIST_TYPE_HORI_POSTER) {
////			return VIEW_TYPE_HORI_POSTER;
////		} else {
//			return VIEW_TYPE_VERT_POSTER;
////		}
//	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_END - VIEW_TYPE_START + 1;
	}
	
//	private static abstract class ViewHolder {
//		private TextView mNameTextView;
//
//		private GridLayout mGridsGridLayout;
//		
//		private View padding;
//		
//		public abstract int getViewType();
//	}
//	
//	private static class HoriPosterViewHolder extends ViewHolder {
//
//		@Override
//		public int getViewType() {
//			return VIEW_TYPE_HORI_POSTER;
//		}
//		
//	}
//	
//	private static class VertPosterViewHolder extends ViewHolder {
//
//		@Override
//		public int getViewType() {
//			return VIEW_TYPE_VERT_POSTER;
//		}
//		
//	}
	
//	public void setOnMediaInfoSelectListener(OnMediaInfoSelectListener listener) {
//		mListener = listener;
//	}
	
	public static interface OnMediaInfoSelectListener {
		public void OnMediaInfoSelect(MediaInfo media);
	}
	

}
