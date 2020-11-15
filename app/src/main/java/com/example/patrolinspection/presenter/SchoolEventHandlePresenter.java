package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.SchoolEventHandleActivity;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SchoolEventHandlePresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public SchoolEventHandlePresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void handleSchoolEvent(String id, String type){
        progressDialog = ProgressDialog.show(context,"","上传中...");

        String address = HttpUtil.LocalAddress + "/api/schoolRecord/" + id;
        String userID = pref.getString("userID",null);
        String policeID = pref.getString("schoolPolice","null");
        HttpUtil.updateSchoolEventRecordRequest(address, userID, policeID, type, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((SchoolEventHandleActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("SchoolEventHandlePresenter",responseData);
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((SchoolEventHandleActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }else{
                    ((SchoolEventHandleActivity)context).finish();
                }
                progressDialog.dismiss();
            }
        });
    }
}
