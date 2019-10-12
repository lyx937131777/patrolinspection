package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.FaceRecognitionActivity;
import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FaceRecognitionPresenter
{
    private Context context;
    private SharedPreferences pref;

    public FaceRecognitionPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void faceRecognition(){

    }

    public void startPatrol(String scheduleId){
        String address = HttpUtil.LocalAddress + "/api/patrolRecord";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        String policeID = "5";//待修改
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("FaceRecognitionPresenter",responsData);
                PatrolRecord patrolRecord = Utility.handlePatrolRecord(responsData);
                LogUtil.e("FaceRecognitionPresenter","id:"+patrolRecord.getInternetID() + "  "+patrolRecord.getPatrolScheduleId()+" "+patrolRecord.getStartTime());
                Date date = Utility.stringToDate(patrolRecord.getStartTime());
                patrolRecord.setStartTimeLong(date.getTime());
                patrolRecord.setUpload(false);
                LogUtil.e("FaceRecognitionPresenter","long:"+patrolRecord.getStartTimeLong());
                patrolRecord.setState("进行中");
                patrolRecord.save();
                Intent intent = new Intent(context, PatrolingActivity.class);
                intent.putExtra("record",patrolRecord.getInternetID());
                context.startActivity(intent);
            }
        });
    }
}
