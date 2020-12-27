package com.example.patrolinspection.db;

import org.litepal.crud.LitePalSupport;

//信息点拍照记录
public class PointPhotoRecord extends LitePalSupport
{
    private String patrolRecordId;
    private String pointId;
    private long time;
    private String photoURL;
    private String photoPath;

    public PointPhotoRecord(String patrolRecordId, String pointId, String photoPath, long time){
        this.patrolRecordId = patrolRecordId;
        this.pointId = pointId;
        this.photoPath = photoPath;
        this.time = time;
        photoURL = "";
        this.save();
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }

    public void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }

    public String getPhotoPath()
    {
        return photoPath;
    }

    public void setPhotoPath(String photoPath)
    {
        this.photoPath = photoPath;
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

    @Override
    public String toString()
    {
        return "{"+
                "\"time\":\"" + time + "\"" +
                ", \"photo\":\"" + photoURL + "\"" +
                "}";
    }
}
