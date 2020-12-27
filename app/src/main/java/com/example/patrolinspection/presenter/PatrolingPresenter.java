package com.example.patrolinspection.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PointPhotoRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//巡检执行中
public class PatrolingPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;
    private int count;


    public PatrolingPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
        count = 0;
    }

    //上传数据 并屏蔽其他操作防止误触
    public void updatePatrol(final String patrolRecordID, final boolean isEnd)
    {
        progressDialog = ProgressDialog.show(context,"","上传中...");
        LogUtil.e("PatrolingPresenter","开始上传"+ TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        new Thread(){
            public void run(){
                updatePatrolInThread(patrolRecordID,isEnd);
            }
        }.start();
    }

    //上传巡检数据（先上传图片）
    public void updatePatrolInThread(final String patrolRecordID, final boolean isEnd){

        PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class,true);
        List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
        for(final PatrolPointRecord patrolPointRecord : patrolPointRecordList){
            for(final PointPhotoRecord pointPhotoRecord : patrolPointRecord.getPointPhotoInfos()){
                if(pointPhotoRecord.getPhotoURL().equals("")){
                    LogUtil.e("PatrolingPresenter","count: "+count+ "   ++");
                    count++;
                    String address = HttpUtil.LocalAddress + "/api/file";
                    final String userID = pref.getString("userID",null);
                    HttpUtil.fileRequest(address, userID, new File(pointPhotoRecord.getPhotoPath()), new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            e.printStackTrace();
                            ((PatrolingActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "服务器连接错误", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
                            LogUtil.e("PatrolingPresenter","count: "+count+ "   --");
                            count--;
                            if(count == 0){
                                update(patrolRecordID,isEnd);
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            final String responseData = response.body().string();
                            LogUtil.e("PatrolingPresenter",responseData);
                            String photo = Utility.checkString(responseData,"msg");
                            pointPhotoRecord.setPhotoURL(photo);
                            pointPhotoRecord.save();
                            patrolPointRecord.save();
                            LogUtil.e("PatrolingPresenter","count: "+count+ "   --");
                            count--;
                            if(count == 0){
                                update(patrolRecordID,isEnd);
                            }
                        }
                    });
                }
            }
        }

        //若结束则设置结束时间、判断跳检和漏检、判断线路状态
        if(isEnd){
            patrolRecord.setPatrolPointStatus("normal");
            for(int i = 0; i < patrolPointRecordList.size(); i++){
                if(!patrolPointRecordList.get(i).getOrderNo().equals(String.valueOf(i+1))){
                    patrolRecord.setPatrolPointStatus("jump");
                }
            }
            for(PatrolPointRecord patrolPointRecord : patrolPointRecordList){
                if(patrolPointRecord.getTime() == 0){
                    patrolRecord.setPatrolPointStatus("miss");
                }
            }
            patrolRecord.setEndTime(System.currentTimeMillis());
            if(patrolRecord.getPlanType().equals("freeSchedule")){
                patrolRecord.setPatrolTimeStatus("normal");
            }else{
                if(patrolRecord.getStartTimeLong() < patrolRecord.getStartTimeHead()){
                    if(patrolRecord.getEndTime() <= patrolRecord.getRealEndLimit()){
                        patrolRecord.setPatrolTimeStatus("abnormal");
                    }else{
                        patrolRecord.setPatrolTimeStatus("abnormal_overtime");
                    }
                }else{
                    if(patrolRecord.getEndTime() <= patrolRecord.getRealEndLimit()){
                        patrolRecord.setPatrolTimeStatus("normal");
                    }else{
                        patrolRecord.setPatrolTimeStatus("overtime");
                    }
                }
            }
        }
        patrolRecord.save();
        LogUtil.e("patrolingPresenter",patrolRecord.toString());
        if(count == 0){
            update(patrolRecordID,isEnd);
        }
    }

    //上传巡检数据
    public void update(final String patrolRecordID, final boolean isEnd){
        String address = HttpUtil.LocalAddress + "/api/patrolRecord/put";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.endPatrolRequest(address, userID, companyID, patrolRecordID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((PatrolingActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                if(isEnd){
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(false);
                    patrolRecord.setState("已结束");
                    patrolRecord.save();
                    ((PatrolingActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "巡检结束，暂未上传。", Toast.LENGTH_LONG).show();
                        }
                    });
                    ((PatrolingActivity)context).setResult(Activity.RESULT_OK)  ;
                    ((PatrolingActivity)context).finish();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("PatrolingPresenter",responseData);
                if(isEnd){
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(true);
                    patrolRecord.setState("已结束");
                    patrolRecord.save();
                    ((PatrolingActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "巡检结束，已上传。", Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    ((PatrolingActivity)context).setResult(Activity.RESULT_OK)  ;
                    ((PatrolingActivity)context).finish();

                }
                progressDialog.dismiss();
                LogUtil.e("PatrolingPresenter","上传完毕"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
            }
        });
    }


}
