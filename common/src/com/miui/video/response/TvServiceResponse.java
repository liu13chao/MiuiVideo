/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKResponse.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.miui.video.response;

import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 * 
 */

public class TvServiceResponse extends ServiceResponse{
	
	public static final int STATUS_SUCCESS = 0;      //没有错误
	public static final int STATUS_HTTP_EMPTY_RESULT = 13;    //没有错误，但是返回的内容是空
	
	public static final int STATUS_INVALID_PARAM = 14;    //参数错误
	public static final int STATUS_CHANNEL_ERROR = 15;    //频道id错误
	public static final int STATUS_NO_AVAILABLE_SOURCE = 16;   //没有可用的视频源
	public static final int STATUS_KEY_CHECK_FAILED = 17;    //user key 不匹配
	public static final int STATUS_EXCEED_MAX_ROWS = 18;     //已经达到最大记录
	public static final int STATUS_USER_AUTH_FAILED = 19;  //user token 错误
	public static final int STATUS_TIME_NOT_SYNC = 20;  //客户端与服务器时间不同步
	public static final int STATUS_TASK_QUEUE_FULL = 21;  //服务器忙
	public static final int STATUS_SESSION_TIMEOUT = 22;  //当前会话超时
	public static final int STATUS_ERROR_MEDIA_ID = 23;  //错误的media id
	public static final int STATUS_ERROR_PERSON_ID = 24;  //错误的person id
	public static final int STATUS_ERROR_AIP_VERSION = 25;  //api版本号不匹配
	public static final int STATUS_ERROR_VERSION = 26; //错误的版本号
	public static final int STATUS_ERROR_TOKEN = 27;  //token错误 
	public static final int STATUS_ERROR_USER_ID = 28;  //错误的UserId
	public static final int STATUS_ERROR_GENERAGE_KEY_TOKEN = 29;  //生成key和token失败
	public static final int STATUS_ERROR_USER_IN_BLACKlIST = 30;  //当前用户在黑名单

	public static final int STATUS_NETWORK_ERROR = 10000;
	public static final int STATUS_SERVER_ERROR = 10001;
	public static final int STATUS_UNKOWN_ERROR = 10002;
	
	public TvServiceResponse() {
	}

	public TvServiceResponse(int status) {
		super(status);
	}
	
	@Override
	public boolean isSuccessful() {
		return getStatus() == STATUS_SUCCESS ||
				getStatus() == STATUS_HTTP_EMPTY_RESULT;
	}
	
	public static boolean isSuccessfull(int result) {
		return result == STATUS_SUCCESS ||
				result == STATUS_HTTP_EMPTY_RESULT;
	}
	
	public static boolean isUserTokenExpired(int result) {
		return result == STATUS_USER_AUTH_FAILED ||
				result == STATUS_ERROR_USER_ID || 
				result == STATUS_SESSION_TIMEOUT;
	}
	
	public void completeData() {
	}
}