package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


import com.example.patrolinspection.LoginActivity;
import com.example.patrolinspection.MainActivity;
import com.example.patrolinspection.NoticeActivity;
import com.example.patrolinspection.db.Company;
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
    private ProgressDialog progressDialog;

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
        progressDialog = ProgressDialog.show(context,"","登录中...");
        String address = HttpUtil.LocalAddress + "/api/users/login/app";
        HttpUtil.loginRequest(address, phoneID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((LoginActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                int state = response.code();
                final String responseData = response.body().string();
                LogUtil.e("Login",state +" " + responseData);
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((LoginActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                    return;
                }
                SharedPreferences.Editor editor = pref.edit();
                String userID = Utility.getUserID(responseData);
                editor.putString("userID", userID);
                editor.putString("equipmentType",Utility.getEquipmentType(responseData));
                editor.putString("latest", String.valueOf(System.currentTimeMillis()));
                editor.apply();
                LogUtil.e("Login","userID:"+pref.getString("userID",null));
                if(state == 200) {
                    String address = HttpUtil.LocalAddress + "/api/users/me";
                    HttpUtil.getHttp(address,userID, new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            final String responseData = response.body().string();
                            LogUtil.e("Login",responseData);
                            if(Utility.checkString(responseData,"code").equals("500")){
                                ((LoginActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                                .LENGTH_LONG).show();
                                    }
                                });
                                progressDialog.dismiss();
                                return;
                            }
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("companyID", Utility.getCompanyID(responseData));
                            Company company = Utility.getCompany(responseData);
                            editor.putBoolean("isSchool",company.isIsschool());
                            editor.putBoolean("isSchoolLogin",company.isIsschoolLogin());
                            editor.putBoolean("isFace",company.isIsface());
                            editor.putBoolean("isAppAttendance",company.isIsappAttendance());//TODO 经度纬度楼层 公司名
                            editor.putInt("heartbeat",60);//TODO 由系统设置
                            editor.putInt("heartbeatWork",2);
                            editor.putString("latest", String.valueOf(System.currentTimeMillis()));
                            editor.apply();
                            LogUtil.e("Login","companyID:"+pref.getString("companyID",null));
                            Intent intent_login = new Intent(context, MainActivity.class);
                            context.startActivity(intent_login);
                            ((LoginActivity)context).finish();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
