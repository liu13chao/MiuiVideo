/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKApi.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-20
 */
package com.miui.video.api;

import java.util.List;

import com.miui.video.request.AddonListRequest;
import com.miui.video.request.BannerListRequest;
import com.miui.video.request.CategoryListRequest;
import com.miui.video.request.ChannelListRequest;
import com.miui.video.request.ChannelRecommendationRequest;
import com.miui.video.request.FilterMediaInfoRequest;
import com.miui.video.request.CommentRequest;
import com.miui.video.request.DeleteMyFavoriteRequest;
import com.miui.video.request.GetCmccKeyRequest;
import com.miui.video.request.GetMediaInfoByH5UrlRequest;
import com.miui.video.request.GetMyFavoriteRequest;
import com.miui.video.request.GetNickNameRequest;
import com.miui.video.request.GetSecurityTokenRequest;
import com.miui.video.request.GetUpdateApkRequest;
import com.miui.video.request.InfoRecommendRequest;
import com.miui.video.request.InformationListRequest;
import com.miui.video.request.LoginRequest;
import com.miui.video.request.MediaDetailInfoRequest;
import com.miui.video.request.MediaRecommendRequest;
import com.miui.video.request.MediaUrlInfoListRequest;
import com.miui.video.request.PlayHistoryRequest;
import com.miui.video.request.RankInfoListRequest;
import com.miui.video.request.ReviewListRequest;
import com.miui.video.request.ReviewListUserRequest;
import com.miui.video.request.SearchMediaInfoRequest;
import com.miui.video.request.SetMyFavoriteRequest;
import com.miui.video.request.SetPlayInfoRequest;
import com.miui.video.request.SpecialSubjectListRequest;
import com.miui.video.request.SpecialSubjectMediaRequest;
import com.miui.video.request.TelevisionProgramsAssembleRequest;
import com.miui.video.request.TelevisionRecommendRequest;
import com.miui.video.request.TelevisionShowInfoRequest;
import com.miui.video.request.TvServiceRequest;
import com.miui.video.request.UploadComUserDataRequest;
import com.miui.video.request.UploadIMEBootInfoRequest;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.FavoriteItem;
import com.miui.video.type.SearchInfo;
import com.miui.video.type.UserDataParam;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 * 
 */
public class DKApi {

	public static final String TAG = DKApi.class.getName();

	// public static boolean getSecurityToken() {
	// }

	// public static synchronized boolean needAuthToken() {
	// TokenInfo tokenInfo = DataStore.getInstance().getToken();
	// String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
	// if (tokenInfo == null || Util.isEmpty(tokenInfo.secretToken)
	// || !uid.equals(tokenInfo.uid)) {
	// tokenInfo.secretToken = null;
	// tokenInfo.secretKey = null;
	// tokenInfo.uid = uid;
	// return true;
	// }
	// return false;
	// }
	
	// mediaNameSearchType
	public static final int SEARCH_MEDIA_BY_PY = 1; // 按名字搜索除 MV 外的视频
	public static final int SEARCH_MV_BY_PY = 2; // 按名字搜索 MV
	public static final int SEARCH_CINEASTE_BY_PY = 3; // 按人名搜索除 MV 外的影人
	public static final int SEARCH_SINGER_BY_PY = 4; // 按歌手名搜索 MV影人

	public static final int SEARCH_MEDIA_BY_FUZZY_PY = 101; // 按T9键盘名字搜索除 MV
															// 外的视频
	public static final int SEARCH_MV_BY_FUZZY_PY = 102; // 按T9键盘名字搜索 MV
	public static final int SEARCH_CINEASTE_BY_FUZZY_PY = 103; // 按T9键盘人名搜索除 MV
																// 外的影人
	public static final int SEARCH_SINGER_BY_FUZZY_PY = 104; // 按T9键盘歌手名搜索 MV影人

	public static final int SEARCH_MEDIA_BY_KEYWORD = 1001; // 按关键字搜索除 MV 外的视频
	public static final int SEARCH_MV_BY_KEYWORD = 1002; // 按关键字搜索 MV
	public static final int SEARCH_CINEASTE_BY_KEYWORD = 1003; // 按关键字搜索除 MV
																// 外的影人
	public static final int SEARCH_SINGER_BY_KEYWORD = 1004; // 按关键字搜索 MV 影人

	public static final int SEARCH_MOBILE_BY_KEYWORD = 1101; // 搜索热门关键字
	public static final int SEARCH_CHANNEL_SUMMARY_BY_KEYWORD = 1102; // 搜索结果按分类给出
	
