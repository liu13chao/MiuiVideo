package com.miui.video.widget.detail;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.MediaReview;
import com.miui.video.widget.RatingView;

public class DetailCommentReviewView extends LinearLayout {
	
	private Context mContext;

	public DetailCommentReviewView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailCommentReviewView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setMediaReviews(List<MediaReview> mediaReviews) {
		removeAllViews();
		if(mediaReviews == null || mediaReviews.size() == 0) {
			return;
		}
		for(int i = 0; i < mediaReviews.size(); i++) {
			View contentView = View.inflate(mContext, R.layout.detail_review_list_item, null);
			addView(contentView);
			
			View divider = contentView.findViewById(R.id.detail_review_list_divider);
			TextView comment = (TextView) contentView.findViewById(R.id.detail_review_list_comment);
			TextView user = (TextView) contentView.findViewById(R.id.detail_review_list_user);
			RatingView ratingView = (RatingView) contentView.findViewById(R.id.detail_review_list_rating);
			MediaReview review = mediaReviews.get(i);
			if(review != null) {
				comment.setText(review.filmreview);
				user.setText(mContext.getString(R.string.xiaomi_user) + " "
						+ review.userid);
				ratingView.setScore(review.score);
			}
			if(i == mediaReviews.size() - 1) {
				divider.setVisibility(View.INVISIBLE);
			}
		}
	}

	//init
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
	}
}
