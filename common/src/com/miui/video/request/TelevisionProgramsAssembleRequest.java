package com.miui.video.request;

import com.miui.video.response.TelevisionProgramAssembleResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class TelevisionProgramsAssembleRequest extends TvServiceRequest {
	
	public TelevisionProgramsAssembleRequest() {
		mPath = "/tvservice/gettvprogramassemble";
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new TelevisionProgramAssembleResponse();
		}
	}
}
