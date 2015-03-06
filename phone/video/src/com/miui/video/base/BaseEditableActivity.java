package com.miui.video.base;

import android.os.Bundle;
import android.view.View;

import com.miui.video.R;
import com.miui.video.util.Util;
import com.miui.video.widget.ButtonPair;
import com.miui.video.widget.ButtonPair.OnPairClickListener;
import com.miui.video.widget.StorageView;
import com.miui.video.widget.TitleView;
import com.miui.video.widget.TitleView.OnBackClickListener;
import com.miui.video.widget.TitleView.OnEditClickListener;

public abstract class BaseEditableActivity extends BaseActivity {

	private TitleView mTopTitleView;
	private ButtonPair mBottomButtonPair;
	private StorageView mStorageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewRes());
		initCommonUI();
	}

	protected void initCommonUI() {
		mTopTitleView = (TitleView) findViewById(R.id.offline_title);
		mTopTitleView.setName(getTitleName());
		mTopTitleView.setHint(R.string.edit);

		mBottomButtonPair = (ButtonPair) findViewById(R.id.offline_pair);
		mBottomButtonPair.setLeftText(R.string.select_all);
		mBottomButtonPair.setRightText(R.string.delete);

		mStorageView = (StorageView) findViewById(R.id.offline_storage);
		if(mStorageView != null){
			mStorageView.setStorage(Util.getSDAllSize(), Util.getSDAvailaleSize());
		}
		mTopTitleView.setOnBackClickListener(new OnBackClickListener() {

			@Override
			public void onBackClick() {
				if (isInEditMode()) {
					exitEdit();
				} else {
					BaseEditableActivity.this.finish();
				}
			}
		});
		
		mTopTitleView.setOnEditClickListener(new OnEditClickListener() {

			@Override
			public void onEditClick() {
				if (isInEditMode()) {
					exitEdit();
				} else {
					enterEdit();
				}
				
//				deselectAll();
//				toggleSelectHint();
//				refreshSelectedCount();

//				toggleEditMode();
//				toggleBottomView();
//				toggleTopName();
			}
		});
		mBottomButtonPair.setOnPairClickListener(new OnPairClickListener() {

			@Override
			public void onRightClick() {
				deleteSelectedItems();
				toggleSelectHint();
				refreshSelectedCount();
			}

			@Override
			public void onLeftClick() {
				toggleSelectAll();
				toggleSelectHint();
				refreshSelectedCount();
			}
		});
	}
	
	private void enterEdit() {
		enterEditMode();
		
		if(mStorageView != null){
			mStorageView.setVisibility(View.INVISIBLE);
		}
		mBottomButtonPair.setVisibility(View.VISIBLE);
		
		mTopTitleView.setName(R.string.edit);
		mTopTitleView.setHint(R.string.cancel);
		
		deselectAll();
		toggleSelectHint();
		refreshSelectedCount();
	}
	
	private void exitEdit() {
		exitEditMode();
		
		if(mStorageView != null){
			mStorageView.setVisibility(View.VISIBLE);
		}
		mBottomButtonPair.setVisibility(View.INVISIBLE);
		
		mTopTitleView.setName(getTitleName());
		mTopTitleView.setHint(R.string.edit);
	}

	public void setEditBtnVisibility(int visibility){
		mTopTitleView.setHintVisibility(visibility);
	}
	
//	private void toggleEditMode() {
//		if (isInEditMode()) {
//			exitEditMode();
//		} else {
//			enterEditMode();
//		}
//	}
//
//	private void toggleBottomView() {
//		if (isInEditMode()) {
//			mStorageView.setVisibility(View.INVISIBLE);
//			mBottomButtonPair.setVisibility(View.VISIBLE);
//		} else {
//			mStorageView.setVisibility(View.VISIBLE);
//			mBottomButtonPair.setVisibility(View.INVISIBLE);
//		}
//	}
//
//	private void toggleTopName() {
//		if (isInEditMode()) {
//			mTopTitleView.setName(R.string.edit);
//			mTopTitleView.setHint(R.string.cancel);
//		} else {
//			mTopTitleView.setName(getTitleNameRes());
//			mTopTitleView.setHint(R.string.edit);
//		}
//	}

	private void toggleSelectAll() {
		if (isAllSelected()) {
			deselectAll();
		} else {
			selectAll();
		}
	}

	protected void toggleSelectHint() {
		if (isAllSelected()) {
			mBottomButtonPair.setLeftText(R.string.select_none);
		} else {
			mBottomButtonPair.setLeftText(R.string.select_all);
		}
	}

	protected void refreshSelectedCount() {
		final int count = getSelectedCount();
		if (count > 0) {
			mBottomButtonPair.setRightText(getResources().getString(
					R.string.delete_with, count));
		} else {
			mBottomButtonPair.setRightText(R.string.delete);
		}
	}
	
	private boolean isAllSelected() {
		return getSelectedCount() == getTotalCount();
	}

	protected abstract boolean isInEditMode();

	protected abstract void enterEditMode();

	protected abstract void exitEditMode();

	protected abstract int getSelectedCount();
	
	protected abstract int getTotalCount();

	protected abstract void selectAll();

	protected abstract void deselectAll();

	protected abstract void deleteSelectedItems();

	protected abstract int getContentViewRes();

	protected abstract String getTitleName();

}
