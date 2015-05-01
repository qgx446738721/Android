package org.voiddog.mblog.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.voiddog.lib.http.DJsonObjectResponse;
import org.voiddog.lib.util.ToastUtil;
import org.voiddog.mblog.R;
import org.voiddog.mblog.activity.ArticleDetailActivity_;
import org.voiddog.mblog.adapter.ArticleListAdapter;
import org.voiddog.mblog.http.HttpStruct;
import org.voiddog.mblog.http.MyHttpNetWork;
import org.voiddog.mblog.http.MyHttpRequest;

import java.util.ArrayList;
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

    //底部加载更多的view
    private TextView foot_view;

    /**
     * 数据区
     */
    private int currentPage = 1, num = 10;
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
        setUpFootView();
        setUpPtrFresh();
        setUpListView();
    }

    @ItemClick(R.id.lv_main)
    void onItemClick(ArticleListAdapter.Data data){
        ArticleDetailActivity_.intent(context).extra("article_id", data.id)
                .extra("article_content", data.content)
                .extra("article_title", data.title)
                .extra("article_subtitle", data.subtitle).start();
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

    void setUpFootView(){
        View view = View.inflate(context, R.layout.load_more_foot_view, null);
        foot_view = (TextView) view.findViewById(R.id.tv_load_more);
        lv_main.addFooterView(view);
        ArticleListAdapter tmpAdapter = new ArticleListAdapter(null);
        lv_main.setAdapter(tmpAdapter);
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
            currentPage = 0;
            hasMore = true;
        }
        if(!hasMore){ return;}
        foot_view.setText("正在加载");
        MyHttpRequest.GetArticleList getArticleList = new MyHttpRequest.GetArticleList(currentPage, num);
        MyHttpNetWork.getInstance().request(getArticleList, new DJsonObjectResponse() {
            @Override
            public void onSuccess(int statusCode, DJsonObjectResponse.DResponse response) {
                ptr_main.refreshComplete();
                if(response.code == 0) {
                    ToastUtil.showToast("refresh Complete!");
                    List<HttpStruct.Article> articleList = response.getData(new TypeToken<List<HttpStruct.Article>>() {}.getType());
                    List<ArticleListAdapter.Data> dataList = new ArrayList<>();
                    for(int i = 0; i < articleList.size(); i++){
                        ArticleListAdapter.Data entity = new ArticleListAdapter.Data();
                        HttpStruct.Article article = articleList.get(i);
                        entity.title = article.title;
                        entity.subtitle = article.subtitle;
                        entity.content = article.body;
                        entity.image = article.image;
                        entity.id = article.id;
                        dataList.add(entity);
                    }
                    if(adapter == null){
                        adapter = new ArticleListAdapter();
                    }
                    if(!isMore){
                        adapter.clearAll();
                    }
                    if(lv_main.getAdapter() != adapter){
                        lv_main.setAdapter(adapter);
                    }
                    adapter.addAll(dataList);
                    currentPage++;
                    if (articleList.size() < num){
                        hasMore = false;
                        foot_view.setText("没有更多了");
                    }
                }
                else{
                    ToastUtil.showToast(response.message);
                    foot_view.setText("加载失败");
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable) {
                ptr_main.refreshComplete();
                foot_view.setText("加载失败");
                ToastUtil.showToast("网络或服务器错误, 错误代码: " + statusCode);
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
