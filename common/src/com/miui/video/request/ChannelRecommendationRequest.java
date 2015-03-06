package com.miui.video.request;

import com.miui.video.response.ChannelRecommendationResponse;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class ChannelRecommendationRequest extends TvServiceRequest {
	
	public ChannelRecommendationRequest(MediaInfoQuery query, boolean isMultiChannel) {
		mPath = "/tvservice/getchannelrecommendmedia3";
		if(query != null && query.ids != null && query.ids.length > 0) {
			if(isMultiChannel) {
				StringBuilder ids = new StringBuilder();
				for(int i = 0; i < query.ids.length; i++) {
					ids.append(query.ids[i]);
					if(i < query.ids.length - 1) {
						ids.append(",");
					}
				}
				addParam("channelids", ids.toString());
			} else {
				addParam("channelid", String.valueOf(query.ids[0]));
			}
			addParam("pageno", String.valueOf(query.pageNo));
			addParam("pagesize", String.valueOf(query.pageSize));
			addParam("orderby", String.valueOf(query.orderBy));
			addParam("listtype", String.valueOf(query.listType));
			addParam("postertype", String.valueOf(query.posterType));
			addParam("userbehavdata", String.valueOf(query.statisticInfo));
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new ChannelRecommendationResponse();
		}
	}
}
