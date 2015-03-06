package com.miui.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.ClassifyMediaListAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.controller.action.DetailAction;
import com.miui.video.controller.content.FavoriteContentBuilder;
import com.miui.video.local.Favorite;
import com.miui.video.local.FavoriteManager;
import com.miui.video.local.FavoriteManager.OnFavoriteChangedListener;
import com.miui.video.mipush.MiPushMediaProcess;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.widget.EmptyView;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 *@author tangfuling
 *
 */

public class FavoriteActivity extends BaseDelActivity {
	
	private String mCategoryName;
	
	private HashMap<String, List<Favorite>>  mCategoryGroup
		= new HashMap<String, List<Favorite>>();
	
	private FavoriteManager mFavoriteManager;
	private ClassifyMediaListAdapter<Favorite> mFavAdapter;
	
	public final static String KEY_FROMNOTIFICATION = "fromNotification";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFavoriteManager.removeListener(mOnFavoriteChangedListener);
	}
	
	//init
	private void init() {
		clearNotificationCount();
		initData();
	}
	
	private void clearNotificationCount() {
		Intent intent = getIntent();
		boolean bFromNotification = intent.getBooleanExtra(KEY_FROMNOTIFICATION, false);
		if(bFromNotification) {
			DKApp.getSingleton(MiPushMediaProcess.class).clearCurCount();
		}
	}
		
	private void initData() {
        mFavoriteManager = DKApp.getSingleton(FavoriteManager.class);
        mFavoriteManager.addListener(mOnFavoriteChangedListener);
		mCategoryName = getResources().getString(R.string.online_video);
		loadData();
	}
		
	//get data
	private void loadData() {
        mFavoriteManager.loadFavorite();
		List<Favorite> favoriteList = mFavoriteManager.getFavoriteList();
		prepareFavorateMedias(favoriteList);
	}
	
	private void prepareFavorateMedias(List<Favorite> favoriteList) {
	    refreshMediaList(favoriteList);
		mCategoryGroup.put(mCategoryName, favoriteList);
		mFavAdapter.setData(mCategoryGroup);
	}

	private OnFavoriteChangedListener mOnFavoriteChangedListener = new OnFavoriteChangedListener() {
		@Override
		public void onFavoritesChanged(List<Favorite> favList) {
		    prepareFavorateMedias(favList);
		}
	};
	
    @Override
    protected CharSequence getPageTitle() {
        return getResources().getString(R.string.my_favorite);
    }
    
    @Override
    protected void onDeleteClick() {
        List<BaseMediaInfo> selectedList = getSelectedMediaList();
        if(selectedList.size() == 0) {
            return;
        }
        List<MediaInfo> list = new ArrayList<MediaInfo>();
        for(int i = 0; i < selectedList.size(); i++) {
            Object selectedMedia = selectedList.get(i);
            if(selectedMedia instanceof Favorite) {
                Object obj = ((Favorite) selectedMedia).getFavoriteItem();
                if(obj instanceof MediaInfo) {
                    MediaInfo mediaInfo = (MediaInfo) obj;
                    MiPushClient.unsubscribe(DKApp.getAppContext(), String.valueOf(mediaInfo.mediaid), null);
                    list.add(mediaInfo);
                }
            }
        }
        AlertMessage.show(R.string.cancel_favorite_success);
        mFavoriteManager.delFavorite(list);
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo media) {
        Favorite favorite = null;
        if(media instanceof Favorite) {
            favorite = (Favorite) media;
            favorite.getFavoriteItem();
            BaseMediaInfo mediaItem = favorite.getFavoriteItem();
            if(mediaItem instanceof MediaInfo){
                new DetailAction(this, (MediaInfo)mediaItem, 
                        SourceTagValueDef.PHONE_V6_MY_FAVORITE_VALUE).action();
            }
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        if(mFavAdapter == null){
            mFavAdapter = new ClassifyMediaListAdapter<Favorite>(this, 3, R.layout.mixed_media_view);
            mFavAdapter.setMediaContentBuilder(new FavoriteContentBuilder(this));
        }
        return mFavAdapter;
    }

    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.local_favorite_empty_title, 
                R.drawable.empty_icon_favorite);
    }
    
}
