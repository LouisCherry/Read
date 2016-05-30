package com.read.pan.activity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
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
import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.database.DbTags;
import com.yamin.reader.utils.ToolUtils;

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
    @BindView(R.id.book_author)
    TextView bookAuthor;
    @BindView(R.id.book_size)
    TextView bookSize;
    @BindView(R.id.book_introduction)
    TextView bookIntroduction;
    private String bookId;
    ReadApplication readApplication;
    private int ifCollect = 0;
    private Book book;
    private DownloadManager downloadManager;
    private ContentResolver resolver;
    private final String ROOT_PATH = Environment.getExternalStorageDirectory()
            .getPath();
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
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_deatil);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        resolver=getContentResolver();
        readApplication = (ReadApplication) getApplication();
        bookId = getIntent().getStringExtra("bookId");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(book.getPath()));
                File fold = new File("Read/download");
                if (fold.isDirectory() && !fold.exists())
                    fold.mkdirs();
                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_MOBILE
                                | DownloadManager.Request.NETWORK_WIFI)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setMimeType("application/com.read.pan.download")
                        .setAllowedOverRoaming(false) // 缺省是true
                        .setTitle("下载") // 用于信息查看
                        .setDescription("下载" + book.getBookName()) // 用于信息查看
                        .setDestinationInExternalPublicDir(
                                "Read/download/" + bookId,
                                StringUtil.getBookInfo(book.getPath(), book.getBookName()));
                // 把当前下载的ID保存起来
                String bookName=book.getBookName();
                String bookPath=ROOT_PATH+"/Read/download/"+book.getBookId()+"/"+StringUtil.getBookInfo(book.getPath(), book.getBookName());
                File file=new File(bookPath);
                if(file.exists()){
                    com.yamin.reader.model.Book b = DbDataOperation.queryBook(resolver, DbTags.FIELD_BOOK_NAME,bookName);
                    if(b==null){
                        com.yamin.reader.model.Book book = new com.yamin.reader.model.Book();
                        book.setBookName(bookName);
                        book.setBookPath(bookPath);
                        String bookSize=null;
                        if(bookPath!=null){
                            File file1=new File(bookPath);
                            if(file1.isFile()){
                                bookSize= ToolUtils.FormetFileSize(file1.length());
                            }
                        }
                        book.setBookSize(bookSize);
                        DbDataOperation.insertToBookInfo(resolver, book);
                    }
                    Snackbar.make(view, "已下载", Snackbar.LENGTH_LONG)
                            .setAction("取消下载", null).show();
                }else{
                    final long mDownloadId = downloadManager.enqueue(request); // 加入下载队列
                    SharedPreferences sPreferences = getSharedPreferences("downloadcomplete", 0);
                    sPreferences.edit().putLong("refernece", mDownloadId)
                            .putString("bookName",bookName)
                            .putString("bookPath",bookPath).commit();
                    startQuery(mDownloadId);
                    Snackbar.make(view, "开始下载", Snackbar.LENGTH_LONG)
                            .setAction("取消下载", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadManager.remove(mDownloadId);
                                }
                            }).show();
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_collect:
                        collect(item, bookId);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void refreshData(String bookId) {
        String userId = "";
        if (readApplication.isLogin()) {
            userId = readApplication.getLoginUser().getUserId();
        }
        RestClient.bookApi().book(bookId, userId).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.code() == ResultCode.SUCCESS) {
                    book = response.body();
                    Picasso.with(getBaseContext()).load(book.getCover()).into(bookDetailImg);
                    bookTitle.setText(book.getBookName());
                    bookAuthor.setText(book.getAuthor());
                    bookSize.setText(ToolUtils.FormetFileSize(book.getSize()));
                    bookIntroduction.setText(book.getIntroduction());
                    ifCollect = book.getIfCollect();
                    if (ifCollect == 1) {
                        MenuView.ItemView itemView = (MenuView.ItemView) findViewById(R.id.action_collect);
                        itemView.setIcon(getResources().getDrawable(R.drawable.general__shared__favour_selected));
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

    private void collect(final MenuItem menuItem, final String bookId) {
        if (readApplication.isLogin()) {
            String userId = readApplication.getLoginUser().getUserId();
            if (userId != null) {
                if(ifCollect==1){
                    RestClient.userApi().deleteCollect(userId,bookId).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            int code = response.code();
                            if (code == ResultCode.SUCCESS) {
                                Snackbar.make(getWindow().getDecorView(), "取消收藏", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                                menuItem.setIcon(R.drawable.general__shared__favour_normal);
                            }
                            if (code == ResultCode.NOBOOK) {
                                Snackbar.make(getWindow().getDecorView(), "取消收藏失败，该书籍不存在", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Snackbar.make(getWindow().getDecorView(), "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        }
                    });
                }else{
                    RestClient.userApi().collect(userId, bookId).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == ResultCode.SUCCESS) {
                                menuItem.setIcon(R.drawable.general__shared__favour_selected);
                                String name="收藏成功";
                                name=name.replaceAll(" ","");
                                Snackbar.make(getWindow().getDecorView(),name , Snackbar.LENGTH_SHORT).setAction("action", null).show();
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
                }
            } else {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        } else {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        refreshData(bookId);
        getMenuInflater().inflate(R.menu.menu_book_deatil, menu);
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

    }
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
            if (!c.moveToFirst()) {
                c.close();
                return;
            }
            c.close();
        }
    }
}
