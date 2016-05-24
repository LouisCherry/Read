package com.read.pan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.read.pan.R;
import com.read.pan.app.ReadApplication;
import com.read.pan.config.PropertyConfig;
import com.read.pan.entity.Book;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDeatilActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.book_detail_img)
    SimpleDraweeView bookDetailImg;
    @BindView(R.id.book_title)
    TextView bookTitle;
    private String bookId;
    ReadApplication readApplication;
    private int ifCollect=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_deatil);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        readApplication = (ReadApplication) getApplication();
        bookId = getIntent().getStringExtra("bookId");
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_collect:
                        if(ifCollect==1){
                         break;
                        }
                        collect(item,bookId);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookId == null) {
            bookId = readApplication.getProperty(PropertyConfig.BOOKID);
        }
        if (bookId != null) {
            refreshData(bookId);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (bookId != null) {
            readApplication.setProperty(PropertyConfig.BOOKID, bookId);
        }
        super.onPause();
    }

    private void refreshData(String bookId) {
        String userId="";
        if(readApplication.isLogin()){
            userId=readApplication.getLoginUser().getUserId();
        }
        RestClient.bookApi().book(bookId,userId).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if(response.code()==ResultCode.SUCCESS){
                    Book book = response.body();
                    Uri uri = Uri.parse(book.getCover());
                    bookDetailImg.setImageURI(uri);
                    bookTitle.setText(book.getBookName());
                    ifCollect=book.getIfCollect();
                    if(ifCollect==1){
//                        MenuView.ItemView itemView= (MenuView.ItemView) findViewById(R.id.action_collect);
//                        itemView.setIcon(getResources().getDrawable(R.drawable.general__shared__favour_selected));
                    }
                }
                if (response.code() == ResultCode.NODETAIL) {
                    Snackbar.make(getWindow().getDecorView(), "暂无详细信息", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Snackbar.make(getWindow().getDecorView(), "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
            }
        });
    }
    private void collect(final MenuItem menuItem, final String bookId){
        if(readApplication.isLogin()){
            String userId=readApplication.getLoginUser().getUserId();
            if(userId!=null){
                RestClient.userApi().collect(userId,bookId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == ResultCode.SUCCESS) {
                            menuItem.setIcon(R.drawable.general__shared__favour_selected);
                            Snackbar.make(getWindow().getDecorView(), "收藏成功", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        }
                        if (response.code() == ResultCode.NOBOOK) {
                            Snackbar.make(getWindow().getDecorView(), "无法收藏，该书籍不存在", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Snackbar.make(getWindow().getDecorView(), "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    }
                });
            }else{
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
            }
        }else{
            startActivity(new Intent(getBaseContext(),LoginActivity.class));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_deatil,menu);
        return super.onCreateOptionsMenu(menu);
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
