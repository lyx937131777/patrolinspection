package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
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

    private String patrolRecordId;
    private String pointId;

    private String policeName;
    private String firstTime;
    private String lastTime;
    private String eventName;

//    private String startTime;
//    private String latestTime;

    private List<HandleRecord> disposalRecordInfos;

    private boolean upload;
    private long time;
    private String reportUnit;
    private String detail;
    private String photoPath;
    private String photoURL;

    public EventRecord(){

    }

    public EventRecord(String eventId, String policeId, String disposalOperateType,
                       String patrolRecordId, String pointId,  long time, String reportUnit,
                       String detail, String photoPath, String photoURL,boolean upload)
    {
        this.eventId = eventId;
        this.policeId = policeId;
        this.disposalOperateType = disposalOperateType;
        this.patrolRecordId = patrolRecordId;
        this.pointId = pointId;
        this.upload = upload;
        this.time = time;
        this.reportUnit = reportUnit;
        this.detail = detail;
        this.photoPath = photoPath;
        this.photoURL = photoURL;
        Police police = LitePal.where("internetID = ?",policeId).findFirst(Police.class);
        policeName = police.getRealName();
        Event event = LitePal.where("internetID = ?",eventId).findFirst(Event.class);
        eventName = event.getName();
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getReportUnit()
    {
        return reportUnit;
    }

    public void setReportUnit(String reportUnit)
    {
        this.reportUnit = reportUnit;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getPhotoPath()
    {
        return photoPath;
    }

    public void setPhotoPath(String photoPath)
    {
        this.photoPath = photoPath;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }

    public void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }

    public boolean isUpload()
    {
        return upload;
    }

    public void setUpload(boolean upload)
    {
        this.upload = upload;
    }

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
        return patrolRecordId;
    }

    public void setPatrolRecordId(String patrolRecordId)
    {
        this.patrolRecordId = patrolRecordId;
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

    public String getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(String lastTime)
    {
        this.lastTime = lastTime;
    }
}
