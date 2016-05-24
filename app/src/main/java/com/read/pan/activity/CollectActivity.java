package com.read.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.read.pan.R;
import com.read.pan.adapter.SearchAdapter;
import com.read.pan.app.ReadApplication;
import com.read.pan.entity.Book;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.search_refresher)
    SwipeRefreshLayout searchRefresher;
    private int offset;
    GridLayoutManager gridLayoutManager;
    private SearchAdapter searchAdapter;
    private ArrayList<Book> books;
    //分页一页的条数
    private int limit = 5;
    private final int REFRESH=1;
    private String name;
    //无网状态
    private static final int NONET = 101;
    private ReadApplication readApplication;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case REFRESH:
                    searchAdapter.notifyDataSetChanged();
                    searchRefresher.setRefreshing(false);
                    break;
                case NONET:
                    searchRefresher.setRefreshing(false);
                    Snackbar.make(getWindow().getDecorView(), "请检查网络", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        readApplication= (ReadApplication) getApplication();
        books=new ArrayList<>();
        offset = 0;
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        //给Recycler设置gridView布局
        gridLayoutManager = new GridLayoutManager(getBaseContext(), 1);
        searchRecycler.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        searchRecycler.setHasFixedSize(true);
        //设置Item增加、移除动画
        searchRecycler.setItemAnimator(new DefaultItemAnimator());
        searchAdapter = new SearchAdapter(getBaseContext(), books);
        searchRecycler.setAdapter(searchAdapter);
        //scroollListener在不使用时，要记得移除
        searchRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem = -1;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == searchAdapter.getItemCount()) {
                    offset = lastVisibleItem + 1;
                    refreshData(getWindow().getDecorView(), offset);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            }
        });
        searchAdapter.setOnItemClickLitener(new SearchAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(
                        new Intent(getBaseContext(), BookDeatilActivity.class)
                                .putExtra("bookId", books.get(position).getBookId()));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        searchRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(getWindow().getDecorView(), 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(getWindow().getDecorView(),0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    private void refreshData(final View rootView,int offset){
        if(offset==0){
            books.clear();
        }
        if(readApplication.isLogin()){
            searchRefresher.setRefreshing(true);
            String userId=readApplication.getLoginUser().getUserId();
            RestClient.userApi().collectList(userId,offset,limit).enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    int code = response.code();
                    if (code == ResultCode.SUCCESS) {
                        List<Book> books1 = response.body();
                        books.addAll(books1);
                        Message message = new Message();
                        message.what = REFRESH;
                        mHandler.sendMessage(message);
                    }
                    if (code == ResultCode.EMPTYLIST) {
                        Snackbar.make(rootView, "暂无更多信息", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        searchRefresher.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {
                    Snackbar.make(rootView, "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    searchRefresher.setRefreshing(false);
                }
            });
        }else{
            startActivity(new Intent(getBaseContext(),LoginActivity.class));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
