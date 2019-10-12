package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class PatrolRecord extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String patrolScheduleId;
    private String companyId;
    private String policeId;
    private String equipmentId;
    private String patrolTimeStatus;
    private String patrolPointStatus;
    private String isnonrmal;
    private String startTime;
    private long startTimeLong;
    private long endTime;
    private String date;

    private boolean upload;
    private String duringTime;
    private String state;

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getPatrolScheduleId()
    {
        return patrolScheduleId;
    }

    public void setPatrolScheduleId(String patrolScheduleId)
    {
        this.patrolScheduleId = patrolScheduleId;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getPoliceId()
    {
        return policeId;
    }

    public void setPoliceId(String policeId)
    {
        this.policeId = policeId;
    }

    public String getEquipmentId()
    {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId)
    {
        this.equipmentId = equipmentId;
    }

    public String getPatrolTimeStatus()
    {
        return patrolTimeStatus;
    }

    public void setPatrolTimeStatus(String patrolTimeStatus)
    {
        this.patrolTimeStatus = patrolTimeStatus;
    }

    public String getPatrolPointStatus()
    {
        return patrolPointStatus;
    }

    public void setPatrolPointStatus(String patrolPointStatus)
    {
        this.patrolPointStatus = patrolPointStatus;
    }

    public String getIsnonrmal()
    {
        return isnonrmal;
    }

    public void setIsnonrmal(String isnonrmal)
    {
        this.isnonrmal = isnonrmal;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public long getStartTimeLong()
    {
        return startTimeLong;
    }

    public void setStartTimeLong(long startTimeLong)
    {
        this.startTimeLong = startTimeLong;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDuringTime()
    {
        return duringTime;
    }

    public void setDuringTime(String duringTime)
    {
        this.duringTime = duringTime;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public boolean isUpload()
    {
        return upload;
    }

    public void setUpload(boolean upload)
    {
        this.upload = upload;
    }
}
