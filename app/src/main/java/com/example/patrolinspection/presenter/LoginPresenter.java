package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


import com.example.patrolinspection.LoginActivity;
import com.example.patrolinspection.MainActivity;
import com.example.patrolinspection.util.CheckUtil;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MyApplication;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginPresenter
{
    private Context context;
    private SharedPreferences pref;

    private String username_text;
    private String password_text;
    private CheckUtil checkUtil;

    public LoginPresenter(Context context, SharedPreferences pref, CheckUtil checkUtil)
    {
        this.context = context;
        this.pref = pref;
        this.checkUtil = checkUtil;
    }

    public void login(String phoneID)
    {
//        if(!checkUtil.checkLogin(username,password))
//            return;
        LogUtil.e("Login", "发送登录请求");
        Intent intent_login = new Intent(MyApplication.getContext(), MainActivity.class);
        context.startActivity(intent_login);
        ((LoginActivity)context).finish();
       // ((LoginActivity)context).finish();
//        ((LoginActivity)context).setSyncFinished(false);
//        String address = HttpUtil.LocalAddress + "/user/login";
//        HttpUtil.loginRequest(address, username_text, password_text, new Callback()
//        {
//            @Override
//            public void onFailure(Call call, IOException e)
//            {
//                e.printStackTrace();
//                ((LoginActivity)context).runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Toast.makeText(context, "服务器连接错误", Toast
//                                .LENGTH_LONG).show();
//                    }
//                });
//                ((LoginActivity)context).setSyncFinished(true);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException
//            {
//                final String responsData = response.body().string();
//                LogUtil.e("Login", responsData);
//                if (Utility.checkMessage(responsData).equals("true"))
//                {
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString("userID", username_text);
//                    editor.putString("password", password_text);
//                    editor.putString("nickname",Utility.checkString(responsData,"nickname"));
//                    editor.putString("latest", String.valueOf(System.currentTimeMillis()));
//                    editor.apply();
//                    Intent intent_login = new Intent(MyApplication.getContext(), MainActivity.class);
//                    context.startActivity(intent_login);
//                    ((LoginActivity)context).finish();
//                } else if(Utility.checkErrorType(responsData).equals("invalid_userID"))
//                {
//                    ((LoginActivity)context).runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Toast.makeText(context, "用户名不存在", Toast
//                                    .LENGTH_LONG).show();
//                        }
//                    });
//                } else if(Utility.checkErrorType(responsData).equals("invalid_userID_or_password"))
//                {
//                    ((LoginActivity)context).runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Toast.makeText(context, "用户名或密码错误", Toast
//                                    .LENGTH_LONG).show();
//                        }
//                    });
//                }else if(Utility.checkErrorType(responsData).equals("query_error"))
//                {
//                    ((LoginActivity)context).runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Toast.makeText(context, "数据库发生错误", Toast
//                                    .LENGTH_LONG).show();
//                        }
//                    });
//                }
//                ((LoginActivity)context).setSyncFinished(true);
//            }
//        });
    }
}
