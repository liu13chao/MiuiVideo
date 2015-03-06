package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import miui.view.PagerAdapter;

import android.content.Context;
import android.view.View;

public abstract class BaseGroupPagerAdapter<T> extends PagerAdapter {

	private List<T> mGroup = new ArrayList<T>();

	protected final Context mContext;

	public BaseGroupPagerAdapter(Context context) {
		mContext = context;
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
		setGroup(Arrays.asList(array));
	}

	public void addGroup(List<T> list) {
		if (list != null && list.size() > 0) {
			mGroup.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void addGroup(T[] array) {
		addGroup(Arrays.asList(array));
	}

	public void addItem(T item) {
		if (item != null) {
			mGroup.add(item);
			notifyDataSetChanged();
		}
	}

	public T getItem(int index) {
		if (index < 0 || index >= getCount()) {
			return null;
		}
		return mGroup.get(index);
	}

	@Override
	public int getCount() {
		return mGroup.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
