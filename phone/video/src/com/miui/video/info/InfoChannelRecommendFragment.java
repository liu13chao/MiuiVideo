/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoRecommendFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.ChannelRecommendAdapter;
import com.miui.video.base.BaseFragment;
import com.miui.video.controller.MediaViewClickHandler;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;

/**
 * @author tianli
 *
 */
public class InfoChannelRecommendFragment extends BaseFragment {

//    private Context mContext;
    private View mContentView;
    private ListView mListView;
    
    // Data
    private ChannelRecommendation[] mRecommend;
    private Channel mChannel;
    
//    private InfoRecommendAdapter mAdapter;
    private ChannelRecommendAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            Object obj = bundle.getSerializable(InfoConstants.KEY_CHANNEL);
            if(obj instanceof Channel) {
                mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
            }
            obj = bundle.getSerializable(InfoConstants.KEY_CHANNEL_RECOMMEND_LIST);
            if(obj instanceof ChannelRecommendation[]) {
                mRecommend = (ChannelRecommendation[])obj;
            }
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.info_recommend_fragment, null);
        return mContentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mContext = getActivity();
        init();
    }
    
    private void init(){
        initUI();
    }
    
    private void initUI(){
        mListView = (ListView)mContentView.findViewById(R.id.info_recommend_fragment_list);
        mAdapter = new ChannelRecommendAdapter(getActivity(), mChannel);
        mAdapter.setViewClickHandler(new MediaViewClickHandler
                (getActivity(), SourceTagValueDef.PHONE_V6_CHANNEL_CHOICE_VALUE));
        mAdapter.setChannelRecommendations(mRecommend);
        mListView.setAdapter(mAdapter);
    }

}
