package com.miui.video.response;

import com.miui.video.type.MediaReview;
import com.miui.video.type.MediaReviewScore;

public class ReviewListResponse extends TvServiceResponse {
	public int count;             //评论总数
	public float averagescore;      //平均得分
	public MediaReviewScore[] scorelist;  //得分统计
	public MediaReview[] data;
}
