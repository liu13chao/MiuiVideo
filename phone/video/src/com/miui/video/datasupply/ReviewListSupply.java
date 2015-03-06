package com.miui.video.datasupply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;

import com.miui.video.api.DKApi;
import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.response.ReviewListResponse;
import com.miui.video.type.MediaReview;
import com.miui.video.type.MediaReviewScore;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class ReviewListSupply implements Observer {
	
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, ArrayList<MediaReview>> reviewListMap = new HashMap<Integer, ArrayList<MediaReview>>();
	private int commentTotalCount;
	private float avgScore;
	private float[] scorePercents;
	private boolean canLoadMore = true;
	private int pageSize;
	private int reviewType = ReviewTypeValueDef.REVIEW_TYPE_ALL;
	
	private ArrayList<ReviewListListener> listeners = new ArrayList<ReviewListListener>();
	private ServiceRequest request;
	
	public void getReviewList(int mediaId, int pageNo, int pageSize, int reviewType) {
		this.pageSize = pageSize;
		this.reviewType = reviewType;
		this.canLoadMore = true;
		if(pageNo == 1) {
			resetMapList();
		}
		
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getReviewList(mediaId, pageNo, pageSize, reviewType, this);
	}
	
	public void getReviewListUser(int mediaId, int pageNo, int pageSize, int reviewType) {
		this.pageSize = pageSize;
		this.reviewType = reviewType;
		this.canLoadMore = true;
		if(pageNo == 1) {
			resetMapList();
		}
		
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getReviewListUser(mediaId, pageNo, pageSize, reviewType, this);
	}
	
	public void addListener(ReviewListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(ReviewListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	public ArrayList<MediaReview> getReviewList(int reviewType) {
		ArrayList<MediaReview> resList = new ArrayList<MediaReview>();
		List<MediaReview> list = reviewListMap.get(reviewType);
		if(list != null) {
			resList.addAll(list);
		}
		return resList;
	}
	
	public int getTotalCount() {
		return commentTotalCount;
	}
	
	public float getAvgScore() {
		return avgScore;
	}
	
	public float[] getScorePercent() {
		return scorePercents;
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			ReviewListResponse reviewListResponse = (ReviewListResponse) response;
			if(reviewListResponse.data != null) {
				commentTotalCount = reviewListResponse.count;
				avgScore = reviewListResponse.averagescore;
				buildScorePercent(reviewListResponse.scorelist);
				if(reviewListResponse.data.length < pageSize) {
					canLoadMore = false;
				} 
				ArrayList<MediaReview> list = reviewListMap.get(reviewType);
				if(list == null) {
					list = new ArrayList<MediaReview>();
					reviewListMap.put(reviewType, list); 
				}
				for(int i = 0; i < reviewListResponse.data.length; i++) {
					list.add(reviewListResponse.data[i]);
				}
			} else {
				canLoadMore = false;
			}
			onReviewListDone(false);
		} else {
			onReviewListDone(true);
		}
	}

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
		
	}
	
	//packaged method
	private void onReviewListDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			ReviewListListener listener = listeners.get(i);
			if(listener != null) {
				listener.onReviewListDone(isError, canLoadMore);
			}
		}
	}
	
	private void resetMapList() {
		List<MediaReview> list = reviewListMap.get(reviewType);
		if(list != null) {
			list.clear();
		}
	}
	
	private void buildScorePercent(MediaReviewScore[] mediaReviewScores) {
		if(mediaReviewScores != null) {
			scorePercents = new float[mediaReviewScores.length];
			for(int i = 0; i < mediaReviewScores.length; i++) {
				MediaReviewScore mediaReviewScore = mediaReviewScores[i];
				if(mediaReviewScore != null) {
					float percent = mediaReviewScore.count / (float) commentTotalCount;
					int starCount = mediaReviewScore.score / 2;
					if(starCount <= scorePercents.length && starCount > 0) {
						scorePercents[starCount - 1] = percent * 100;
					}
				}
			}
		}
	}

	//self def class
	public interface ReviewListListener {
		public void onReviewListDone(boolean isError, boolean canLoadMore);
	}
}
