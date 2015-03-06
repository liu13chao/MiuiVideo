package com.miui.video.widget.detail.ep;

import com.miui.video.R;
import com.miui.video.api.DKApi;
import com.miui.video.model.ImageManager;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.type.AppRecommandInfo;
import com.miui.video.type.ImageUrlInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailEpSingleItemView extends FrameLayout {

	private Context mContext;
	
	//UI
	private View mContentView;
	private ImageView mIcon;
	private TextView mTitle;
	private TextView mSubtitle;
	
	//data
	private int mMediaId;
	private AppRecommandInfo mAppRecommandInfo;
	
	private ImageManager mImageManager;
	
	public DetailEpSingleItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailEpSingleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailEpSingleItemView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setData(int mediaId, AppRecommandInfo appRecommandInfo) {
		this.mMediaId = mediaId;
		this.mAppRecommandInfo = appRecommandInfo;
		refresh();
	}
	
	//init
	private void init() {
		initManager();
		initUI();
	}
	
	private void initManager() {
		mImageManager = ImageManager.getInstance();
	}
	
	private void initUI() {
		mContentView = View.inflate(mContext, R.layout.detail_ep_single_item, null);
		mContentView.setOnClickListener(mOnClickListener);
		addView(mContentView);
		mIcon = (ImageView) mContentView.findViewById(R.id.detail_ep_single_item_icon);
		mTitle = (TextView) mContentView.findViewById(R.id.detail_ep_single_item_title);
		mSubtitle = (TextView) mContentView.findViewById(R.id.detail_ep_single_item_subtitle);
	}
	
	//packaged method
	private void refresh() {
		if(mAppRecommandInfo == null) {
			return;
		}
		fetchIcon();
		mTitle.setText(mAppRecommandInfo.name);
		mSubtitle.setText(mAppRecommandInfo.subtitle);
	}
	
	private void fetchIcon() {
		ImageUrlInfo imageUrlInfo = new ImageUrlInfo();
		imageUrlInfo.url = mAppRecommandInfo.posterurl;
		imageUrlInfo.md5 = mAppRecommandInfo.md5;
		mImageManager.fetchImage(ImageManager.createTask(imageUrlInfo, null), mIcon);
		if(!ImageManager.isUrlDone(imageUrlInfo, mIcon)) {
			mIcon.setBackgroundResource(R.drawable.transparent);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mAppRecommandInfo != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAppRecommandInfo.link));
				if (intent.resolveActivity(mContext.getPackageManager()) != null) {
					mContext.startActivity(intent);
				}
				uploadStatistic();
			}
		}
	};
	
	//statistic
	private void uploadStatistic() {
		ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
		statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_RECOMAND_MIUI;
		statisticInfo.mediaId = mMediaId;
		statisticInfo.categoryId = mAppRecommandInfo.link;
		DKApi.uploadComUserData(statisticInfo.formatToJson());
	}			
}
