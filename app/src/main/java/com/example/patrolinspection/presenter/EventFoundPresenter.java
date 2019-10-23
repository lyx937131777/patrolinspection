package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.patrolinspection.EventFoundActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EventFoundPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public EventFoundPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void postEventRecord(final String policeID, String imagePath,final String eventName,final String patrolRecordID,final String pointID,final String detail){
        progressDialog = ProgressDialog.show(context,"","上传中...");

        if(patrolRecordID != ""){
            PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
            patrolRecord.setIsnonrmal("false");
            patrolRecord.save();
        }

        String address = HttpUtil.LocalAddress + "/api/file";
        final String userID = pref.getString("userID",null);
        HttpUtil.fileRequest(address, userID, new File(imagePath), new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((EventFoundActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("EventFoundPresenter",responsData);
                String photo = Utility.checkString(responsData,"msg");
                String address = HttpUtil.LocalAddress + "/api/eventRecord";
                String companyID = pref.getString("companyID",null);
                String reportUnit = "保安";//TODO 上报单位到底是什么
                long time = System.currentTimeMillis();
                Event event = LitePal.where("name = ?",eventName).findFirst(Event.class);
                String eventID = event.getInternetID();
                HttpUtil.postEventRecordRequest(address, userID, companyID, eventID, policeID, reportUnit, detail, "find",photo, time,
                        patrolRecordID, pointID, new Callback()
                        {
                            @Override
                            public void onFailure(Call call, IOException e)
                            {
                                e.printStackTrace();
                                ((EventFoundActivity)context).runOnUiThread(new Runnable() {
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
                                LogUtil.e("EventFoundPresenter",responsData);
                                if(Utility.checkString(responsData,"code").equals("000")){
                                    ((EventFoundActivity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "发布成功", Toast
                                                    .LENGTH_LONG).show();
                                        }
                                    });
                                    ((EventFoundActivity) context).finish();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });


    }
}
