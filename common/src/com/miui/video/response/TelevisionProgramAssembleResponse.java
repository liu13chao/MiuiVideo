package com.miui.video.response;

import com.miui.video.type.TvProgrammeAssemble;
import com.miui.video.type.TvProgrammesAndDate;

public class TelevisionProgramAssembleResponse extends TvServiceResponse {
	public TvProgrammeAssemble data;
	
	@Override
	public void completeData() {
		super.completeData();
		if(data != null) {
			if(data.programmes != null) {
				for(int i = 0; i < data.programmes.length; i++) {
					TvProgrammesAndDate tvProgrammesAndDate = data.programmes[i];
					if(tvProgrammesAndDate.data != null) {
						for(int j = 0; j < tvProgrammesAndDate.data.length; j++) {
							tvProgrammesAndDate.data[j].completeData();
						}
					}
				}
			}
		}
	}
}
