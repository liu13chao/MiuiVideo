package com.miui.video.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.Channel;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.StringUtils;

/**
 *@author tangfuling
 *
 */

public class ChannelRankAdapter extends BaseGroupAdapter<MediaInfo> {
	public static final String TAG = "ChannelRankAdapter";
	
//	private final Channel mChannel;
	private int mCategory;
	private boolean mShowRank = true;
	private boolean mShowScroe = true;
	
	public ChannelRankAdapter(Context context, Channel channel, int category) {
		super(context);
		if (channel == null) {
			throw new IllegalArgumentException("Channel should not be null");
		}
//		mChannel = channel;
		mCategory = category;
	}
	
	public void setShowRank(boolean show){
		mShowRank = show;
	}
	
	public void setShowScroe(boolean show){
		mShowScroe = show;
	}
	
	private static class ViewHolder {
		RelativeLayout layout;
		ImageView poster;
		TextView title;
		TextView subtitle;
		TextView actors;
		TextView score;
		TextView place;
		View line;
		View padding;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = View.inflate(mContext, R.layout.channel_rank_item, null);
			holder = new ViewHolder();
			if(!mShowScroe){
				convertView.findViewById(R.id.channel_rank_item_score_layout).setVisibility(View.GONE);
				holder.score = (TextView) convertView.findViewById(R.id.channel_rank_item_hot);
			}else{
				convertView.findViewById(R.id.channel_rank_item_hot_layout).setVisibility(View.GONE);
				holder.score = (TextView) convertView.findViewById(R.id.channel_rank_item_score);
			}
			holder.layout = (RelativeLayout) convertView.findViewById(R.id.channel_rank_item_layout);
			holder.poster = (ImageView) convertView.findViewById(R.id.channel_rank_item_poster);
			holder.title = (TextView) convertView.findViewById(R.id.channel_rank_item_title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.channel_rank_item_subtitle);
			holder.actors = (TextView) convertView.findViewById(R.id.channel_rank_item_actor);
			holder.place = (TextView) convertView.findViewById(R.id.channel_rank_item_place);
			holder.line = convertView.findViewById(R.id.channel_rank_item_line);
			holder.padding = convertView.findViewById(R.id.channel_rank_item_padding);
			convertView.setTag(holder);
		}
		
		MediaInfo info = getItem(position);
		if (info == null) {
			return convertView;
		}
		// poster
		ImageUrlInfo posterInfo = info.getPosterInfo();
		if(!ImageManager.isUrlDone(posterInfo, holder.poster)){
			holder.poster.setImageResource(R.drawable.transparent);
			ImageManager.getInstance().fetchImage(ImageManager.createTask(posterInfo, null),
			        holder.poster);
		}
		// title
		holder.title.setText(info.medianame);
		// subtitle
		final Resources res = mContext.getResources();
		if (Channel.CHANNEL_TYPE_MOVIE == mCategory) {
		    final StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(info.area)) {
                sb.append(info.area);
            } else {
                sb.append(res.getString(R.string.area_unknown));
            }
            if (!TextUtils.isEmpty(info.issuedate)) {
                sb.append(" | ").append(info.issuedate);
            }
            holder.subtitle.setText(sb.toString());
		} else if (Channel.CHANNEL_TYPE_VARIETY == mCategory) {
			holder.subtitle.setText(res.getString(R.string.update_to_count_qi, info.lastissuedate));
		} else {
	         if (info.setnow >= info.setcount && info.setcount != 0) {
	                holder.subtitle.setText(res.getString(R.string.count_ji_quan, info.setcount));
	            } else {
	                holder.subtitle.setText(res.getString(R.string.update_to_count_ji, info.setnow));
	            }
		}
		// actor
		String actors = cleanText(info.actors);
		if (!TextUtils.isEmpty(actors)) {
			holder.actors.setText(actors.replace(" ", " / "));
		}
		// score
		try {
			if(mShowScroe){
				holder.score.setText(StringUtils.formatString("%.1f", info.score));
			}else{
				holder.score.setText(StringUtils.formatString(" %d", info.playcount));
			}
		} catch (Exception e) {
			DKLog.e(TAG, "score: " + info.score + ", exception: " + e.getMessage());
		}
		// place
		if(mShowRank){
			holder.place.setVisibility(View.VISIBLE);
			holder.place.setText(mContext.getResources().getString(R.string.place_at, (position + 1)));
		}else{
			holder.place.setVisibility(View.INVISIBLE);
		}
		
		int size = getCount();
		if(size == 1) {
			holder.layout.setBackgroundResource(R.drawable.com_item_bg_full);
			holder.line.setVisibility(View.INVISIBLE);
			holder.padding.setVisibility(View.GONE);
		} else {
			if(position == 0) {
				holder.layout.setBackgroundResource(R.drawable.com_item_bg_up);
				holder.line.setVisibility(View.VISIBLE);
				holder.padding.setVisibility(View.GONE);
			} else if(position == size - 1) {
				holder.layout.setBackgroundResource(R.drawable.com_item_bg_down);
				holder.line.setVisibility(View.INVISIBLE);
				holder.padding.setVisibility(View.VISIBLE);
			} else {
				holder.layout.setBackgroundResource(R.drawable.com_item_bg_mid);
				holder.line.setVisibility(View.VISIBLE);
				holder.padding.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	private static final String USELESS_WHITESPACE = "[\\x00\\t\\n\\x0B\\f\\r]+?";
	private static final String REPEATED_BLANK = " +?";

	private static String cleanText(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll(USELESS_WHITESPACE, "").replaceAll(REPEATED_BLANK, " ").trim();
	}

}
