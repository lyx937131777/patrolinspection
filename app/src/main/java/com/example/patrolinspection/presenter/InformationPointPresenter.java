package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.InformationPointActivity;
import com.example.patrolinspection.LoginActivity;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InformationPointPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public InformationPointPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void register(String id, String name, String longitude, String latitude, String height, String floor){
        progressDialog = ProgressDialog.show(context,"","上传中...");

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
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("IP",responseData);
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((InformationPointActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }else{
                    ((InformationPointActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "信息点创建成功", Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    ((InformationPointActivity) context).finish();
                }
                progressDialog.dismiss();
            }
        });
    }
}
