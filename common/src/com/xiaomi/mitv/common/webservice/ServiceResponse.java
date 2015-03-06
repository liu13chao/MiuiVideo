/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKResponse.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.xiaomi.mitv.common.webservice;

/**
 * @author tianli
 * 
 */
public class ServiceResponse {

	public static final int STATUS_SUCCESS = 0;

	public static final int STATUS_SYNC_TS = 105;
	
	public static final int STATUS_NETWORK_ERROR = 10000;
	public static final int STATUS_SERVER_ERROR = 10001;
	public static final int STATUS_UNKOWN_ERROR = 10002;

	private int status;
	private String desc;
	private long ts;

	public ServiceResponse() {
		status = STATUS_SUCCESS;
	}

	public ServiceResponse(int status) {
		this.status = status;
	}
	
	public boolean isSuccessful() {
		return status == STATUS_SUCCESS;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public void completeData() {
		
	}
}