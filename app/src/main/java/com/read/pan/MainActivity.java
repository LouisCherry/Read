package com.read.pan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.read.pan.adapter.FragmentAdapter;
import com.read.pan.app.ReadApplication;
import com.read.pan.fragment.BookshelfFragment;
import com.read.pan.fragment.StoreFragment;
import com.yamin.reader.activity.FileBrowserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BookshelfFragment.OnFragmentInteractionListener,
        StoreFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindString(R.string.tablayoutTitle1)
    String tablayoutTitle1;
    @BindString(R.string.tablelayoutTitle2)
    String tablayoutTitle2;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    ImageView avatar;
    List<String> tablayoutTitle;
    List<Fragment> viewPagerFragments;
    ReadApplication application;
    @Override
    public void onFragmentInteraction(Uri uri){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bindListener();
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化TabLayout的title
        tablayoutTitle = new ArrayList<>();
        tablayoutTitle.add(tablayoutTitle1);
        tablayoutTitle.add(tablayoutTitle2);
        //初始化ViewPager的数据集
        viewPagerFragments=new ArrayList<>();
        viewPagerFragments.add(new BookshelfFragment());
        viewPagerFragments.add(new StoreFragment());
        application= (ReadApplication) getApplication();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setSupportActionBar(toolbar);
        tabLayout.addTab(tabLayout.newTab().setText(tablayoutTitle.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tablayoutTitle.get(1)));
        //创建ViewPager的adapter
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), viewPagerFragments, tablayoutTitle);
        viewPager.setAdapter(adapter);
        //千万别忘了，关联TabLayout与ViewPager
        //同时也要覆写PagerAdapter的getPageTitle方法，否则Tab没有title
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 注册控件监听事件
     */
    private void bindListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        navView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View navHeadView=navView.getHeaderView(0);
        avatar= (ImageView) navHeadView.findViewById(R.id.nav_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(application.isLogin()){

                }
//                RestClient.userApi().login("123","123").enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        if(response.code()== ResultCode.SUCCESS){
//                            Snackbar.make(v,"登录成功",Snackbar.LENGTH_SHORT).setAction("action",null).show();
//                        }
//                        if(response.code()==ResultCode.USERNOTEXIST){
//                            Snackbar.make(v,"用户名不存在",Snackbar.LENGTH_SHORT).setAction("action",null).show();
//                        }
//                        if(response.code()==ResultCode.PASSWRONG){
//                            Snackbar.make(v,"密码错误",Snackbar.LENGTH_SHORT).setAction("action",null).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    }
//                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //主界面左上角的icon点击反应
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent=null;
        switch (item.getItemId()) {
            case R.id.nav_imp:
                //文件夹导入书籍
                intent=new Intent(this, FileBrowserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_like:
                //喜欢的图书
                break;
            case R.id.nav_download:
                //下载的图书
                break;
            case R.id.nav_setting:
                //设置
                break;
            case R.id.nav_share:
                //分享
                break;
            case R.id.nav_feedback:
                //反馈
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
