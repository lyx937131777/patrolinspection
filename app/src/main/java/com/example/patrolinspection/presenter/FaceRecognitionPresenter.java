package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.FaceRecognitionActivity;
import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.SignInOutActivity;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FaceRecognitionPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public FaceRecognitionPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void startPatrol(final String policeID, String imagePath, final String scheduleId){
        progressDialog = ProgressDialog.show(context,"","识别中...");

        String address = HttpUtil.LocalAddress + "/api/police/face";
        String userID = pref.getString("userID",null);
        HttpUtil.faceRecognitionRequest(address, userID, policeID, "patrol", new File(imagePath), new Callback()
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
                    ((FaceRecognitionActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                }else{
                    startPatrol(policeID,scheduleId);
                }

            }
        });
    }

    public void startPatrol(String policeID, String scheduleId){
        if(progressDialog == null || !progressDialog.isShowing()){
            progressDialog = ProgressDialog.show(context,"","识别中...");
        }

        String address = HttpUtil.LocalAddress + "/api/patrolRecord";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        long time = System.currentTimeMillis();
        HttpUtil.startPatrolRequset(address, userID, companyID, policeID, time, scheduleId, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((FaceRecognitionActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("FaceRecognitionPresenter",responseData);
                PatrolRecord patrolRecord = Utility.handlePatrolRecord(responseData);
                LogUtil.e("FaceRecognitionPresenter","id:"+patrolRecord.getInternetID() + "  "+patrolRecord.getPatrolScheduleId()+" "+patrolRecord.getStartTime());
                Date date = TimeUtil.stringToDate(patrolRecord.getStartTime());
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd  HH:mm:ss");
                LogUtil.e("FaceRecognitionPresenter","startTime:" + ft.format(date));
                patrolRecord.setStartTimeLong(date.getTime());
                patrolRecord.setUpload(false);
                patrolRecord.setState("进行中");
                patrolRecord.save();

                LogUtil.e("FaceRecognitionPresenter","long:"+patrolRecord.getStartTimeLong());
                Intent intent = new Intent(context, PatrolingActivity.class);
                intent.putExtra("record",patrolRecord.getInternetID());
                ((FaceRecognitionActivity)context).startActivityForResult(intent,0);
                progressDialog.dismiss();
            }
        });
    }
}
