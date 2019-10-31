package com.example.patrolinspection.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.patrolinspection.DataUpdatingActivity;
import com.example.patrolinspection.EventListActivity;
import com.example.patrolinspection.LineListActivity;
import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.PlanListActivity;
import com.example.patrolinspection.PointListActivity;
import com.example.patrolinspection.PoliceListActivity;
import com.example.patrolinspection.ScheduleListActivity;
import com.example.patrolinspection.UploadListActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataUpdatingPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;
    private int count;
    private int photoCount;

    public DataUpdatingPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
        count = 0;
        photoCount = 0;
    }

    public void updateAll(){
        updatePatrolPlan();
        updatePatrolLine();
        updateEvent();
        updatePolice();
        uploadPatrolRecordPhoto();
        new Thread(){
            public void run(){
                try{
                    TimeUnit.SECONDS.sleep(20);
                    if(progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "传输超时！", Toast
                                        .LENGTH_LONG).show();
                            }
                        });
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void updatePolice()
    {
        addCount();
        String address = HttpUtil.LocalAddress + "/api/police/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingSchedule",responsData);
                List<Police> policeList = Utility.handlePoliceList(responsData);
                LitePal.deleteAll(Police.class);
                LitePal.saveAll(policeList);
                reduceCount();
            }
        });
    }



    public void updatePatrolSchedule(){
        addCount();
        String address = HttpUtil.LocalAddress + "/api/schedule/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingSchedule",responsData);
                List<PatrolSchedule> patrolScheduleList = Utility.handlePatrolScheduleList(responsData);
                LitePal.deleteAll(PatrolSchedule.class);
                if(patrolScheduleList != null && patrolScheduleList.size() > 0){
                    for(PatrolSchedule patrolSchedule : patrolScheduleList){
                        String lineID = patrolSchedule.getPatrolLineId();
                        List<PatrolLine> patrolLineList = LitePal.where("internetID = ?",lineID).find(PatrolLine.class);
                        if(patrolLineList.size() > 0){
                            patrolSchedule.save();
                        }
                    }
                }
                reduceCount();
            }
        });
    }

    public void updatePatrolPlan(){
        addCount();
        updatePatrolSchedule();
        String address = HttpUtil.LocalAddress + "/api/plan/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingPlan",responsData);
                List<PatrolPlan> patrolPlanList = Utility.handlePatrolPlanList(responsData);
                LitePal.deleteAll(PatrolPlan.class);
                LitePal.saveAll(patrolPlanList);
                reduceCount();
            }
        });
    }

    public void updateInformationPoint(){
        addCount();
        String address = HttpUtil.LocalAddress + "/api/point/all";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingInformationPoint",responsData);
                List<InformationPoint> informationPointList  = Utility.handleInformationPointList(responsData);
                LitePal.deleteAll(InformationPoint.class);
                LitePal.saveAll(informationPointList);
                reduceCount();
//                for(InformationPoint informationPoint : informationPointList){
//                    LogUtil.e("DataUpdating",informationPoint.getInternetID());
//                    LogUtil.e("DataUpdating",informationPoint.getCompanyId());
//                    LogUtil.e("DataUpdating",informationPoint.getNum());
//                    LogUtil.e("DataUpdating",informationPoint.getName());
//                    LogUtil.e("DataUpdating","=====================================");
//                }
            }
        });
    }

    public void updatePatrolLine(){
        addCount();
        updateInformationPoint();
        String address = HttpUtil.LocalAddress + "/api/line/byEquipment";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingByEquipmentRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingPatrolLine",responsData);
                List<PatrolLine> patrolLineList = Utility.handlePatrolLineList(responsData);
                LitePal.deleteAll(PatrolLine.class);
                LitePal.deleteAll(PatrolIP.class);
//                LitePal.saveAll(patrolLineList);
                for(PatrolLine patrolLine : patrolLineList){
                    if(patrolLine.getPointLineModels() != null){
                        for(PatrolIP patrolIP: patrolLine.getPointLineModels()){
                            patrolIP.setPatrolLineID(patrolLine.getInternetID());
                            LogUtil.e("DataUpdatingPatrolLine",patrolIP.getPatrolLineID() + " " + patrolIP.getOrderNo() + " " + patrolIP.getPointId());
                            patrolIP.save();
                        }
                    }
                    patrolLine.save();
                }
                updatePatrolSchedule();
                reduceCount();
//                patrolLineList.clear();
//                patrolLineList.addAll(LitePal.findAll(PatrolLine.class));
//                for(PatrolLine patrolLine : patrolLineList){
//                    LogUtil.e("DataUpdating",patrolLine.getInternetID());
//                    LogUtil.e("DataUpdating",patrolLine.getCompanyId());
//                    LogUtil.e("DataUpdating",patrolLine.getPatrolLineName());
//                    LogUtil.e("DataUpdating",patrolLine.getPatrolLineNo());
//                    LogUtil.e("DataUpdating",patrolLine.getPatrolLineType());
//                    LogUtil.e("DataUpdating",patrolLine.getPictureType());
//                    LogUtil.e("DataUpdating","-----------------------");
//                    for(String string : patrolLine.getEventInfoIds()){
//                        Event event = LitePal.where("internetID = ?", string).findFirst(Event.class);
//                        LogUtil.e("DataUpdating","   "+event.getInternetID());
//                        LogUtil.e("DataUpdating","   "+event.getCompanyId());
//                        LogUtil.e("DataUpdating","   "+event.getName());
//                        LogUtil.e("DataUpdating","   "+event.getType());
//                        LogUtil.e("DataUpdating","-----------------");
//                    }
//                    LogUtil.e("DataUpdating","-----------------------");
//                    if(patrolLine.getPointLineModels() != null){
//                        for(PatrolIP patrolIP : patrolLine.getPointLineModels()){
//                            LogUtil.e("DataUpdating","      "+patrolIP.getOrderNo());
//                            LogUtil.e("DataUpdating","      "+patrolIP.getPointId());
//                            LogUtil.e("DataUpdating","--------PatrolIP---------");
//                        }
//                    }
//                    LogUtil.e("DataUpdating","=====================================");
//                }

            }
        });
    }

    public void updateEvent(){
        addCount();
        String address = HttpUtil.LocalAddress + "/api/event/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                Log.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingEvent",responsData);
                List<Event> eventList = Utility.handleEventList(responsData);
                LitePal.deleteAll(Event.class);
                LitePal.saveAll(eventList);
                reduceCount();
            }
        });
    }

    public void uploadPatrolRecordPhoto(){
        if(progressDialog == null || !progressDialog.isShowing()){
            progressDialog = ProgressDialog.show(context,"","上传中...");
        }
        List<PatrolRecord> patrolRecordList = LitePal.where("upload = ?","0").find(PatrolRecord.class);
        for(PatrolRecord patrolRecord : patrolRecordList){
            final String patrolRecordID = patrolRecord.getInternetID();
            List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
            for(final PatrolPointRecord patrolPointRecord : patrolPointRecordList){
                if((!patrolPointRecord.getPhotoPath().equals("")) && patrolPointRecord.getPhotoURL().equals("")){
                    LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   ++");
                    photoCount++;
                    String address = HttpUtil.LocalAddress + "/api/file";
                    final String userID = pref.getString("userID",null);
                    HttpUtil.fileRequest(address, userID, new File(patrolPointRecord.getPhotoPath()), new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            e.printStackTrace();
                            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "服务器连接错误", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
                            LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   --");
                            photoCount--;
                            if(photoCount == 0){
                                uploadPatrolRecord();
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            final String responsData = response.body().string();
                            LogUtil.e("DataUpdatingPresenter",responsData);
                            String photo = Utility.checkString(responsData,"msg");
                            patrolPointRecord.setPhotoURL(photo);
                            patrolPointRecord.save();
                            LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   --");
                            photoCount--;
                            if(photoCount == 0){
                                uploadPatrolRecord();
                            }
                        }
                    });
                }
            }
        }
        if(photoCount == 0){
            uploadPatrolRecord();
        }
    }

    public void uploadPatrolRecord(){
        List<PatrolRecord> patrolRecordList = LitePal.where("upload = ?","0").find(PatrolRecord.class);
        for(PatrolRecord patrolRecord : patrolRecordList) {
            addCount();
            final String patrolRecordID = patrolRecord.getInternetID();
            String address = HttpUtil.LocalAddress + "/api/patrolRecord/put";
            String companyID = pref.getString("companyID",null);
            String userID = pref.getString("userID",null);
            HttpUtil.endPatrolRequest(address, userID, companyID, patrolRecordID, new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    e.printStackTrace();
                    ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "服务器连接错误", Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(false);
                    patrolRecord.save();
                    reduceCount();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    final String responsData = response.body().string();
                    LogUtil.e("DataUpdatingPresenter",responsData);
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(true);
                    patrolRecord.save();
                    reduceCount();
                }
            });
        }
        if(count == 0){
            progressDialog.dismiss();
        }
    }


    private void addCount() {
        Log.e("DataUpdatingPresenter","count: "+count+ "   ++");
        if(count == 0 && (progressDialog == null ||!progressDialog.isShowing())){
            progressDialog = ProgressDialog.show(context,"","数据更新中...");
        }
        count++;
    }

    private void reduceCount(){
        Log.e("DataUpdatingPresenter","count: "+count+ "   --");
        count--;
        if(count == 0){
            progressDialog.dismiss();
            if(!(context instanceof  UploadListActivity)){
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "数据更新完毕", Toast.LENGTH_LONG).show();
                    }
                });
            }
            if(context instanceof LineListActivity){
                ((LineListActivity) context).refresh();
            }else if(context instanceof ScheduleListActivity){
                ((ScheduleListActivity) context).refresh();
            }else if(context instanceof PlanListActivity){
                ((PlanListActivity) context).refresh();
            }else if(context instanceof PointListActivity){
                ((PointListActivity) context).refresh();
            }else if(context instanceof EventListActivity){
                ((EventListActivity) context).refresh();
            }else if(context instanceof PoliceListActivity){
                ((PoliceListActivity) context).refresh();
            }else if(context instanceof UploadListActivity){
                ((UploadListActivity) context).refresh();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
