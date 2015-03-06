package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.CategoryListResponse;
import com.miui.video.type.Category;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class CategoryListSupply implements Observer {

	private ArrayList<CategoryListListener> listeners = new ArrayList<CategoryListListener>();
	private ArrayList<Category> categoryList = new ArrayList<Category>();
	private ServiceRequest request;
	
	public void getCategoryList(String statisticInfo) {
		categoryList.clear();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getCategoryList(-1, this);
	}
	
	public void addListener(CategoryListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(CategoryListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	private void onCategoryListDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			CategoryListListener listener = listeners.get(i);
			if(listener != null) {
				listener.onCategoryListDone(categoryList, isError);
			}
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			CategoryListResponse categoryListResponse = (CategoryListResponse) response;
			Category[] categorys = categoryListResponse.data;
			if(categorys != null) {
				for(int i = 0; i < categorys.length; i++) {
					categoryList.add(categorys[i]);
				}
			}
			onCategoryListDone(false);
		} else {
			onCategoryListDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}

	public interface CategoryListListener {
		public void onCategoryListDone(ArrayList<Category> categoryList, boolean isError);
	}
	
}
