package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

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


    public PatrolingPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void updatePatrol(final String patrolRecordID, final boolean isEnd){
        progressDialog = ProgressDialog.show(context,"","上传中...");

        PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
        List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
        patrolRecord.setPointPatrolRecords(patrolPointRecordList);
        //判断跳检和漏检
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
        //若结束则设置结束时间、判断线路状态
        if(isEnd){
            patrolRecord.setEndTime(System.currentTimeMillis());
            if(patrolRecord.getEndTime() < patrolRecord.getEndTimeHead()){
                patrolRecord.setPatrolTimeStatus("abnormal");
            }else if(patrolRecord.getEndTime() > patrolRecord.getEndTimeTail()){
                patrolRecord.setPatrolTimeStatus("overtime");
            }else{
                patrolRecord.setPatrolTimeStatus("normal");
            }
        }
        patrolRecord.save();
        LogUtil.e("patrolingPresenter",patrolRecord.toString());

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
            }
        });
    }
}
