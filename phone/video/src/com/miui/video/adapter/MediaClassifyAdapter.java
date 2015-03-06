package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;
import com.miui.video.widget.media.MediaViewGrid;

/**
 *@author tangfuling
 *
 */
public class MediaClassifyAdapter extends BaseClassifyAdapter {
	
	private int NUM_COLUMNS_V = 3;
	private int NUM_COLUMNS_H = 2;
	
	//分类列表
	private List<String> mClassifyGroup;
	//每个分类的显示类型,如果不设置默认是 MEDIA_CLASSIFY_TYPE_V
	private HashMap<String, Integer> mClassifyChildViewType;
	//每个分类的数据
	private HashMap<String, List<? extends BaseMediaInfo>> mClassifyChildData;
	//拆分每个分类的数据为一行一行的，便于复用
	private HashMap<String, List<List<? extends BaseMediaInfo>>> mClassifyDividedChildData = 
			new HashMap<String, List<List<? extends BaseMediaInfo>>>();
	
	private Context mContext;
	private boolean mIsInEditMode;
	
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;
	
	public MediaClassifyAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setInEditMode(boolean isInEditMode) {
		mIsInEditMode = isInEditMode;
		notifyDataSetChanged();
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		mOnItemLongClickListener = onItemLongClickListener;
	}
	
	public void setClassifyList(List<String> classifyGroup) {
		this.mClassifyGroup = classifyGroup;
	}
	
	public void setClassifyChildData(HashMap<String, List<? extends BaseMediaInfo>> classifyChildData) {
		this.mClassifyChildData = classifyChildData;
		divideChildData();
		notifyDataSetChanged();
	}
	
	public void setClassifyChildViewType(HashMap<String, Integer> classifyChildViewType) {
		this.mClassifyChildViewType = classifyChildViewType;
		divideChildData();
	}
	
