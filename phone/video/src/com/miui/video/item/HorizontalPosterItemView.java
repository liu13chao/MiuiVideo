package com.miui.video.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;

public class HorizontalPosterItemView extends ItemView {
	
	private ImageView mPosterImageView;
	private TextView mTitleTextView;
	private TextView mSubtitleTextView;
	private TextView mSoutheastExtraTextView;
	private TextView mSouthwestExtraTextView;
	
	public HorizontalPosterItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HorizontalPosterItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HorizontalPosterItemView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		View view = View.inflate(context, R.layout.item_view_horizontal_poster, this);
		mPosterImageView = (ImageView) view.findViewById(R.id.horizontal_item_poster);
		mTitleTextView = (TextView) view.findViewById(R.id.horizontal_item_title);
		mSubtitleTextView = (TextView) view.findViewById(R.id.horizontal_item_subtitle);
		mSoutheastExtraTextView = (TextView) view.findViewById(R.id.horizontal_item_south_east_extra);
		mSouthwestExtraTextView = (TextView) view.findViewById(R.id.horizontal_item_south_west_extra);
	}

	@Override
	public void setPoster(Bitmap bitmap) {
		mPosterImageView.setImageBitmap(bitmap);
	}

	@Override
	public void setPosterUrl(String url, String md5) {
		ImageUrlInfo info = new ImageUrlInfo(url, md5, null);
		ImageManager.getInstance().fetchImage(ImageManager.createTask(info, null), mPosterImageView);
	}

	@Override
	public void setTitle(String title) {
		mTitleTextView.setText(title);
	}

	@Override
	public void setSubtitle(String subtitle) {
		mSubtitleTextView.setText(subtitle);
	}

	@Override
	public void setSouthExtra(String extra) {
	}

	@Override
	public void setSouthwestExtra(String extra) {
		mSoutheastExtraTextView.setText(extra);
	}

	@Override
	public void setSoutheastExtra(String extra) {
		mSouthwestExtraTextView.setText(extra);
	}

}
