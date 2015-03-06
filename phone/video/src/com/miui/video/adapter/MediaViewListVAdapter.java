package com.miui.video.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;

/**
 *@author tangfuling
 *
 */

public class MediaViewListVAdapter extends BaseGroupAdapter<Object> {
	
	public static final String TAG = "MediaViewListVAdapter";

	public MediaViewListVAdapter(Context context) {
		super(context);
	}
	
	private static class ViewHolder {
		ImageView poster;
		TextView title;
		TextView subtitle;
		TextView actors;
		TextView score;
	     View divider;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = View.inflate(mContext, R.layout.media_view_list_v_item, null);
			holder = new ViewHolder();
			
			holder.poster = (ImageView) convertView.findViewById(R.id.media_view_list_v_poster);
			holder.title = (TextView) convertView.findViewById(R.id.media_view_list_v_title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.media_view_list_v_subtitle);
			holder.actors = (TextView) convertView.findViewById(R.id.media_view_list_v_actor);
			holder.score = (TextView) convertView.findViewById(R.id.media_view_list_v_score);
			holder.divider = convertView.findViewById(R.id.divider);
			convertView.setTag(holder);
		}
		
		Object obj = getItem(position);
		if(obj instanceof MediaInfo) {
			MediaInfo info = (MediaInfo) obj;
			ImageUrlInfo posterInfo = info.getPosterInfo();
			if(!ImageManager.isUrlDone(posterInfo, holder.poster)) {
			    holder.poster.setImageResource(R.drawable.transparent);
			    //TODO
		         ImageManager.getInstance().fetchImage(ImageManager.createTask(posterInfo, null), holder.poster);
			}
			// title
			holder.title.setText(info.medianame);
			// subtitle
			final Resources res = mContext.getResources();
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
			// actor
			final String actors = cleanText(info.actors);
			if (!TextUtils.isEmpty(actors)) {
				holder.actors.setText(actors.replace(" ", " / "));
			}
			// score
			try {
				holder.score.setText(String.format("%.1f", info.score));
			} catch (Exception e) {
				DKLog.e(TAG, "score: " + info.score + ", exception: " + e.getMessage());
			}
			
			int count = getCount();
			if(count == 1) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_full);
			} else {
				if(position == 0) {
					convertView.setBackgroundResource(R.drawable.com_item_bg_up);
				} else if(position == count - 1) {
					convertView.setBackgroundResource(R.drawable.com_item_bg_down);
				} else {
					convertView.setBackgroundResource(R.drawable.com_item_bg_mid);
				}
			}
	         // divider
			if(position == count - 1){
			    holder.divider.setVisibility(View.INVISIBLE);
			}else{
			    holder.divider.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}
	
	private static final String USELESS_WHITESPACE = "[\\x00\\t\\n\\x0B\\f\\r]+?";
	private static final String REPEATED_BLANK = " {1,}";

	private static String cleanText(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll(USELESS_WHITESPACE, "").replaceAll(REPEATED_BLANK, " ").trim();
	}

}
