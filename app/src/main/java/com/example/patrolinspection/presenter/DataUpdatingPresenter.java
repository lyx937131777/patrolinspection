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
import com.example.patrolinspection.EventRecordListActivity;
import com.example.patrolinspection.LineListActivity;
import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.PlanListActivity;
import com.example.patrolinspection.PointListActivity;
import com.example.patrolinspection.PoliceListActivity;
import com.example.patrolinspection.ScheduleListActivity;
import com.example.patrolinspection.UploadListActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.PointPhotoRecord;
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
        uploadEventRecord();
        new Thread(){
            public void run(){
                try{
                    TimeUnit.SECONDS.sleep(30);
                    if(progressDialog != null && progressDialog.isShowing()){
                        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "传输超时！", Toast.LENGTH_LONG).show();
                            }
                        });
                        progressDialog.dismiss();
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
                LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingPolice",responseData);
                List<Police> policeList = Utility.handlePoliceList(responseData);
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
                LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingSchedule",responseData);
                List<PatrolSchedule> patrolScheduleList = Utility.handlePatrolScheduleList(responseData);
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
                LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingPlan",responseData);
                List<PatrolPlan> patrolPlanList = Utility.handlePatrolPlanList(responseData);
                LitePal.deleteAll(PatrolPlan.class);
                LitePal.saveAll(patrolPlanList);
                reduceCount();
            }
        });
    }

    public void updateInformationPoint(){
        addCount();
        String address = HttpUtil.LocalAddress + "/api/point/list";
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
                LogUtil.e("DataUpdatingPresenterInformation","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingInformationPoint",responseData);
                List<InformationPoint> informationPointList  = Utility.handleInformationPointList(responseData);
                if(informationPointList != null){
                    LogUtil.e("DataUpdatingInformationPoint","信息点数量："+informationPointList.size());
                    for(InformationPoint informationPoint : informationPointList){
                        LogUtil.e("DataUpdatingInformationPoint","internetId: "+informationPoint.getInternetID()+"  name: "+informationPoint.getName());
                    }
                }
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
                LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingPatrolLine",responseData);
                List<PatrolLine> patrolLineList = Utility.handlePatrolLineList(responseData);
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
                LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                count--;
                if(count == 0){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("DataUpdatingEvent",responseData);
                List<Event> eventList = Utility.handleEventList(responseData);
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
                for(final PointPhotoRecord pointPhotoRecord : patrolPointRecord.getPointPhotoInfos()){
                    if(pointPhotoRecord.getPhotoURL().equals("")){
                        LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   ++");
                        photoCount++;
                        String address = HttpUtil.LocalAddress + "/api/file";
                        final String userID = pref.getString("userID",null);
                        HttpUtil.fileRequest(address, userID, new File(pointPhotoRecord.getPhotoPath()), new Callback()
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
                                final String responseData = response.body().string();
                                LogUtil.e("DataUpdatingPresenter",responseData);
                                String photo = Utility.checkString(responseData,"msg");
                                pointPhotoRecord.setPhotoURL(photo);
                                pointPhotoRecord.save();
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
                    LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                    count--;
                    if(count == 0){
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    final String responseData = response.body().string();
                    LogUtil.e("DataUpdatingPresenter",responseData);
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

    public void uploadEventRecord(){
        if(progressDialog == null || !progressDialog.isShowing()){
            progressDialog = ProgressDialog.show(context,"","上传中...");
        }

        List<EventRecord> eventRecordList = LitePal.where("upload = ?","0").find(EventRecord.class);
        for(final EventRecord eventRecord : eventRecordList){
            addCount();
            if(!eventRecord.getPhotoPath().equals("") && eventRecord.getPhotoURL().equals("")){
                String address = HttpUtil.LocalAddress + "/api/file";
                final String userID = pref.getString("userID",null);
                HttpUtil.fileRequest(address, userID, new File(eventRecord.getPhotoPath()), new Callback()
                {
                    @Override
                    public void onFailure(Call call, IOException e)
                    {
                        e.printStackTrace();
                        ((AppCompatActivity) context).runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(context, "服务器连接错误", Toast
                                        .LENGTH_LONG).show();
                            }
                        });
                        LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                        count--;
                        if(count == 0){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        final String responseData = response.body().string();
                        LogUtil.e("EventFoundPresenter",responseData);
                        String photo = Utility.checkString(responseData,"msg");
                        eventRecord.setPhotoURL(photo);
                        eventRecord.save();
                        postEventRecord(eventRecord);
                    }
                });
            }else{
                postEventRecord(eventRecord);
            }
        }
        if(count == 0){
            progressDialog.dismiss();
        }
    }

    public void postEventRecord(final EventRecord eventRecord){
        String userID = pref.getString("userID",null);
        String photo = eventRecord.getPhotoURL();
        String address = HttpUtil.LocalAddress + "/api/eventRecord";
        String companyID = pref.getString("companyID",null);
        String reportUnit = eventRecord.getReportUnit();
        String disposalOperateType = eventRecord.getDisposalOperateType();
        long time = eventRecord.getTime();
        String eventID = eventRecord.getEventId();
        String policeID = eventRecord.getPoliceId();
        String detail = eventRecord.getDetail();
        String patrolRecordID = eventRecord.getPatrolRecordId();
        String pointID = eventRecord.getPointId();
        HttpUtil.postEventRecordRequest(address, userID, companyID, eventID, policeID, reportUnit, detail, disposalOperateType,photo, time,
                patrolRecordID, pointID, new Callback()
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
                        LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                        count--;
                        if(count == 0){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        final String responseData = response.body().string();
                        LogUtil.e("EventFoundPresenter",responseData);
                        if(Utility.checkString(responseData,"code").equals("000")){
                            eventRecord.setUpload(true);
                            eventRecord.save();
                            reduceCount();
                        } else{
                            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast.LENGTH_LONG).show();
                                }
                            });
                            LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
                            count--;
                            if(count == 0){
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void addCount() {
        LogUtil.e("DataUpdatingPresenter","count: "+count+ "   ++");
        if(count == 0 && (progressDialog == null ||!progressDialog.isShowing())){
            ((AppCompatActivity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog = ProgressDialog.show(context, "", "数据更新中...");
                }
            });
        }
        count++;
    }

    private void reduceCount(){
        LogUtil.e("DataUpdatingPresenter","count: "+count+ "   --");
        count--;
        if(count == 0){
            progressDialog.dismiss();
            if(!(context instanceof UploadListActivity) && !(context instanceof EventRecordListActivity)){
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
            }else if(context instanceof EventRecordListActivity){
                ((EventRecordListActivity) context).refresh();
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
