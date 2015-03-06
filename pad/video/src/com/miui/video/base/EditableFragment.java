/**
 * 
 */
package com.miui.video.base;

import java.util.ArrayList;

import com.miui.video.R;
import com.miui.video.widget.actionmode.ActionModeView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author dz
 *
 */
public abstract class EditableFragment extends Fragment {
	
	protected Activity mActivity;
	protected ActionModeView.Callback mActionModeCallback;
	protected ViewGroup mContainer;
	protected ActionModeView mActionModeView;
	
	protected ArrayList<Object> mSelectedObject = new ArrayList<Object>();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContainer = container;
		initActionMode(container);
		return null;
	}	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		if (mActionModeView != null) {
			mContainer.removeView(mActionModeView);
		}
	}
		
	public boolean isInEdit() {
		return (mActionModeView != null && mActionModeView.isEdit());
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mActionModeView != null && mActionModeView.isEdit()) {
			exitActionModeView();
			return true;
		}
		return false;
	};		
	
	private void initActionMode(ViewGroup dectorView) {
		if (dectorView == null || mActivity == null)
			return;
		
		initActionModeCallback();
		if (mActionModeCallback == null)
			return;
		
		mActionModeView = new ActionModeView(mActivity, mActionModeCallback, dectorView, true);
	}

	protected void startActionModeView() {
		if (mActionModeView != null && !mActionModeView.isEdit()) {
			mActionModeView.startActionMode();
			if (mSelectedObject.size() == 0) {
				mActionModeView.setUISelectAll();
			}
			onStartActionMode();
		}
	}
	
	protected void exitActionModeView() {
		if (mActionModeView != null && mActionModeView.isEdit()) {
			mActionModeView.exitActionMode();
		}
		onExitActionMode();
	}

	protected void refreshActionModeViewTitle() {
		String str = getResources().getString(R.string.select_count_xiang);
		str = String.format(str, mSelectedObject.size());
		mActionModeView.setTitle(str);
	}
	
	protected abstract void onStartActionMode();
	protected abstract void onExitActionMode();
	protected abstract void initActionModeCallback();
}
