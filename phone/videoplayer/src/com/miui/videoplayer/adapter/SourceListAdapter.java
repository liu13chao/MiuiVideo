package com.miui.videoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.SourceInfo;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.model.MediaConfig;
import com.miui.videoplayer.model.OnlineEpisodeSource;

public class SourceListAdapter extends AbsGroupAdapter<OnlineEpisodeSource> {

	private OnlineEpisodeSource mCurrentSource;
	public SourceListAdapter(Context context) {
		super(context);
	}
	
	public void setCurrentSource(OnlineEpisodeSource source) {
		mCurrentSource = source;
		notifyDataSetChanged();
	}
	
	public OnlineEpisodeSource getCurrentSource() {
		return mCurrentSource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.vp_list_item_source, parent, false);
			holder = new ViewHolder();
			holder.mTextView = (TextView) convertView.findViewById(R.id.vp_source_item_text);
			holder.mImageView = (ImageView) convertView.findViewById(R.id.vp_source_item_image);
			convertView.setTag(holder);
		}
		if (!(getItem(position) instanceof OnlineEpisodeSource)) {
			return convertView;
		}
		final OnlineEpisodeSource item = (OnlineEpisodeSource) getItem(position);
		if (mCurrentSource != null && mCurrentSource.getSource() == item.getSource()
				&& mCurrentSource.getResolution() == item.getResolution()) {
			holder.mTextView.setSelected(true);
		} else {
			holder.mTextView.setSelected(false);
		}
		final String text = DKApp.getSingleton(SourceManager.class). getSourceName(
		        item.getSource()) + MediaConfig.getResolutionName(getContext(), item.getResolution());
		holder.mTextView.setText(text);
		SourceInfo info = DKApp.getSingleton(SourceManager.class).getSourceInfo(item.getSource());
		if(info != null){
			ImageUrlInfo urlInfo = new ImageUrlInfo(info.posterurl, info.md5, null);
			ImageManager.getInstance().fetchImage(ImageManager.createTask(urlInfo, null), holder.mImageView);
		}
		return convertView;
	}

	private static class ViewHolder {
		private TextView mTextView;
		private ImageView mImageView;
	}

}