	//nSearchMask 参数
	public static final int SEARCH_MASK_ALL	= 0;			// 全部
	public static final int	SEARCH_MASK_NAME = 1;			// 按名字搜索
	public static final int SEARCH_MASK_DIRECTOR = 2;			// 按导演搜索
	public static final int SEARCH_MASK_ACTOR = 4;			  // 按演员/歌手/主持搜索
	public static final int SEARCH_MASK_MOVIE = 1024;		  // 在电影中搜索
	public static final int SEARCH_MASK_TV = 2048;		      // 在电视剧中搜索
	public static final int SEARCH_MASK_CARTOON = 4096;		  // 在动漫中搜索
	public static final int SEARCH_MASK_SYNTHESIS = 8192;		// 在综艺中搜索
	public static final int SEARCH_MASK_DOCUMENTARY = 16384;		// 在纪录片中搜索
	public static final int SEARCH_MASK_MUSIC_VIDEO = 32768;		// 在音乐中搜索
	public static final int SEARCH_MASK_EDUCATION = 65536;		// 在教育中搜索
	
	// ordertype of GetMediaInfoList
	public static final int ORDER_BY_UPDATETIME = 0;
	public static final int ORDER_BY_HOT = 1;
	public static final int ORDER_BY_ISSUEDATE = 2;
	public static final int ORDER_BY_UPDATETIME_ASC = 3;
	public static final int ORDER_BY_HOT_ASC = 4;
	public static final int ORDER_BY_ISSUEDATE_ASC = 5;
	public static final int ORDER_BY_SCORE_DESC = 6;
	public static final int ORDER_BY_SCORE_ASC = 7;

