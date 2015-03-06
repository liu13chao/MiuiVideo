
package com.miui.video.util;

import android.view.View;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.ActorsView;
import com.miui.video.widget.RatingView;
import com.miui.video.widget.media.MediaView;

/**
 *@author tangfuling
 *
 */

public class UIUtil {
	
	public static void fillMediaSummary(View view, Object media) {
		if (view == null) {
			return;
		}
		if (media instanceof MediaInfo) {
			MediaInfo mediaInfo = (MediaInfo) media;
			MediaView mediaView = (MediaView) view
					.findViewById(R.id.media_summary_media_view);
			mediaView.setContentInfo(mediaInfo);
			mediaView.setClickable(true);
			
			View panel = null;
			
			panel = view.findViewById(R.id.media_summary_title);
			if(panel != null) {
				if(Util.isEmpty(mediaInfo.medianame)) {
					panel.setVisibility(View.GONE);
				} else {
					panel.setVisibility(View.VISIBLE);
					TextView title = (TextView) panel;
					title.setText(mediaInfo.medianame);
				}
			}
			
			RatingView ratingView = (RatingView) view.findViewById(R.id.media_summary_rating);
			ratingView.setScore(mediaInfo.score);
			
			panel = view.findViewById(R.id.media_summary_director_panel);
			if (panel != null) {
				if (Util.isEmpty(mediaInfo.director)) {
					panel.setVisibility(View.GONE);
				} else {
					panel.setVisibility(View.VISIBLE);
					TextView director = (TextView) panel
							.findViewById(R.id.media_summary_director);
					director.setText(mediaInfo.director);
				}
			}
			
			panel = view.findViewById(R.id.media_summary_actors_panel);
			if (panel != null) {
				if (Util.isEmpty(mediaInfo.actors)) {
					panel.setVisibility(View.GONE);
				} else {
					panel.setVisibility(View.VISIBLE);
					
					View vActors = panel.findViewById(R.id.media_summary_actors);
					if( vActors instanceof TextView) {
						TextView actors = (TextView) vActors;
						actors.setText(mediaInfo.actors);
					} else {
						ActorsView actorsView = (ActorsView) vActors;
						actorsView.setActors(mediaInfo.actors);
					}
				}
			}
			
			panel = view.findViewById(R.id.media_summary_area_panel);
			if (panel != null) {
				if (Util.isEmpty(mediaInfo.area)) {
					panel.setVisibility(View.GONE);
				} else {
					panel.setVisibility(View.VISIBLE);
					TextView area = (TextView) panel.findViewById(R.id.media_summary_area);
					area.setText(mediaInfo.area);
				}
			}
		}
	}
}
