package com.example.patrolinspection;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private String phoneID;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        loginPresenter = myComponent.loginPresenter();
//        loginPresenter = new LoginPresenter(this,PreferenceManager.getDefaultSharedPreferences(this), new CheckUtil(this));

//        username_text = pref.getString("userID", null);
//        password_text = pref.getString("password", null);
//        if (username_text != null & password_text != null)
//        {
//            Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent_login);
//            finish();
//        }

        phoneID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loginPresenter.login(phoneID);
        TextView textView = findViewById(R.id.text);
        textView.setText("当前设备机号为："+phoneID+"\n未在系统中注册，请注册后重新登录");
        Button login = findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // 登录按钮
            case R.id.login:
                loginPresenter.login("vcqasfecds3243");//待修改 phoneID
                break;
            default:
                break;
        }
    }

    public LoginPresenter getLoginPresenter()
    {
        return loginPresenter;
    }

    public void setLoginPresenter(LoginPresenter loginPresenter)
    {
        this.loginPresenter = loginPresenter;
    }

}