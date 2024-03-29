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

//签到签退
public class SignInOutPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;


    public SignInOutPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    //签到or签退（带图片）
    public void signInOut(final String policeID, String imagePath, final String type, final String attendanceType){
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
                final String responseData = response.body().string();
                LogUtil.e("SignInOutPresenter",responseData);
                //TODO Utility code 000？
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((SignInOutActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                }else{
                    signInOut(policeID,type,attendanceType);
                }

            }
        });
    }

    //签到or签退
    public void signInOut(final String policeID, final String type, final String attendanceType) {
        if(progressDialog == null || !progressDialog.isShowing()){
            progressDialog = ProgressDialog.show(context,"","识别中...");
        }
        String address = HttpUtil.LocalAddress + "/api/attendance";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
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
                final String responseData = response.body().string();
                LogUtil.e("SignInOutPresenter",responseData);
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
