package com.example.patrolinspection.db;

import com.example.patrolinspection.util.Utility;
import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

//排班 对应一条线路和对应的时间 对应一个计划
public class PatrolSchedule extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String companyId;
    private String startTime;//时分 24小时制
    private String endTime;//时分 24小时制
    private String errorRange;//误差范围
    private String patrolLineId;
    private String patrolPlanId;

    public PatrolSchedule(){

    }

    public PatrolSchedule(String internetID, String companyId, String startTime, String endTime,
                          String errorRange, String patrolLineId, String patrolPlanId)
    {
        this.internetID = internetID;
        this.companyId = companyId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorRange = errorRange;
        this.patrolLineId = patrolLineId;
        this.patrolPlanId = patrolPlanId;
    }

    public int getDuringMin(){
        int h = (Integer.parseInt(endTime.split(":")[0])+24-Integer.parseInt(startTime.split(":")[0]))%24;
        int m = (Integer.parseInt(endTime.split(":")[1])-Integer.parseInt(startTime.split(":")[1]));
        return h*60+m ;
    }


    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getErrorRange()
    {
        return errorRange;
    }

    public void setErrorRange(String errorRange)
    {
        this.errorRange = errorRange;
    }

    public String getPatrolLineId()
    {
        return patrolLineId;
    }

    public void setPatrolLineId(String patrolLineId)
    {
        this.patrolLineId = patrolLineId;
    }

    public String getPatrolPlanId()
    {
        return patrolPlanId;
    }

    public void setPatrolPlanId(String patrolPlanId)
    {
        this.patrolPlanId = patrolPlanId;
    }

    public boolean isTwoDay(){
        return Integer.parseInt(endTime.split(":")[0]) < Integer.parseInt(startTime.split(":")[0]);
    }

    public long getStartTimeHead(){
        Date date = new Date();
        String time =  Utility.dateToString(date,"yyyy-MM-dd")+ " "+startTime;
        Date date2 = Utility.stringToDate(time,"yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.add(Calendar.MINUTE, -Integer.parseInt(errorRange));
        return calendar.getTimeInMillis();
    }

    public long getEndLimit(){
        Date date = new Date();
        if(isTwoDay()){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,1);
            date = c.getTime();
        }
        String time =  Utility.dateToString(date,"yyyy-MM-dd")+ " "+endTime;
        Date date2 = Utility.stringToDate(time,"yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.add(Calendar.MINUTE, Integer.parseInt(errorRange));
        return calendar.getTimeInMillis();
    }

    public String getLineName(){
        PatrolLine patrolLine = LitePal.where("internetID = ?",patrolLineId).findFirst(PatrolLine.class);
        if(patrolLine == null){
            return "线路已被删除";
        }
        return patrolLine.getPatrolLineName();
    }

    public String getPlanType(){
        PatrolPlan patrolPlan = LitePal.where("internetID = ?",patrolPlanId).findFirst(PatrolPlan.class);
        if(patrolPlan == null){
            return "计划已被删除";
        }else{
            return patrolPlan.getPatrolPlanType();
        }
    }
}
