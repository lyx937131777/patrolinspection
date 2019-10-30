package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class EventRecord extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String eventId;
    private String companyId;
    private String policeId;

    private String disposalOperateType;

    private String PatrolRecordId;
    private String pointId;

    private String policeName;
    private String firstTime;
    private String eventName;

//    private String startTime;
//    private String latestTime;

    private List<HandleRecord> disposalRecordInfos;



    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
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

    public String getDisposalOperateType()
    {
        return disposalOperateType;
    }

    public void setDisposalOperateType(String disposalOperateType)
    {
        this.disposalOperateType = disposalOperateType;
    }

    public String getPatrolRecordId()
    {
        return PatrolRecordId;
    }

    public void setPatrolRecordId(String patrolRecordId)
    {
        PatrolRecordId = patrolRecordId;
    }

    public String getPointId()
    {
        return pointId;
    }

    public void setPointId(String pointId)
    {
        this.pointId = pointId;
    }

    public List<HandleRecord> getDisposalRecordInfos()
    {
        return disposalRecordInfos;
    }

    public void setDisposalRecordInfos(List<HandleRecord> disposalRecordInfos)
    {
        this.disposalRecordInfos = disposalRecordInfos;
    }

    public String getPoliceName()
    {
        return policeName;
    }

    public void setPoliceName(String policeName)
    {
        this.policeName = policeName;
    }

    public String getFirstTime()
    {
        return firstTime;
    }

    public void setFirstTime(String firstTime)
    {
        this.firstTime = firstTime;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }
}
