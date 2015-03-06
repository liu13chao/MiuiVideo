package com.miui.video.request;

import com.miui.video.response.FilterMediaInfoResponse;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class FilterMediaInfoRequest extends TvServiceRequest {
	
	public FilterMediaInfoRequest(MediaInfoQuery query) {
		mPath = "/tvservice/filtermediainfo";
		StringBuilder ids = new StringBuilder();
		for(int i = 0; i < query.ids.length; i++) {
			ids.append(query.ids[i]);
			if(i < query.ids.length - 1) {
				ids.append(",");
			}
		}
		addParam("channelids", ids.toString());
		addParam("pageno", String.valueOf(query.pageNo));
		addParam("pagesize", String.valueOf(query.pageSize));
		addParam("orderby", String.valueOf(query.orderBy));
		addParam("listtype", String.valueOf(query.listType));
		addParam("postertype", String.valueOf(query.posterType));
		addParam("searchtype", String.valueOf(query.searchType));
		addParam("userbehavdata", query.statisticInfo);
		addParam("fee", String.valueOf(MediaFeeDef.MEDIA_ALL));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new FilterMediaInfoResponse();
		}
	}
}
