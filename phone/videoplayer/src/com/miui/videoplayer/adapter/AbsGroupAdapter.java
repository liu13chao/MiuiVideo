package com.miui.videoplayer.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class AbsGroupAdapter<T> extends BaseAdapter {

	private List<T> mGroup = new ArrayList<T>();
	private final Context mContext;

	public AbsGroupAdapter(Context context) {
		mContext = context;
	}

	public Context getContext() {
		return mContext;
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

	public List<T> getGroup() {
		return mGroup;
	}

	@Override
	public int getCount() {
		return mGroup.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		}
		return mGroup.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
