package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.InformationPointActivity;
import com.example.patrolinspection.LoginActivity;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InformationPointPresenter
{
    private Context context;
    private SharedPreferences pref;
    public InformationPointPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void register(String id, String name, String longitude, String latitude, String height, String floor){
        String address = HttpUtil.LocalAddress + "/api/point";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.registerIPRequest(address, userID, companyID, id, name, longitude, latitude, height, floor, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((InformationPointActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("IP",responsData);
                ((InformationPointActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "信息点创建成功", Toast
                                .LENGTH_LONG).show();
                    }
                });
                ((InformationPointActivity) context).finish();
            }
        });
    }
}
