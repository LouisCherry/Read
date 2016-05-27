package com.read.pan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.read.pan.MainActivity;
import com.read.pan.R;
import com.read.pan.app.ReadApplication;
import com.read.pan.entity.User;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {
    @BindView(R.id.userInfo_img)
    ImageView userInfoImg;
//    SimpleDraweeView userInfoImg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_logout)
    AppCompatButton btnLogout;
    private ReadApplication readApplication;
    private Uri imageUri;
    private final int REFRESH=1;
    //无网状态
    private static final int NONET = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case REFRESH:
                    if(readApplication.isLogin()){
                        User user=readApplication.getLoginUser();
                        if(user.getAvatar()!=null){
//                            Uri uri=Uri.parse(user.getAvatar());
//                            userInfoImg.setImageURI(uri);
                            Picasso.with(getBaseContext()).load(user.getAvatar()).into(userInfoImg);
                        }
                    }
                    break;
                case NONET:
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
        User user=readApplication.getLoginUser();
        if(user.getAvatar()!=null){
//            Uri uri=Uri.parse(user.getAvatar());
//            userInfoImg.setImageURI(uri);
            Picasso.with(getBaseContext()).load(user.getAvatar()).into(userInfoImg);
        }
        userInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoActivity.this)
                        .items(R.array.chose_avatar)
                        .itemsCallback(new MaterialDialog.ListCallback(){

                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        startCaemra();
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        startGralley();
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri = null;
        //如果返回的是拍照上传
        if (data == null) {
            uri = imageUri;
        } //返回的是图库上传
        else {
            uri = data.getData();
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    intent.setDataAndType(uri, "image/*");
                    //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
                    intent.putExtra("crop", true);
                    // 设置裁剪尺寸
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 320);
                    intent.putExtra("outputY", 320);
                    intent.putExtra("return-data", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 3);
                    break;
                case 2:
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("crop", true);
                    // 设置裁剪尺寸
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 320);
                    intent.putExtra("outputY", 320);
                    intent.putExtra("return-data", true);
                    // 创建一个文件夹对象，赋值为外部存储器的目录
                    File sdcardDir = Environment.getExternalStorageDirectory();
                    // 得到一个路径，内容是sdcard的文件夹路径和名字
                    String path = sdcardDir.getPath()
                            + "/Read/images";

                    File photofile = new File(path);
                    uri = Uri.fromFile(new File(photofile, "myheadimg.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 3);

                    break;
                case 3:
                    if (data != null) {
                        Bitmap myBitmap = data.getParcelableExtra("data");
                        updateAvatar();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    //更改头像
    public void updateAvatar(){
        File sdcardDir = Environment.getExternalStorageDirectory();
        String picPath = sdcardDir.getPath()
                + "/Read/images/myheadimg.jpg";
        if(readApplication.isLogin()){
            final User user=readApplication.getLoginUser();
            File file=new File(picPath);
            RequestBody idRequestBody =RequestBody.create(MediaType.parse("text/plain"), user.getUserId());
            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
            RestClient.userApi().update(null,null,idRequestBody,photoRequestBody).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.code()==ResultCode.SUCCESS){
                        Snackbar.make(getWindow().getDecorView(), "上传成功", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        User user1=response.body();
                        readApplication.updateUserInfo(user1);
                        Message msg=new Message();
                        msg.what = REFRESH;
                        mHandler.sendMessage(msg);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Snackbar.make(getWindow().getDecorView(), "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    Log.i("upload",t.getMessage());
                }
            });
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
    //开始拍照
    private void startCaemra() {
        // 指定照相机拍照后图片的存储路径，这里存储在自己定义的文件夹下
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            // 得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath()
                    + "/Read/images";
            File photofile = new File(path);
            if (!photofile.exists()) {
                // 若不存在，创建目录，可以在应用启动的时候创建
                photofile.mkdirs();
            } else {
                imageUri = Uri.fromFile(new File(photofile,
                        "myheadimg.jpg"));
                // 拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                // 保存照片在自定义的文件夹下面
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        } else {
            Snackbar.make(getWindow().getDecorView(),"SD卡不可用",Snackbar.LENGTH_SHORT).show();
            return;

        }
    }
    //选择图库
    private void startGralley() {
        try {
            // 选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
            // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}
