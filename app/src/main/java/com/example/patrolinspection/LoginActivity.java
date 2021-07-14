package com.example.patrolinspection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.LoginPresenter;
import com.example.patrolinspection.util.LogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//登录界面
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private String phoneID;
    private TextView textView;

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
        textView = findViewById(R.id.text);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            }else{
                checkAllPermissions();
            }
        }else {
            checkAllPermissions();
        }


        Button login = findViewById(R.id.login);
        login.setOnClickListener(this);
        LogUtil.e("LoginActivity", "手机型号： " + Build.MODEL);
        LogUtil.e("LoginActivity", "手机品牌： " + Build.BRAND);
        LogUtil.e("LoginActivity", "手机ID： " + Build.SERIAL);
        LogUtil.e("LoginActivity", "安卓版本： " + Build.VERSION.RELEASE);
        LogUtil.e("LoginActivity", "API版本： " + Build.VERSION.SDK_INT);
    }

    private void checkAllPermissions()
    {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.INTERNET);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
//        {
//            permissionList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
//        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else{
            readSerial();
        }
    }

    private void readSerial()
    {
        phoneID = Build.SERIAL.toUpperCase();
        LogUtil.e("LoginActivity","phoneID 1 : "+phoneID);
        if (isDefault(phoneID) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q )
        {
            phoneID = Build.getSerial().toUpperCase();
            LogUtil.e("LoginActivity","phoneID 2 : "+phoneID);
        }
        if(isDefault(phoneID)){
            phoneID = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            LogUtil.e("LoginActivity","phoneID 3 : "+phoneID);
        }

//        phoneID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loginPresenter.login(phoneID);
        textView.setText("当前设备机号为：" + phoneID + "\n未在系统中注册，请注册后重新登录");
    }

    private boolean isDefault(String s){
        return s.equals("UNKNOWN") || s.equals("0123456789ABCDEF");
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
//                loginPresenter.login("vcqasfecds3243");//TODO phoneID
                loginPresenter.login(phoneID);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "请打开弹窗（悬浮窗）权限！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 10);
                }else {
                    checkAllPermissions();
                }
            }else {
                checkAllPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSerial();
                } else {
                    Toast.makeText(this, "请通过所有权限！", Toast.LENGTH_LONG).show();
                    checkAllPermissions();
                }
                break;
        }
    }
}