package com.miui.videoplayer.adapter;

import java.util.ArrayList;
import java.util.List;
import com.miui.video.R;
import com.miui.video.adapter.BaseGroupAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.model.loader.BootInfoLoader;
import com.miui.video.type.BootResponseInfo;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.type.SourceInfo;
import com.miui.videoplayer.VideoPlayerActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SourceListAdapter extends BaseGroupAdapter<MediaUrlInfo> {

	private Context mContext;
	private List<MediaUrlInfo> mMediaUrlInfos = new ArrayList<MediaUrlInfo>();
	private BootInfoLoader mBootInfoLoader = new BootInfoLoader();
	
	public SourceListAdapter(Context context) {
		super(context);
		this.mContext = context;
		mBootInfoLoader.load();
	}
	
	public void setData(MediaUrlInfoList mediaUrlInfoList) {
		buildMediaUrlInfos(mediaUrlInfoList);
		refresh();
	}
	
	//packaged method
	private void buildMediaUrlInfos(MediaUrlInfoList mediaUrlInfoList) {
		mMediaUrlInfos.clear();
		if(mediaUrlInfoList == null) {
			return;
		}
		
		if(mediaUrlInfoList.urlSuper != null) {
			int clarity = MediaConstantsDef.CLARITY_SUPPER;
			MediaUrlInfo[] urlSuper = mediaUrlInfoList.urlSuper;
			for(int i = 0; i < urlSuper.length; i++) {
				MediaUrlInfo mediaUrlInfo = urlSuper[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = clarity;
					mMediaUrlInfos.add(mediaUrlInfo);
				}
			}
		}
		if(mediaUrlInfoList.urlHigh != null) {
			int clarity = MediaConstantsDef.CLARITY_HIGH;
			MediaUrlInfo[] urlHigh = mediaUrlInfoList.urlHigh;
			for(int i = 0; i < urlHigh.length; i++) {
				MediaUrlInfo mediaUrlInfo = urlHigh[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = clarity;
					mMediaUrlInfos.add(mediaUrlInfo);
				}
			}
		}
		if(mediaUrlInfoList.urlNormal != null) {
			int clarity = MediaConstantsDef.CLARITY_NORMAL;
			MediaUrlInfo[] urlNormal = mediaUrlInfoList.urlNormal;
			for(int i = 0; i < urlNormal.length; i++) {
				MediaUrlInfo mediaUrlInfo = urlNormal[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = clarity;
					mMediaUrlInfos.add(mediaUrlInfo);
				}
			}
		}
	}
	
	private class ViewHolder {
		private TextView title;
	}
	
	@Override
	public MediaUrlInfo getItem(int position) {
		if(position < 0 || position >= mMediaUrlInfos.size()) {
			return null;
		}
		return mMediaUrlInfos.get(position);
	}
	
	@Override
	public int getCount() {
		return mMediaUrlInfos.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null) {
			View view = View.inflate(mContext, R.layout.vp_popup_ctrl_source_selection_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.source_title);
			
			view.setTag(viewHolder);
			convertView = view;
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MediaUrlInfo mediaUrlInfo = mMediaUrlInfos.get(position);
		if(mediaUrlInfo != null) {
			StringBuilder sb = new StringBuilder();
			String sourceStr = mContext.getResources().getString(R.string.unknown_source);
			int source = mediaUrlInfo.mediaSource;
			BootResponseInfo bootResponseInfo = mBootInfoLoader.getBootResponseInfo();
			SourceInfo sourceInfo = null;
			if(bootResponseInfo != null) {
				sourceInfo =bootResponseInfo.getSourceInfo(source);
			}
			if(sourceInfo != null) {
				sourceStr = sourceInfo.name;
			}
			sb.append(sourceStr);
			sb.append("-");
			String clarityStr = mContext.getResources().getString(R.string.clarity_unknown);
			int clarity = mediaUrlInfo.clarity;
			if(clarity == MediaConstantsDef.CLARITY_SUPPER) {
				clarityStr = mContext.getResources().getString(R.string.clarity_supper);
			} else if(clarity == MediaConstantsDef.CLARITY_HIGH) {
				clarityStr = mContext.getResources().getString(R.string.clarity_high);
			} else if(clarity == MediaConstantsDef.CLARITY_NORMAL) {
				clarityStr = mContext.getResources().getString(R.string.clarity_standard);
			}
			sb.append(clarityStr);
			viewHolder.title.setText(sb.toString());
			
			if (VideoPlayerActivity.curClarity == clarity && VideoPlayerActivity.curMediaSource == source) {
				viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.vp_90_blue));
			} else {
				viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.vp_90_white));
			}
		}
		return convertView;
	}
}