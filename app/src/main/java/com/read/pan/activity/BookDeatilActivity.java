package com.read.pan.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.read.pan.R;
import com.read.pan.app.ReadApplication;
import com.read.pan.config.PropertyConfig;
import com.read.pan.entity.Book;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;
import com.read.pan.util.StringUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDeatilActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.toolbar_layout)
//    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.book_detail_img)
    ImageView bookDetailImg;
//    SimpleDraweeView bookDetailImg;
    @BindView(R.id.book_title)
    TextView bookTitle;
    private String bookId;
    ReadApplication readApplication;
    private int ifCollect=0;
    private Book book;
    private DownloadManager downloadManager;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
        }

    };
    int step = 1000;
    QueryRunnable runnable = new QueryRunnable();

    class QueryRunnable implements Runnable {
        public long DownID;

        @Override
        public void run() {
            queryState(DownID);
            handler.postDelayed(runnable, step);
        }
    };

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
                downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(book.getPath()));
                File fold=new File("Read/download");
                if(fold.isDirectory()&&!fold.exists())
                    fold.mkdirs();
                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_MOBILE
                                | DownloadManager.Request.NETWORK_WIFI)
                        .setAllowedOverRoaming(false) // 缺省是true
                        .setTitle("下载") // 用于信息查看
                        .setDescription("下载"+book.getBookName()) // 用于信息查看
                        .setDestinationInExternalPublicDir(
                                "Read/download/"+ bookId,
                                StringUtil.getBookInfo(book.getPath(),book.getBookName()));
                final long mDownloadId = downloadManager.enqueue(request); // 加入下载队列

                startQuery(mDownloadId);
                Snackbar.make(view, "开始下载", Snackbar.LENGTH_LONG)
                        .setAction("取消下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadManager.remove(mDownloadId);
                            }
                        }).show();
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
                    book = response.body();
                    Picasso.with(getBaseContext()).load(book.getCover()).into(bookDetailImg);
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
    private void startQuery(long downloadId) {
        if (downloadId != 0) {
            runnable.DownID = downloadId;
            handler.postDelayed(runnable, step);
        }

    };

    private void stopQuery() {
        handler.removeCallbacks(runnable);
    }

    private void queryState(long downID) {
        // 关键：通过ID向下载管理查询下载情况，返回一个cursor
        Cursor c = downloadManager.query(new DownloadManager.Query()
                .setFilterById(downID));
        if (c == null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG)
                    .show();
        } else { // 以下是从游标中进行信息提取
            if(!c.moveToFirst()){
                c.close();
                return;
            }
            c.close();
        }
    }

    private String statusMessage(int st) {
        switch (st) {
            case DownloadManager.STATUS_FAILED:
                return "Download failed";
            case DownloadManager.STATUS_PAUSED:
                return "Download paused";
            case DownloadManager.STATUS_PENDING:
                return "Download pending";
            case DownloadManager.STATUS_RUNNING:
                return "Download in progress!";
            case DownloadManager.STATUS_SUCCESSFUL:
                return "Download finished";
            default:
                return "Unknown Information";
        }
    }

}
