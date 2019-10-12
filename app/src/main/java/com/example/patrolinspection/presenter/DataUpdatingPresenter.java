package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.DataUpdatingActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataUpdatingPresenter
{
    private Context context;
    private SharedPreferences pref;

    public DataUpdatingPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void updateAll(){
        updatePatrolPlan();
        updatePatrolLine();
        updateEvent();
    }
    public void updatePatrolSchedule(){
        String address = HttpUtil.LocalAddress + "/api/schedule/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((DataUpdatingActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("DataUpdatingSchedule",responsData);
                List<PatrolSchedule> patrolScheduleList = Utility.handlePatrolScheduleList(responsData);
                LitePal.deleteAll(PatrolSchedule.class);
                LitePal.saveAll(patrolScheduleList);
            }
        });
    }

    public void updatePatrolPlan(){
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
                ((DataUpdatingActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("DataUpdatingPlan",responsData);
                List<PatrolPlan> patrolPlanList = Utility.handlePatrolPlanList(responsData);
                LitePal.deleteAll(PatrolPlan.class);
                LitePal.saveAll(patrolPlanList);
            }
        });
    }

    public void updateInformationPoint(){

        String address = HttpUtil.LocalAddress + "/api/point/all";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((DataUpdatingActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("DataUpdatingInformationPoint",responsData);
                List<InformationPoint> informationPointList  = Utility.handleInformationPointList(responsData);
                LitePal.deleteAll(InformationPoint.class);
                LitePal.saveAll(informationPointList);
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
        updateInformationPoint();
        String address = HttpUtil.LocalAddress + "/api/line/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((DataUpdatingActivity)context).runOnUiThread(new Runnable() {
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
                            if(patrolIP.save()){
                                LogUtil.e("DataUpdating","save success!");
                            }else {
                                LogUtil.e("DataUpdating","save fail！！！");
                            }
                        }
                    }
                    patrolLine.save();
                }
                List<PatrolIP> patrolIPList = LitePal.findAll(PatrolIP.class);
                LogUtil.e("DataUpdating","Size: " + patrolIPList.size());
                for(PatrolIP patrolIP : patrolIPList){
                    LogUtil.e("DataUpdatingPatrolLine",patrolIP.getPatrolLineID() + " " + patrolIP.getOrderNo() + " " + patrolIP.getPointId());
                }
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
        String address = HttpUtil.LocalAddress + "/api/event/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((DataUpdatingActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("DataUpdatingEvent",responsData);
                List<Event> eventList = Utility.handleEventList(responsData);
                LitePal.deleteAll(Event.class);
                LitePal.saveAll(eventList);
            }
        });
    }
}
