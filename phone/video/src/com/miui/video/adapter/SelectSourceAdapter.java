package com.miui.video.adapter;

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
import com.miui.videoplayer.adapter.AbsGroupAdapter;
import com.miui.videoplayer.download.SourceManager;

public class SelectSourceAdapter extends AbsGroupAdapter<Integer> {

	private int mCurrentSource;
	private Context mContext;
	public SelectSourceAdapter(Context context) {
		super(context);
		mContext = context;
	}

	public void setCurrentSource(int source) {
		mCurrentSource = source;
		notifyDataSetChanged();
	}
	
	public int getCurrentSource() {
		return mCurrentSource;
	}
	
	private static class ViewHolder {
		private TextView mTextView;
		private ImageView mImageView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.source_item_select, parent, false);
			holder = new ViewHolder();
			holder.mTextView = (TextView) convertView.findViewById(R.id.source_item_text);
			holder.mImageView = (ImageView) convertView.findViewById(R.id.source_item_image);
			convertView.setTag(holder);
		}
		
		final int sourceid = (Integer)getItem(position);
		if (mCurrentSource == sourceid) {
			holder.mTextView.setSelected(true);
			holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.orange));
		} else {
			holder.mTextView.setSelected(false);
			holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		final String text = DKApp.getSingleton(SourceManager.class). getSourceName(sourceid);
		holder.mTextView.setText(text);
		SourceInfo info = DKApp.getSingleton(SourceManager.class).getSourceInfo(sourceid);
		if(info != null){
			ImageUrlInfo urlInfo = new ImageUrlInfo(info.posterurl, info.md5, null);
			ImageManager.getInstance().fetchImage(ImageManager.createTask(urlInfo, null), holder.mImageView);
		}
		return convertView;
	}

}