	private void divideChildData() {
		mClassifyDividedChildData.clear();
		if(mClassifyChildData == null) {
			return;
		}
		Set<Entry<String, List<? extends BaseMediaInfo>>> set = mClassifyChildData.entrySet();
		Iterator<Entry<String, List<? extends BaseMediaInfo>>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, List<? extends BaseMediaInfo>> entry = iterator.next();
			String key = entry.getKey();
			List<? extends BaseMediaInfo> value = entry.getValue();
			
			List<List<? extends BaseMediaInfo>> dividedList = new ArrayList<List<? extends BaseMediaInfo>>();
			if(value != null) {
				int numCloumns = NUM_COLUMNS_V;
				if(mClassifyChildViewType != null) {
					int childItemMediaViewType = mClassifyChildViewType.get(key);
					if(childItemMediaViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_H) {
						numCloumns = NUM_COLUMNS_H;
					}
				}
				int valueSize = value.size();
				int rows = (int) Math.ceil(valueSize / (float)numCloumns);
				for(int i = 0; i < rows; i++) {
					int start = i * numCloumns;
					int end = (i + 1) * numCloumns;
					if(end > valueSize) {
						end = (int) valueSize;
					}
					List<? extends BaseMediaInfo> list = value.subList(start, end);
					dividedList.add(list);
				}
			}
			mClassifyDividedChildData.put(key, dividedList);
		}
	}
	
	@Override
	public int getGroupCount() {
		if(mClassifyGroup != null) {
			return mClassifyGroup.size();
		}
		return 0;
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		if(mClassifyGroup != null) {
			return mClassifyGroup.get(groupPosition);
		}
		return null;
	}
	
	private class ViewHolderGroup {
		View container;
		TextView textView;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolderGroup viewHolderGroup = null;
		if(convertView == null) {
			View view = View.inflate(mContext, R.layout.media_classify_group, null);
			viewHolderGroup = new ViewHolderGroup();
			viewHolderGroup.container = view.findViewById(R.id.media_classify_group_container);
			viewHolderGroup.textView = (TextView) view.findViewById(R.id.media_classify_group_title);
			convertView = view;
			convertView.setTag(viewHolderGroup);
		} else {
			viewHolderGroup = (ViewHolderGroup) convertView.getTag();
		}
		
		String title = (String) getGroup(groupPosition);
		viewHolderGroup.textView.setText(title);
		
		if(!isExpanded) {
			viewHolderGroup.container.setBackgroundResource(R.drawable.com_item_bg_full);
		} else {
			viewHolderGroup.container.setBackgroundResource(R.drawable.com_item_bg_up);
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(mClassifyGroup != null && mClassifyDividedChildData != null) {
			String key = mClassifyGroup.get(groupPosition);
			if(!Util.isEmpty(key)) {
				List<List<? extends BaseMediaInfo>> list = mClassifyDividedChildData.get(key);
				if(list != null) {
					return list.size();
				}
			}
		}
		return 0;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if(mClassifyGroup != null && mClassifyDividedChildData != null) {
			String key = mClassifyGroup.get(groupPosition);
			if(!Util.isEmpty(key)) {
				List<List<? extends BaseMediaInfo>> list = mClassifyDividedChildData.get(key);
				if(list != null) {
					return list.get(childPosition);
				}
			}
		}
		return null;
	}
	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		if(mClassifyGroup != null && mClassifyChildViewType != null) {
			String key = mClassifyGroup.get(groupPosition);
			if(!Util.isEmpty(key)) {
				return mClassifyChildViewType.get(key);
			}
		}
		return MediaViewHelper.MEDIA_CLASSIFY_TYPE_V;
	}
	
	@Override
	public int getChildTypeCount() {
		return 2;
	}
	
	private class ViewHolderChild {
		MediaViewGrid mediaViewGrid;
		View bottomMargin;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolderChild viewHolderChildV = null;
		ViewHolderChild viewHolderChildH = null;
		
		int childItemViewType = getChildType(groupPosition, childPosition);
		
		if(convertView == null) {
			if(childItemViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_H) {
				View view = View.inflate(mContext, R.layout.media_classify_child_h, null);
				viewHolderChildH = new ViewHolderChild();
				viewHolderChildH.mediaViewGrid = (MediaViewGrid) view.findViewById(R.id.media_classify_child_item_h);
				viewHolderChildH.mediaViewGrid.setOnItemClickListener(mOnItemClickListener);
				viewHolderChildH.mediaViewGrid.setOnItemLongClickListener(mOnItemLongClickListener);
				viewHolderChildH.bottomMargin = view.findViewById(R.id.media_classify_child_item_v_margin_view);
				convertView = view;
				convertView.setTag(viewHolderChildH);
			} else {
				View view = View.inflate(mContext, R.layout.media_classify_child_v, null);
				viewHolderChildV = new ViewHolderChild();
				viewHolderChildV.mediaViewGrid = (MediaViewGrid) view.findViewById(R.id.media_classify_child_item_v);
				viewHolderChildV.mediaViewGrid.setOnItemClickListener(mOnItemClickListener);
				viewHolderChildV.mediaViewGrid.setOnItemLongClickListener(mOnItemLongClickListener);
				viewHolderChildV.bottomMargin = view.findViewById(R.id.media_classify_child_item_v_margin_view);
				convertView = view;
				convertView.setTag(viewHolderChildV);
			}
		} else {
			if(childItemViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_H) {
				viewHolderChildH = (ViewHolderChild) convertView.getTag();
			} else {
				viewHolderChildV = (ViewHolderChild) convertView.getTag();
			}
		}
		
		int childCount = getChildrenCount(groupPosition);
		
		@SuppressWarnings("unchecked")
		List<BaseMediaInfo> list = (List<BaseMediaInfo>) getChild(groupPosition, childPosition);
		if(childItemViewType == MediaViewHelper.MEDIA_CLASSIFY_TYPE_H) {
			viewHolderChildH.mediaViewGrid.setInEditMode(mIsInEditMode);
			viewHolderChildH.mediaViewGrid.setGroup(list);
			if(childPosition == childCount - 1) {
				viewHolderChildH.bottomMargin.setVisibility(View.VISIBLE);
			} else {
				viewHolderChildH.bottomMargin.setVisibility(View.GONE);
			}
		} else {
			viewHolderChildV.mediaViewGrid.setInEditMode(mIsInEditMode);
			viewHolderChildV.mediaViewGrid.setGroup(list);
			if(childPosition == childCount - 1) {
				viewHolderChildV.bottomMargin.setVisibility(View.VISIBLE);
			} else {
				viewHolderChildV.bottomMargin.setVisibility(View.GONE);
			}
		}
		
		if(childCount == 1) {
			convertView.setBackgroundResource(R.drawable.com_item_bg_down);
		} else {
			if(childPosition == childCount - 1) {
				convertView.setBackgroundResource(R.drawable.com_item_bg_down);
			} else {
				convertView.setBackgroundResource(R.drawable.com_item_bg_mid);
			}
		}
		return convertView;
	}
}
