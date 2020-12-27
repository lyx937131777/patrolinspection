package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//护校事件
public class SchoolEvent implements Serializable
{
    @SerializedName("id")
    private String internetID;
    private String schoolEventType;
    private String photo;
    private String occurrenceTime;
    private String schoolEventStatus;
    private String policeId;
    private String policeName;

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getSchoolEventType()
    {
        return schoolEventType;
    }

    public void setSchoolEventType(String schoolEventType)
    {
        this.schoolEventType = schoolEventType;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String getOccurrenceTime()
    {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime)
    {
        this.occurrenceTime = occurrenceTime;
    }

    public String getSchoolEventStatus()
    {
        return schoolEventStatus;
    }

    public void setSchoolEventStatus(String schoolEventStatus)
    {
        this.schoolEventStatus = schoolEventStatus;
    }

    public String getPoliceId()
    {
        return policeId;
    }

    public void setPoliceId(String policeId)
    {
        this.policeId = policeId;
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
