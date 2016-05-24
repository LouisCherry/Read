package com.read.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.read.pan.MainActivity;
import com.read.pan.R;
import com.read.pan.app.ReadApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoActivity extends AppCompatActivity {
    @BindView(R.id.userInfo_img)
    SimpleDraweeView userInfoImg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_logout)
    AppCompatButton btnLogout;
    private ReadApplication readApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        readApplication = (ReadApplication) getApplication();
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readApplication.isLogin()){
                    readApplication.cleanLoginInfo();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }
        });
        userInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
