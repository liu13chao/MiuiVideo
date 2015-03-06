package com.miui.video;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.fragment.ChannelAllFragment;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.PagerView.OnPageChangeListener;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;

public class ChannelSubActivity extends BaseFragmentActivity {

    private ArrayList<ChannelAllFragment> mAllFragments = new ArrayList<ChannelAllFragment>();
    private View mTitleTop;
    private TextView mTitleName;

    private PagerView mPagerView;
    private ViewFragmentPagerAdapter mViewPagerAdapter;
    private Fragment[] mPages;
    private String[] mPageTitles;

    private Channel mChannel, mRootChannel;
    private int mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);
        init();
    }

    private void init() {
        initReceivedData();
        initFragment();
        initUI();
    }

    private void initReceivedData() {
        Intent intent = getIntent();
        Object obj = intent.getSerializableExtra(ChannelActivity.KEY_CHANNEL);
        if(obj instanceof Channel) {
            mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel( (Channel) obj);
        }
        obj = intent.getSerializableExtra(ChannelActivity.KEY_ROOT_CHANNEL);
        if(obj instanceof Channel) {
            mRootChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel( (Channel) obj);
        }
        mCategory = intent.getIntExtra(ChannelActivity.KEY_CATEGORY, 0);
    }

    private void initFragment() {
        int[] orders = null;
        Bundle bundle = new Bundle();
        switch (mCategory) {
        case Channel.CHANNEL_TYPE_MOVIE:
            mPageTitles = new String[3];
            mPageTitles[0] = getResources().getString(R.string.channel_hot);
            mPageTitles[1] = getResources().getString(R.string.channel_score);
            mPageTitles[2] = getResources().getString(R.string.channel_new);
            orders = new int[3];
            orders[0] = DKApi.ORDER_BY_HOT;
            orders[1] = DKApi.ORDER_BY_SCORE_DESC;
            orders[2] = DKApi.ORDER_BY_ISSUEDATE;
            break;
        case Channel.CHANNEL_TYPE_VARIETY:
            mPageTitles = new String[1];
            mPageTitles[0] = getResources().getString(R.string.channel_variety_hot);
            orders = new int[1];
            orders[0] = DKApi.ORDER_BY_HOT;
            break;
        default:
            mPageTitles = new String[2];
            mPageTitles[0] = getResources().getString(R.string.channel_hot);
            mPageTitles[1] = getResources().getString(R.string.channel_new);
            orders = new int[2];
            orders[0] = DKApi.ORDER_BY_HOT;
            orders[1] = DKApi.ORDER_BY_ISSUEDATE;
            break;
        }
        if(orders != null){
            for(int i = 0; i < orders.length; i ++){
                ChannelAllFragment af = new ChannelAllFragment();
                bundle = new Bundle();
                bundle.putSerializable(ChannelAllFragment.KEY_CHANNEL, mChannel);
                bundle.putInt(ChannelAllFragment.KEY_SORT_NAME, orders[i]);
                bundle.putInt(ChannelAllFragment.KEY_CATEGORY, mCategory);
                if(i == 0){
                    bundle.putBoolean(ChannelActivity.KEY_AUTO_INITDATA, true);
                }
                af.setArguments(bundle);
                mAllFragments.add(af);
            }
        }
    }

    private void initUI() {
        initDecorView();
        initTab();
        initPagerView();
    }

    private void initDecorView() {
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        OnlineBg onlineBg = new OnlineBg(this);
        LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        decorView.addView(onlineBg, 0, onlineBgParams);
    }

    private void initTab() {
        initTopBtn();

        mTitleTop = findViewById(R.id.title_top);
        mTitleName = (TextView) findViewById(R.id.title_top_name);
        mTitleTop.setOnClickListener(mOnClickListener);

        if(mChannel != null) {
            mTitleName.setText(mChannel.name);
        }
    }

    private void initTopBtn() {
        findViewById(R.id.channel_search_btn).setOnClickListener(mOnClickListener);
        findViewById(R.id.channel_filte_btn).setOnClickListener(mOnClickListener);
    }

    private void initPagerView() {
        mPagerView = (PagerView) findViewById(R.id.channel_pager_view);
//        int pagerTitleIntervalH = (int) getResources().getDimension(R.dimen.video_common_interval_150);
//        mPagerView.setPagerTitleIntervalH(pagerTitleIntervalH);
//        mPagerView.setTitleWithDefaultIntervalH(mPageTitles);
        mPagerView.setTitle(mPageTitles);
        mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
        mPages = new Fragment[mAllFragments.size()];
        for(int i = 0 ; i < mAllFragments.size(); i ++){
            mPages[i] = mAllFragments.get(i);
        }

        mViewPagerAdapter.setPages(mPages);
        mPagerView.setOffscreenPageLimit(3);
        mPagerView.setViewPagerAdapter(mViewPagerAdapter);
        mPagerView.setOnPageChangedListener(mOnPageChangeListener);
        //		mPagerView.getPager().setOnTouchInterceptor(mOnTouchInterceptor);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
            case R.id.title_top:
                ChannelSubActivity.this.finish();
                break;
            case R.id.channel_search_btn:
                startSearchActivity();
                break;
            case R.id.channel_filte_btn:
                startFilterActivity();
                break;
            default:
                break;
            }
        }
    };

    private void startSearchActivity() {
        Intent intent = new Intent();
        intent.setClass(this, SearchActivity.class);
        startActivity(intent);
    }

    private void startFilterActivity(){
        if(mRootChannel.subfilter != null){
            Intent intent = new Intent();
            intent.putExtra(ChannelActivity.KEY_CHANNEL, mRootChannel);
            intent.setClass(this, ChannelFilterActivity.class);
            startActivity(intent);
        }
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int page) {
            if(page >= 0 && page < mAllFragments.size()){
                mAllFragments.get(page).onSelected();
            }
        }
    };

}
