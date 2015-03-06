package com.miui.video.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TvLiveData implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public HashMap<Integer, TelevisionInfo> tvinfoMaps;
	public ArrayList<TvProgrammeAllDataInfo> tvProgrammeAllDataInfos;
	public HashMap<Integer, String> tvSubCategorys;
}
