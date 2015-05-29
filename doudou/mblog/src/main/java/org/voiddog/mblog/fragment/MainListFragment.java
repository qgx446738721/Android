package org.voiddog.mblog.fragment;

import android.content.Context;
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

    //底部加载更多的view
    private TextView foot_view;

    /**
     * 数据区
     */
    GetAllMovingRequest movingRequest = new GetAllMovingRequest();
    private boolean hasMore = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    Context context;

    /**
     * 适配器
     */
    private ArticleListAdapter adapter;

    /**
     * 初始化入口
     */
    @AfterViews
    void init(){
        context = getActivity();
        movingRequest.email = config.email().getOr("634771197@qq.com");
        adapter = new ArticleListAdapter();

        setUpFootAndHeadView();
        setUpPtrFresh();
        setUpListView();
    }

    @ItemClick(R.id.lv_main)
    void onItemClick(ArticleData data){
        if(data != null) {
            ArticleDetailActivity_.intent(context).extra("article_id", data.mid)
                    .extra("article_content", data.content)
                    .extra("article_title", data.title)
                    .extra("article_subtitle", data.sub_title).start();
        }
    }

    void setUpPtrFresh(){
        final StoreHouseHeader header = new StoreHouseHeader(context);
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
                loadListData(false);
            }
        });
        ptr_main.autoRefresh();
    }

    void setUpFootAndHeadView(){
        View view = View.inflate(context, R.layout.load_more_foot_view, null);
        foot_view = (TextView) view.findViewById(R.id.tv_load_more);
        lv_main.addFooterView(view);
        View headView = new TextView(context);
        headView.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.title_height)
                - PtrLocalDisplay.dp2px(15), 0, 0);
        lv_main.addHeaderView(headView);
        lv_main.setAdapter(adapter);
    }

    void setUpListView(){
        lv_main.setOnScrollListener(this);
    }

    /**
     * 加载数据
     * @param isMore 是否是下拉加载
     */
    void loadListData(final boolean isMore){
        if(!isMore){
            movingRequest.page = 0;
            hasMore = true;
        }
        if(!hasMore){ return;}
        foot_view.setText("正在加载");
        HttpNetWork.getInstance().request(movingRequest, new HttpNetWork.NetResponseCallback() {
            @Override
            public void onResponse(DHttpRequestBase request, HttpResponsePacket response) {
                ptr_main.refreshComplete();
                if(response.code == 0){
                    List<ArticleData> articleDataList = response.getData(
                            new TypeToken<List<ArticleData>>(){}.getType()
                    );
                    if(!isMore){
                        adapter.clearAll();
                    }
                    adapter.addAll(articleDataList);
                    if(hasMore){
                        movingRequest.page++;
                    }
                    if(articleDataList.size() < movingRequest.num){
                        hasMore = false;
                        foot_view.setText("没有更多了");
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                    foot_view.setText("加载失败");
                }
            }
        }, new HttpNetWork.NetErrorCallback() {
            @Override
            public void onError(DHttpRequestBase request, String errorMsg) {
                ptr_main.refreshComplete();
                foot_view.setText("加载失败");
                ToastUtil.showToast("网络或服务器错误");
            }
        });
    }

    /**
     * list滚动监听
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE && firstVisibleItem + visibleItemCount == totalItemCount){
            loadListData(true);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
}
