/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   BaseGroupAdapter.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-13 
 */
package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * @author tianli
 * 
 */
public abstract class BaseGroupAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mGroup = new ArrayList<T>();

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	public BaseGroupAdapter(Context context) {
		this.mContext = context;
	}

	public int getCount() {
		return mGroup.size();
	}

	public T getItem(int position) {
		if (position < 0 || position >= mGroup.size()) {
			return null;
		}
		return mGroup.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean isEmpty() {
		return mGroup.isEmpty();
	}

	public void clear() {
		mGroup.clear();
		notifyDataSetChanged();
	}

	public void setGroup(List<T> list) {
		mGroup.clear();
		if (list != null && list.size() > 0) {
			mGroup.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void setGroup(T[] array) {
		if(array != null) {
			setGroup(Arrays.asList(array));
		} else {
			List<T> list = null;
			setGroup(list);
		}
	}

	public void addGroup(List<T> list) {
		if (list != null) {
			for (T item : list) {
				if (item != null) {
					mGroup.add(item);
				}
			}
			notifyDataSetChanged();
		}
	}

	public void addGroup(T[] array) {
		if(array != null) {
			addGroup(Arrays.asList(array));
		} else {
			List<T> list = null;
			addGroup(list);
		}
	}

//	public List<T> getGroup() {
//		return mGroup;
//	}

	public void refresh() {
		notifyDataSetChanged();
	}

}
