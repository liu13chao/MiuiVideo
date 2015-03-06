package com.miui.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.InformationData;

public class InfoListAdapter extends BaseGroupAdapter<InformationData> {

	private Context context;
	private int mSelection = -1;
	Bitmap mDefaultPosterCache = null;
	
	public InfoListAdapter(Context context) {
		super(context);
		this.context = context;
		createDefaultPoster();
	}
	
	private void createDefaultPoster(){
		int width = context.getResources().getDimensionPixelSize(R.dimen.info_channel_list_poster_width);
		int height = context.getResources().getDimensionPixelSize(R.dimen.info_channel_list_poster_height);
		mDefaultPosterCache = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		Canvas canvas = new Canvas(mDefaultPosterCache);
		NinePatchDrawable npd = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.default_border_right_angle);
		npd.setBounds(0, 0, width, height);
		npd.draw(canvas);
		BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.default_poster_media);
		canvas.drawBitmap(bd.getBitmap(), (width - bd.getMinimumWidth()) / 2, (height - bd.getMinimumHeight()) / 2, null);
	}
	
	private class ViewHolder {
		ImageView poster;
		TextView duration;
		TextView name;
		TextView desc;
		View line;
	}
	
	public void setSelection(int selection){
		mSelection = selection;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			View view = View.inflate(context, R.layout.info_list_item, null);
			viewHolder.poster = (ImageView) view.findViewById(R.id.info_list_item_poster);
			viewHolder.name = (TextView) view.findViewById(R.id.info_list_item_name);
			viewHolder.desc = (TextView) view.findViewById(R.id.info_list_item_desc);
			viewHolder.duration = (TextView) view.findViewById(R.id.info_list_item_duration);
			viewHolder.line = view.findViewById(R.id.info_list_item_line);
			convertView = view;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(mSelection == position){
			viewHolder.name.setTextColor(0xfff95f22);
		}else{
			viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.text_color_deep_dark));
		}
		InformationData informationData = getItem(position);
		viewHolder.name.setText(informationData.medianame);
		String desc = context.getResources().getString(R.string.play_count_ci);
		desc = String.format(desc, informationData.playcount);
		viewHolder.desc.setText(desc);
		if(informationData.playcount == 0) {
		    viewHolder.desc.setVisibility(View.GONE);
		} else {
		    viewHolder.desc.setVisibility(View.VISIBLE);
		}
		viewHolder.duration.setText(informationData.getDesc());
		ImageManager imageManager = ImageManager.getInstance();
		ImageUrlInfo posterInfo = informationData.getPosterInfo();
		if(!ImageManager.isUrlDone(posterInfo, viewHolder.poster)) {
		    viewHolder.poster.setImageBitmap(mDefaultPosterCache);
		    imageManager.fetchImage(ImageManager.createTask(posterInfo, null), viewHolder.poster);
		}
		int size = getCount();
		if(size == 1) {
			convertView.setBackgroundResource(R.drawable.com_item_bg_full);
			viewHolder.line.setVisibility(View.INVISIBLE);
		} else {
			if(position == 0) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_up);
				viewHolder.line.setVisibility(View.VISIBLE);
			} else if(position == size - 1) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_down);
				viewHolder.line.setVisibility(View.INVISIBLE);
			} else {
				convertView.setBackgroundResource(R.drawable.com_item_bg_mid);
				viewHolder.line.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}
}
