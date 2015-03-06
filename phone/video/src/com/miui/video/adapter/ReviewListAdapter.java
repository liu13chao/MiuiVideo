package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.MediaReview;
import com.miui.video.widget.RatingView;

/**
 *@author tangfuling
 *
 */

public class ReviewListAdapter extends BaseGroupAdapter<MediaReview> {
	
	public ReviewListAdapter(Context context) {
		super(context);
	}

	private static class ViewHolder {
		TextView user;
		TextView comment;
		RatingView ratingView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView != null && convertView.getTag() != null
				&& convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.detail_review_list_item, null);
			holder.comment = (TextView) convertView.findViewById(R.id.detail_review_list_comment);
			holder.user = (TextView) convertView.findViewById(R.id.detail_review_list_user);
			holder.ratingView = (RatingView) convertView
					.findViewById(R.id.detail_review_list_rating);
			convertView.setTag(holder);
		}
		
		MediaReview review = getItem(position);
		holder.comment.setText(review.filmreview);
		holder.user.setText(mContext.getString(R.string.xiaomi_user) + " "
				+ review.userid);
		holder.ratingView.setScore(review.score);

		return convertView;
	}
}
