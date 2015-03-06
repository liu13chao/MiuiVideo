package com.miui.video.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;

public class VerticalPosterItemView extends ItemView {
	
	private ImageView mPosterImageView;
	private TextView mTitleTextView;
	private TextView mSubtitleTextView;
	private TextView mSouthExtraTextView;
	private TextView mSouthEastExtraTextView;
	private ImageView mFlagImageView;
	
	public VerticalPosterItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VerticalPosterItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public VerticalPosterItemView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		View view = View.inflate(context, R.layout.item_view_vertical_poster, this);
		mPosterImageView = (ImageView) view.findViewById(R.id.vertical_item_poster);
		mTitleTextView = (TextView) view.findViewById(R.id.vertical_item_title);
		mSubtitleTextView = (TextView) view.findViewById(R.id.vertical_item_subtitle);
		mSouthExtraTextView = (TextView) view.findViewById(R.id.vertical_item_south_extra);
		mSouthEastExtraTextView = (TextView) view.findViewById(R.id.vertical_item_southeast_extra);
		mFlagImageView = (ImageView) view.findViewById(R.id.vertical_item_flag);
	}

	@Override
	public void setPoster(Bitmap bitmap) {
		mPosterImageView.setImageBitmap(bitmap);
	}

	@Override
	public void setPosterUrl(String url, String md5) {
		ImageUrlInfo info = new ImageUrlInfo(url, md5, null);
		if(!ImageManager.isUrlDone(info, mPosterImageView)){
			mPosterImageView.setImageResource(R.drawable.transparent);
			ImageManager.getInstance().fetchImage(ImageManager.createTask(info, null), mPosterImageView);
		}
	}
	
	public void setPosterBackgroud(Drawable d) {
		mPosterImageView.setBackground(d);
	}
	
	public void setPosterBackgroud(int resId) {
		mPosterImageView.setBackgroundResource(resId);
	}

	public void setPosterBackgroudColor(int color) {
		mPosterImageView.setBackgroundColor(color);
	}
	
	@Override
	public void setTitle(String title) {
		mTitleTextView.setText(title);
	}

	@Override
	public void setSubtitle(String subtitle) {
		if(TextUtils.isEmpty(subtitle)){
			mSubtitleTextView.setVisibility(View.GONE);
		}else{
			mSubtitleTextView.setText(subtitle);
			mSubtitleTextView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setSouthExtra(String extra) {
		if(TextUtils.isEmpty(extra)){
			mSouthExtraTextView.setVisibility(View.INVISIBLE);
		}else{
			mSouthExtraTextView.setVisibility(View.VISIBLE);
			mSouthExtraTextView.setText(extra);
		}
	}

	@Override
	public void setSouthwestExtra(String extra) {
	}

	@Override
	public void setSoutheastExtra(String extra) {
		if (TextUtils.isEmpty(extra)) {
			mSouthEastExtraTextView.setVisibility(View.INVISIBLE);
		} else{
			mSouthEastExtraTextView.setVisibility(View.VISIBLE);
			mSouthEastExtraTextView.setText(extra);
		}
	}

	public void setFlag(int resId) {
		mFlagImageView.setImageResource(resId);
	}
	
	public void setFlag(Drawable d) {
		mFlagImageView.setImageDrawable(d);
	}

}
