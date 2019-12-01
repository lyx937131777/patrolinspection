package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class HandleRecord extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String eventRecordID;
    private String policeId;
    private String policeName;
    private String photo;
    @SerializedName("operateime")
    private String operateTime;
    private String detail;
    private String reportUnit;
    private String disposalOperateType;

    public String getDisposalOperateType()
    {
        return disposalOperateType;
    }

    public void setDisposalOperateType(String disposalOperateType)
    {
        this.disposalOperateType = disposalOperateType;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getEventRecordID()
    {
        return eventRecordID;
    }

    public void setEventRecordID(String eventRecordID)
    {
        this.eventRecordID = eventRecordID;
    }

    public String getPoliceId()
    {
        return policeId;
    }

    public void setPoliceId(String policeId)
    {
        this.policeId = policeId;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String getOperateTime()
    {
        return operateTime;
    }

    public void setOperateTime(String operateTime)
    {
        this.operateTime = operateTime;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getReportUnit()
    {
        return reportUnit;
    }

    public void setReportUnit(String reportUnit)
    {
        this.reportUnit = reportUnit;
    }

    public String getPoliceName()
    {
        return policeName;
    }

    public void setPoliceName(String policeName)
    {
        this.policeName = policeName;
    }
}
