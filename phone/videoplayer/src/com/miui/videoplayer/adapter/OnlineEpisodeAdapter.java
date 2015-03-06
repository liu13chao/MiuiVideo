/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlineEpisodeAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-9
 */

package com.miui.videoplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;
import com.miui.videoplayer.common.Constants;
import com.miui.videoplayer.model.Episode;
import com.miui.videoplayer.model.OnlineEpisode;

/**
 * @author tianli
 *
 */
public class OnlineEpisodeAdapter extends BaseGroupAdapter<Episode> {

	private int mSelectedEpisode;
	
	public OnlineEpisodeAdapter(Context context) {
		super(context);
	}
	
	public void setSelectedEpisode(int selectedEpisode) {
		this.mSelectedEpisode = selectedEpisode;
	}

	private class ViewHolder {
		private TextView  title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.vp_popup_ci_selection_item, null);
			vHolder.title = (TextView) convertView.findViewById(R.id.vp_popup_ci_selection);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		Episode episode = getItem(position);
		if(episode instanceof OnlineEpisode) {
			OnlineEpisode onlineEpisode = (OnlineEpisode) episode;
			int color = 0;
			if(mSelectedEpisode == onlineEpisode.getCi()){
				color = mContext.getResources().getColor(R.color.vp_select_color);
			}else{
				color = mContext.getResources().getColor(R.color.vp_90_white);
			}
			vHolder.title.setTextColor(color);
			String str = onlineEpisode.getName();
			if(onlineEpisode.getMediaStyle() == Constants.MEDIA_TYPE_SERIES) {
				str = mContext.getResources().getString(R.string.episode_suffix);
				str = String.format(str, onlineEpisode.getCi());
			}
			vHolder.title.setText(str);
		}
		return convertView;
	}
}
