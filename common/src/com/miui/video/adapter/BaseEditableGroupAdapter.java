package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public abstract class BaseEditableGroupAdapter<T> extends BaseGroupAdapter<T> {

	private List<Integer> mSelecteds = new ArrayList<Integer>();
	private boolean mEditable = false;

	public BaseEditableGroupAdapter(Context context) {
		super(context);
	}

	public boolean isInEditMode() {
		return mEditable;
	}

	public void enterEditMode() {
		mEditable = true;
		notifyDataSetChanged();
	}

	public void exitEditMode() {
		mEditable = false;
		notifyDataSetChanged();
	}

	public void toggleEditMode() {
		if (isInEditMode()) {
			exitEditMode();
		} else {
			enterEditMode();
		}
	}

	public boolean isSelected(int position) {
		return mSelecteds.contains(position);
	}

	public boolean isAllSelected() {
		return getSelectedCount() == getCount();
	}

	public boolean isNoneSelected() {
		return getSelectedCount() == 0;
	}

	public void selectItem(int position) {
		if (position < 0 || position >= getCount()) {
			return;
		}
		if (mSelecteds.contains(position)) {
			return;
		}
		mSelecteds.add(position);
		notifyDataSetChanged();
	}

	public void deselectItem(int position) {
		if (!mSelecteds.contains(position)) {
			return;
		}
		final int index = mSelecteds.indexOf(position);
		mSelecteds.remove(index);
		notifyDataSetChanged();
	}

	public void toggleSelectItem(int position) {
		if (isSelected(position)) {
			deselectItem(position);
		} else {
			selectItem(position);
		}
	}

	public void selectAll() {
		mSelecteds.clear();
		final int count = getCount();
		for (int i = 0; i < count; i++) {
			mSelecteds.add(i);
		}
		notifyDataSetChanged();
	}

	public void deselectAll() {
		mSelecteds.clear();
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelecteds.size();
	}

	public List<T> getSelectedItems() {
		List<T> list = new ArrayList<T>();
		for (Integer position : mSelecteds) {
			T item = getItem(position);
			if (item != null) {
				list.add(item);
			}
		}
		return list;
	}

	public List<T> removeSelectedItems() {
		List<T> list = new ArrayList<T>();
		for (Integer position : mSelecteds) {
			T item = getItem(position);
			if (item != null) {
				list.add(item);
			}
			if(position >= 0 && position < mGroup.size()){
				mGroup.remove((int)position);
			}
		}
		notifyDataSetChanged();
		return list;
	}
	
	public List<T> getUnselectedItems() {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < getCount(); i++) {
			T item = getItem(i);
			if (!isSelected(i) && item != null) {
				list.add(item);
			}
		}
		return list;
	}

	@Override
	public void clear() {
		mGroup.clear();
		mSelecteds.clear();
		notifyDataSetChanged();
	}

}
