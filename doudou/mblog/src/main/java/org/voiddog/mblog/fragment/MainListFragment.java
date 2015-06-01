package org.voiddog.mblog.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.lib.http.HttpNetWork;
import org.voiddog.lib.http.HttpResponsePacket;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.ArticleDetailActivity_;
import org.voiddog.mblog.adapter.ArticleListAdapter;
import org.voiddog.mblog.data.ArticleData;
import org.voiddog.mblog.http.GetAllMovingRequest;
import org.voiddog.mblog.preference.Config_;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * 主页文章列表
 * Created by Dog on 2015/4/4.
 */
@EFragment(R.layout.fragment_main_lsit)
public class MainListFragment extends Fragment implements AbsListView.OnScrollListener{
    /**
     * View区
     */
    @ViewById
    ListView lv_main;
    @ViewById
    PtrFrameLayout ptr_main;
    @Pref
    Config_ config;

    GetAllMovingRequest movingRequest = new GetAllMovingRequest();
    boolean isLoading, isFirstLoad = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    //底部加载更多的view
    private TextView foot_view;
    private View head_view;

    /**
     * 适配器
     */
    private ArticleListAdapter adapter = new ArticleListAdapter();

    @AfterViews
    void init(){
        setUpPtrFresh();
        setUpFootAndHeadView();
        setUpListView();

        if(isFirstLoad){
            adapter.registerReceiver(getActivity());
            isFirstLoad = false;
            ptr_main.autoRefresh();
        }
    }

    @ItemClick(R.id.lv_main)
    void onItemClick(ArticleData articleData){
        ArticleDetailActivity_.intent(this)
                .article_title(articleData.title)
                .article_subtitle(articleData.sub_title)
                .article_content(articleData.content)
                .article_id(articleData.mid)
                .start();
    }

    void setUpListView() {
        lv_main.setOnScrollListener(this);
        lv_main.setAdapter(adapter);
    }

    void setUpFootAndHeadView(){
        if(foot_view == null) {
            foot_view = (TextView) View.inflate(getActivity(), R.layout.load_more_foot_view, null);
        }
        else {
            lv_main.removeFooterView(foot_view);
        }
        lv_main.addFooterView(foot_view);
        if(head_view == null) {
            head_view = new TextView(getActivity());
            head_view.setPadding(0, getActivity().getResources().getDimensionPixelSize(R.dimen.title_height)
                    - PtrLocalDisplay.dp2px(15), 0, 0);
        }
        else{
            lv_main.removeHeaderView(head_view);
        }
        lv_main.addHeaderView(head_view);
    }

    void setUpPtrFresh(){
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);
        header.setBackgroundColor(getResources().getColor(R.color.bg));
        header.initWithString("voiddog");

        ptr_main.setHeaderView(header);
        ptr_main.addPtrUIHandler(header);
        ptr_main.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, view, view2);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                refresh();
            }
        });
    }

    void refresh(){
        if(isLoading){
            ptr_main.refreshComplete();
            return;
        }

        movingRequest.email = config.email().getOr("");
        movingRequest.page = 0;
        requestNetData();
    }

    void loadMore(){
        movingRequest.email = config.email().getOr("");
        requestNetData();
    }

    void requestNetData() {
        isLoading = true;
        foot_view.setText("正在加载");
        HttpNetWork.getInstance().request(movingRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                isLoading = false;
                ptr_main.refreshComplete();
                if (response.code == 0) {
                    List<ArticleData> articleDataList = response.getData(
                            new TypeToken<List<ArticleData>>() {
                            }.getType()
                    );
                    fillData(articleDataList);
                } else {
                    ToastUtil.showToast(response.message);
                    foot_view.setText("加载失败");
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                isLoading = false;
                ptr_main.refreshComplete();
                ToastUtil.showToast("网路或服务器错误");
                foot_view.setText("加载失败");
            }
        });
    }

    void fillData(List<ArticleData> articleDataList){
        if(articleDataList.size() < movingRequest.num){
            foot_view.setText("没有更多了");
        }
        if(movingRequest.page == 0){
            adapter.setDataList(articleDataList);
        }
        else {
            adapter.addAll(articleDataList);
        }
        movingRequest.page++;
    }

    @Override
    public void onDestroy() {
        try {
            adapter.unRegisterReceiver(getActivity());
        }
        catch (Exception ignore){}
        super.onDestroy();
    }

    /**
     * list滚动监听
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE && firstVisibleItem + visibleItemCount == totalItemCount){
            loadMore();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
}
