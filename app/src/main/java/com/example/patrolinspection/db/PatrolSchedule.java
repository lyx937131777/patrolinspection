package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

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
}
