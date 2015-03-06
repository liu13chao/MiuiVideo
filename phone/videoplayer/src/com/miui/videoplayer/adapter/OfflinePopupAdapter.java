package com.miui.videoplayer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;
import com.miui.video.offline.OfflineMedia;
import com.miui.videoplayer.widget.OfflineStatusView;

public class OfflinePopupAdapter extends BaseGroupAdapter<OfflineMedia>{

	Drawable mDone;
	
	public OfflinePopupAdapter(Context context) {
		super(context);
		mDone = context.getResources().getDrawable(R.drawable.offline_finish_icon);
		mDone.setBounds(0, 0, mDone.getIntrinsicWidth(), mDone.getIntrinsicHeight());
	}

	private class ViewHolder {
		private TextView title;
		private TextView status;
		private OfflineStatusView statusIcon;
		private OfflineMedia mMedia;
	}
	
	private OfflineMediaChangedListener mOfflineMediaListener = null;
	public void setOfflineMediaChangedListener(OfflineMediaChangedListener listener){
		mOfflineMediaListener = listener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		OfflineMedia media = getItem(position);
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.vp_popup_offline_media, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.offline_media_name);
			viewHolder.status = (TextView) convertView.findViewById(R.id.offline_media_status);
			viewHolder.statusIcon = (OfflineStatusView) convertView.findViewById(R.id.offline_media_status_icon);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mOfflineMediaListener != null){
						mOfflineMediaListener.onOfflineMediaChanged(((ViewHolder)v.getTag()).mMedia);
					}
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mMedia = media;
		viewHolder.title.setText(String.format(mContext.getResources().getString(R.string.vp_offline_title_name), 
				getItem(position).episode));
		if (media == null || media.isError()) {
			viewHolder.status.setText(R.string.vp_offline_status_fail);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_ERROR);
		} else if (media.isLoading()) {
			viewHolder.status.setText(R.string.vp_offline_status_dowloading);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_DOWLOADING);
			viewHolder.statusIcon.setProgress((float)media.completeSize / media.fileSize);
		} else if (media.isPaused()) {
			viewHolder.status.setText(R.string.vp_offline_status_pause);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_PAUSE);
		} else if (media.isWaiting()) {
			viewHolder.status.setText(R.string.vp_offline_status_wait);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_WAIT);
		} else if(media.isFinished()){
			viewHolder.status.setText(R.string.vp_offline_status_done);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_DONE);
		} else{
			viewHolder.status.setText(R.string.vp_offline_status_none);
			viewHolder.statusIcon.setStatus(OfflineStatusView.OFFLINE_STATUS_NONE);
		}
		return convertView;
	}

	public interface OfflineMediaChangedListener{
		public void onOfflineMediaChanged(OfflineMedia media);
	}
	
}
