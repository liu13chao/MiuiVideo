package com.miui.video.widget.statusbtn;

import java.util.ArrayList;

/**
 *@author tangfuling
 *
 */

/**
 * this class is defined for StatusBtnAdapter
 *
 */

public class StatusBtnItemList extends ArrayList<StatusBtnItem> {
	private static final long serialVersionUID = 2L;
	
	public StatusBtnItem getStatusBtnItem(int episode) {
		for(int i = 0; i < size(); i++) {
			StatusBtnItem statusBtnItem = get(i);
			if(statusBtnItem != null && statusBtnItem.episode == episode) {
				return statusBtnItem;
			}
		}
		return null;
	}
}
