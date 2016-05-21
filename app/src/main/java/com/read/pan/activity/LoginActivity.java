package com.read.pan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.read.pan.R;
import com.read.pan.app.ReadApplication;
import com.read.pan.entity.User;
import com.read.pan.network.RestClient;
import com.read.pan.network.ResultCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    ReadApplication readApplication;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ProgressDialog progressDialog=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        readApplication= (ReadApplication) getApplication();
        setSupportActionBar(toolbar);
        progressDialog=new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onResume() {
        _loginButton.setEnabled(true);
        super.onResume();
    }

    public void login(final View v) {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setOnCancelListener(cancelListener);
        progressDialog.show();

        String userName = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        RestClient.userApi().login(userName, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == ResultCode.SUCCESS) {
                    User user=response.body();
                    onLoginSuccess(user);
                }
                if (response.code() == ResultCode.USERNOTEXIST) {
                    Snackbar.make(v, "用户名不存在", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    onLoginFailed();
                }
                if (response.code() == ResultCode.PASSWRONG) {
                    Snackbar.make(v, "密码错误", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                    onLoginFailed();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(v, "网络连接失败", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                onLoginFailed();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User user) {
        _loginButton.setEnabled(true);
        readApplication.saveUserInfo(user);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
        progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || email.length() < 3 || email.length() > 10) {
            _emailText.setError("between 3 and 10 alphanumeric characters");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
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
    private DialogInterface.OnCancelListener cancelListener=new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            _loginButton.setEnabled(true);
        }
    };
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
