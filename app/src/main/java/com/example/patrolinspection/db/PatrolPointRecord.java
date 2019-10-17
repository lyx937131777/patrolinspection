package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class PatrolPointRecord extends LitePalSupport
{
    private String patrolRecordId;
    private String pointId;
    private String orderNo;
    private long time;

    private String state;
    private String photoBase64;
    private String photoPath;
    @SerializedName("photo")
    private String photoURL;

    public PatrolPointRecord(String patrolRecordId, PatrolIP patrolIP){
        this.patrolRecordId = patrolRecordId;
        this.pointId = patrolIP.getPointId();
        this.orderNo = patrolIP.getOrderNo();
        state = "未巡检";
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
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

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getPhotoBase64()
    {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64)
    {
        this.photoBase64 = photoBase64;
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

    @Override
    public String toString()
    {
        String t;
        if(time == 0){
            t = "";
        }else{
            t = String.valueOf(time);
        }
        return "{" +
                "\"patrolRecordId\":\"" + patrolRecordId + "\"" +
                ", \"pointId\":\"" + pointId + "\"" +
                ", \"orderNo\":\"" + orderNo + "\"" +
                ", \"photo\":\"" + photoURL + "\"" +
                ", \"time\":\"" + t + "\"" +
                "}";
    }
}