	public static TvServiceRequest userLogin(Observer observer) {
		LoginRequest request = new LoginRequest();
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static ServiceResponse userLoginSync() {
		LoginRequest request = new LoginRequest();
		return request.execSync();
	}

	public static ServiceResponse getSecurityTokenSync() {
		GetSecurityTokenRequest request = new GetSecurityTokenRequest();
		return request.execSync();
	}

	public static TvServiceRequest getBannerList(int channelID,
			String statisticInfo, Observer observer) {
		BannerListRequest request = new BannerListRequest(channelID,
				statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static ServiceRequest getAddonList(int pageNo, int pageSize, String statisticInfo, Observer observer) {
		AddonListRequest request = new AddonListRequest(pageNo, pageSize, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getCategoryList(int channelID, Observer observer) {
		CategoryListRequest request = new CategoryListRequest(channelID);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getChannelList(int channelID, Observer observer) {
		ChannelListRequest request = new ChannelListRequest(channelID);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getChannelRecommendation(MediaInfoQuery query,
			boolean isMultiChannel, Observer observer) {
		ChannelRecommendationRequest request = new ChannelRecommendationRequest(
				query, isMultiChannel);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getMediaDetailInfo(int mediaID,
			boolean getAll, int fee, String statisticInfo, Observer observer) {
		MediaDetailInfoRequest request = new MediaDetailInfoRequest(mediaID,
				getAll, fee, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getMediaRecommend(int mediaID,
			Observer observer) {
		MediaRecommendRequest request = new MediaRecommendRequest(mediaID);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getReviewList(int mediaID, int pageNo,
			int pageSize, int reviewType, Observer observer) {
		ReviewListRequest request = new ReviewListRequest(mediaID, pageNo,
				pageSize, reviewType);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getReviewListUser(int mediaID, int pageNo,
			int pageSize, int reviewType, Observer observer) {
		ReviewListUserRequest request = new ReviewListUserRequest(mediaID, pageNo,
				pageSize, reviewType);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getRankInfoList(int channelID, int pageNo, int pageSize, String statisticInfo, Observer observer) {
		RankInfoListRequest request = new RankInfoListRequest(channelID, pageNo, pageSize, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getFilterMediaInfo(MediaInfoQuery query, Observer observer) {
		FilterMediaInfoRequest request = new FilterMediaInfoRequest(query);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getMediaUrlInfoList(int mediaID, int ci,
			int source, Observer observer) {
		MediaUrlInfoListRequest request = new MediaUrlInfoListRequest(mediaID,
				ci, source);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getTelevisionShowInfo(MediaInfoQuery query,
			String statisticInfo, Observer observer) {
		TelevisionShowInfoRequest request = new TelevisionShowInfoRequest(
				query, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getTelevisionProgramsAssemble(Observer observer) {
		TelevisionProgramsAssembleRequest request = new TelevisionProgramsAssembleRequest();
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getMediaInfoByH5Url(int mediaSource,
			String playUrl, String statisticInfo, Observer observer) {
		GetMediaInfoByH5UrlRequest request = new GetMediaInfoByH5UrlRequest(
				mediaSource, playUrl, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static TvServiceRequest getPlayHistory(UserDataParam playHistoryInfoQuery, Observer observer) {
		PlayHistoryRequest request = new PlayHistoryRequest(
				playHistoryInfoQuery);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static ServiceRequest getNickNameInfo(List<String> userIds, Observer observer) {
		GetNickNameRequest request = new GetNickNameRequest(userIds);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}

	public static ServiceRequest GetUpdateApkInfo(String miuiversion, String curVersionCode, Observer observer) {
		GetUpdateApkRequest request = new GetUpdateApkRequest(miuiversion, curVersionCode);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceResponse getPlayHistorySync(UserDataParam playHistoryInfoQuery) {
		PlayHistoryRequest request = new PlayHistoryRequest(
				playHistoryInfoQuery);
		return request.execSync();
	}
	
	public static TvServiceRequest searchMediaInfo(SearchInfo searchInfo, Observer observer) {
		SearchMediaInfoRequest request = new SearchMediaInfoRequest(searchInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest comment(int mediaID, int score, String comment,
			String statisticInfo, Observer observer) {
		CommentRequest request = new CommentRequest(mediaID, score, comment, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest uploadComUserData(String statisticInfo) {
		UploadComUserDataRequest request = new UploadComUserDataRequest(statisticInfo);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getTelevisionRecommendation(MediaInfoQuery query, Observer observer) {
		TelevisionRecommendRequest request = new TelevisionRecommendRequest(query);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	public static TvServiceRequest getSpecialSubjectList(int pageNo, int pageSize, String statisticInfo, Observer observer) {
		SpecialSubjectListRequest request = new SpecialSubjectListRequest(pageNo, pageSize, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getSpecialSubjectMedia(int id, String statisticInfo, Observer observer) {
		SpecialSubjectMediaRequest request = new SpecialSubjectMediaRequest(id, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest getMyFavoriteMedia(Observer observer) {
		GetMyFavoriteRequest request = new GetMyFavoriteRequest();
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceResponse getMyFavoriteMediaSync() {
		GetMyFavoriteRequest request = new GetMyFavoriteRequest();
		return request.execSync();
	}
	
	public static TvServiceRequest setMyFavoriteMedia(FavoriteItem myFavoriteItemInfo, 
			String statisticInfo, Observer observer) {
		SetMyFavoriteRequest request = new SetMyFavoriteRequest(myFavoriteItemInfo, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceResponse setMyFavoriteMediaSync(FavoriteItem myFavoriteItemInfo, String statisticInfo) {
		SetMyFavoriteRequest request = new SetMyFavoriteRequest(myFavoriteItemInfo, statisticInfo);
		return request.execSync();
	}
	
	public static TvServiceRequest deleteMyFavoriteMedia(int[] ids, String statisticInfo, Observer observer) {
		DeleteMyFavoriteRequest request = new DeleteMyFavoriteRequest(ids, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceResponse deleteMyFavoriteMediaSync(int[] ids, String statisticInfo) {
		DeleteMyFavoriteRequest request = new DeleteMyFavoriteRequest(ids, statisticInfo);
		return request.execSync();
	}

	public static TvServiceRequest uploadIMEBootInfo(String imei, String uniqueIdentifier, String deviceName, 
			String networkType, int deviceType, int appVersion, Observer observer) {
		UploadIMEBootInfoRequest request = new UploadIMEBootInfoRequest(imei, uniqueIdentifier, deviceName, 
				networkType, deviceType, appVersion);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static TvServiceRequest deleteMyFavoriteMedia(int[] ids, 
			boolean needToReLogin, String statisticInfo, Observer observer) {
		DeleteMyFavoriteRequest request = new DeleteMyFavoriteRequest(ids, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceRequest setPlayInfo(String statisticInfo) {
		SetPlayInfoRequest request = new SetPlayInfoRequest(statisticInfo);
		request.sendRequest();
		return request;
	}
	
	public static ServiceRequest getInfomationListRequest(MediaInfoQuery query, Observer observer) {
		InformationListRequest request = new InformationListRequest(query);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceRequest getInfoRecommendRequest(int mediaid, int channelID, String statisticInfo,
			Observer observer){
		InfoRecommendRequest request = new InfoRecommendRequest(mediaid, channelID, statisticInfo);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
	
	public static ServiceRequest getCmccKey(String channelId, String appid, Observer observer){
		GetCmccKeyRequest request = new GetCmccKeyRequest(channelId, appid);
		request.setObserver(observer);
		request.sendRequest();
		return request;
	}
}
