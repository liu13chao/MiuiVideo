package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.TelevisionInfo;

public class TvSelectTvAdapter extends BaseGroupAdapter<TelevisionInfo> {

	private Context mContext;
	private TelevisionInfo mCurTvInfo;
	
	public TvSelectTvAdapter(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setCurTvInfo(TelevisionInfo tvInfo) {
		this.mCurTvInfo = tvInfo;
	}
	
	private class ViewHolder{
		TextView tvName;
		TextView tvPrograme;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if( convertView == null){
			convertView = View.inflate(mContext, R.layout.tv_select_tv_item, null);
			viewHolder = new ViewHolder();
			convertView.setTag(viewHolder);
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_select_tv_item_title);
			viewHolder.tvPrograme = (TextView)  convertView.findViewById(R.id.tv_select_tv_item_sub_title);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		TelevisionInfo curTelevisionInfo = getItem(position);
		if(curTelevisionInfo != null) {
			String tvName = curTelevisionInfo.getChannelName();
			viewHolder.tvName.setText(tvName);
			if(curTelevisionInfo.getCurrentShow() != null) {
				String tvProgramme = curTelevisionInfo.getCurrentShow().videoname;
				viewHolder.tvPrograme.setText(tvProgramme);
			}
			
			if(mCurTvInfo != null && mCurTvInfo.getChannelId() == curTelevisionInfo.getChannelId()) {
				viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.p_90_blue));
				viewHolder.tvPrograme.setTextColor(mContext.getResources().getColor(R.color.p_30_blue));
			} else {
				viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.p_90_white));
				viewHolder.tvPrograme.setTextColor(mContext.getResources().getColor(R.color.p_30_white));
			}
		}
		
		return convertView;
	}
}
