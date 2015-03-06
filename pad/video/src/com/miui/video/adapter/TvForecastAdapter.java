package com.miui.video.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.miui.video.R;
import com.miui.video.type.TelevisionShow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TvForecastAdapter extends BaseGroupAdapter<TelevisionShow> {

	private Context mContext;
	
	public TvForecastAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	private class ViewHolder{
		TextView title;
		TextView subTitle;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if( convertView == null){
			convertView = View.inflate(mContext, R.layout.tv_forecast_item, null);
			viewHolder = new ViewHolder();
			convertView.setTag(viewHolder);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_forecast_item_title);
			viewHolder.subTitle = (TextView) convertView.findViewById(R.id.tv_forecast_item_sub_title);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		TelevisionShow curTvShow = getItem(position);
		if(curTvShow != null) {
			String strTime = curTvShow.videostarttime +"000";
			String tvShowTime = new SimpleDateFormat("HH:mm", Locale.CHINA).format(
					new Date(Long.parseLong(strTime)));
			String tvShowName = curTvShow.videoname;
			viewHolder.title.setText(tvShowName);
			viewHolder.subTitle.setText(tvShowTime);
			
			if(position == 0) {
				viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.p_90_blue));
				viewHolder.subTitle.setTextColor(mContext.getResources().getColor(R.color.p_30_blue));
			} else {
				viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.p_90_white));
				viewHolder.subTitle.setTextColor(mContext.getResources().getColor(R.color.p_30_white));
			}
		}
		return convertView;
	}
}
