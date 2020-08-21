package com.example.patrolinspection.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.EventFoundActivity;
import com.example.patrolinspection.EventHandleActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EventHandlePresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public EventHandlePresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void postHandleRecord(final String policeID, String imagePath,final String eventRecordID, final String type,final String report,final String detail){
        ((EventHandleActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialog.show(context,"","上传中...");
            }
        });
        String address = HttpUtil.LocalAddress + "/api/file";
        final String userID = pref.getString("userID",null);
        HttpUtil.fileRequest(address, userID, new File(imagePath), new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((EventHandleActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("EventHandlePresenter",responsData);
                String photo = Utility.checkString(responsData,"msg");
                String address = HttpUtil.LocalAddress + "/api/eventRecord/disposal";
                long time = System.currentTimeMillis();
                HttpUtil.postHandleRecordRequest(address, userID, eventRecordID, policeID, report, detail, type,photo, time, new Callback()
                        {
                            @Override
                            public void onFailure(Call call, IOException e)
                            {
                                e.printStackTrace();
                                ((EventHandleActivity)context).runOnUiThread(new Runnable() {
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
                                LogUtil.e("EventHandlePresenter",responsData);
                                if(Utility.checkString(responsData,"code").equals("000")){
                                    ((EventHandleActivity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "发布成功", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    ((EventHandleActivity) context).setResult(Activity.RESULT_OK);
                                    ((EventHandleActivity) context).finish();
                                }else{
                                    ((EventHandleActivity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, Utility.checkString(responsData,"msg"), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }
}
