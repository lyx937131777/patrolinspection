package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.SignInOutActivity;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignInOutPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;


    public SignInOutPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void signInOut(String policeID, String imagePath, final String type, final String attendanceType){
        progressDialog = ProgressDialog.show(context,"","识别中...");

        String address = HttpUtil.LocalAddress + "/api/police/face";
        String userID = pref.getString("userID",null);
        HttpUtil.faceRecognitionRequest(address, userID, policeID, type, new File(imagePath), new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((SignInOutActivity)context).runOnUiThread(new Runnable() {
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
                final String responsData = response.body().string();
                LogUtil.e("SignInOutPresenter",responsData);
                //待修改 Utility code 000？
                if(Utility.checkString(responsData,"code").equals("500")){
                    ((SignInOutActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responsData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                }else{
                    String address = HttpUtil.LocalAddress + "/api/attendance";
                    String companyID = pref.getString("companyID",null);
                    String userID = pref.getString("userID",null);
                    String policeID = "5";//待修改
                    HttpUtil.attendanceRequest(address, userID, companyID, policeID, attendanceType, type, new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            e.printStackTrace();
                            ((SignInOutActivity)context).runOnUiThread(new Runnable() {
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
                            final String responsData = response.body().string();
                            LogUtil.e("SignInOutPresenter",responsData);
                            ((SignInOutActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, MapUtil.getFaceType(type)+"成功", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
                            ((SignInOutActivity) context).finish();
                            progressDialog.dismiss();
                        }
                    });
                }

            }
        });
    }
}
