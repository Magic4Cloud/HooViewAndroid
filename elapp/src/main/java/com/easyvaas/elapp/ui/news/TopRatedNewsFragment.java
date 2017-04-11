package com.easyvaas.elapp.ui.news;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easyvaas.elapp.adapter.TopRatedNewsAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/10
 * Editor  Misuzu
 * 要闻列表
 */

public class TopRatedNewsFragment extends MyBaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private TopRatedNewsAdapter mTopRatedNewsAdapter;
    private TopRatedModel mTopRatedModel;


    @Override
    protected int getLayout() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initViewAndData() {
        mTopRatedModel = new TopRatedModel();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setPadding(0, (int) ViewUtil.dp2Px(getContext(), 8), 0, 0);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        mRecyclerView.setAdapter(mTopRatedNewsAdapter = new TopRatedNewsAdapter(getActivity(), mTopRatedModel));
        getBannerData();
        getNewsData();
    }

    private void getNewsData() {
        RetrofitHelper.getInstance().getService().getTopRatedNewsTest("http://www.mocky.io/v2/58ec8f0c2700000c1048942d")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopRatedModel>() {
                    @Override
                    public void OnSuccess(TopRatedModel topRatedModel) {
                        // Aya : 2017/4/11 后台接口有问题 延后调试
                        if (topRatedModel != null)
                            mTopRatedNewsAdapter.setTopRatedModel(topRatedModel);
                    }

                    @Override
                    public void OnFailue(String msg) {
                    }
                });
    }

    private void getBannerData() {
        RetrofitHelper.getInstance().getService().getBannerNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<BannerModel>() {
                    @Override
                    public void OnSuccess(BannerModel bannerModel) {
                        mTopRatedNewsAdapter.setBannerModel(bannerModel);
                    }

                    @Override
                    public void OnFailue(String msg) {

                    }
                });
    }

    public static TopRatedNewsFragment newInstance() {
        return new TopRatedNewsFragment();
    }



}
