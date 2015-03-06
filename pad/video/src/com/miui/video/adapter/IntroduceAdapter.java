package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.MediaDetailInfo;

/**
 *@author tangfuling
 *
 */

public class IntroduceAdapter extends BaseGroupAdapter<MediaDetailInfo> {

	private Context mContext;
	private MediaDetailInfo mMediaDetailInfo;
	
	private String mTime;
	private String mType;
	private String mIntroduce;
	
	public void setData(MediaDetailInfo mediaDetailInfo) {
		this.mMediaDetailInfo = mediaDetailInfo;
		if(this.mMediaDetailInfo != null) {
			mIntroduce = mMediaDetailInfo.desc;
			mType = mType +mMediaDetailInfo.allcategorys;
			
			String issuedate = mMediaDetailInfo.issuedate;
			if(issuedate != null && !issuedate.contains("1970-01-01")) {
				mTime = mTime + issuedate;
			}

			notifyDataSetChanged();
		}
	}
	
	public IntroduceAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.mTime = context.getResources().getString(R.string.time_colon);
		this.mType = context.getResources().getString(R.string.type_colon);
	}
	
	@Override
	public int getCount() {
		if(mMediaDetailInfo != null) {
			return 1;
		}
		return 0;
	}
	
	private class ViewHolder {
		public TextView time;
		public TextView type;
		public TextView introduce;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.detail_introduce, null);
			viewHolder = new ViewHolder();;
			viewHolder.time = (TextView) convertView.findViewById(R.id.detail_introduce_time);
			viewHolder.type = (TextView) convertView.findViewById(R.id.detail_introduce_type);
			viewHolder.introduce = (TextView) convertView.findViewById(R.id.detail_introduce);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.time.setText(mTime);
		viewHolder.type.setText(mType);
		viewHolder.introduce.setText(mIntroduce);
		return convertView;
	}

}
