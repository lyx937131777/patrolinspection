package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    public void updatePatrol(final String patrolRecordID, final boolean isEnd){
        progressDialog = ProgressDialog.show(context,"","上传中...");

        PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
        List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
        for(final PatrolPointRecord patrolPointRecord : patrolPointRecordList){
            if((!patrolPointRecord.getPhotoPath().equals("")) && patrolPointRecord.getPhotoURL().equals("")){
                Log.e("PatrolingPresenter","count: "+count+ "   ++");
                count++;
                String address = HttpUtil.LocalAddress + "/api/file";
                final String userID = pref.getString("userID",null);
                HttpUtil.fileRequest(address, userID, new File(patrolPointRecord.getPhotoPath()), new Callback()
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
                        Log.e("PatrolingPresenter","count: "+count+ "   --");
                        count--;
                        if(count == 0){
                            update(patrolRecordID,isEnd);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        final String responsData = response.body().string();
                        LogUtil.e("EventFoundPresenter",responsData);
                        String photo = Utility.checkString(responsData,"msg");
                        patrolPointRecord.setPhotoURL(photo);
                        patrolPointRecord.save();
                        Log.e("PatrolingPresenter","count: "+count+ "   --");
                        count--;
                        if(count == 0){
                            update(patrolRecordID,isEnd);
                        }
                    }
                });
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
        patrolRecord.save();
        LogUtil.e("patrolingPresenter",patrolRecord.toString());
        Log.e("PatrolingPresenter","此时的count: "+count+ "   ");
        if(count == 0){
            update(patrolRecordID,isEnd);
        }
    }

    public void update(final String patrolRecordID, final boolean isEnd){
        Log.e("PatrolingPresenter","update开始时的count: "+count+ "   ");
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
                }
                progressDialog.dismiss();
                Log.e("PatrolingPresenter","update结束时的count: "+count+ "   ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("PatrolingPresenter",responsData);
                if(isEnd){
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(true);
                    patrolRecord.setState("已结束");
                    patrolRecord.save();
                    ((PatrolingActivity)context).finish();
                }
                progressDialog.dismiss();
                Log.e("PatrolingPresenter","update结束时的count: "+count+ "   ");
            }
        });
    }


}
