package com.example.patrolinspection.db;

import com.example.patrolinspection.util.LogUtil;
import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

//信息点巡检记录
public class PatrolPointRecord extends LitePalSupport
{
    private String patrolRecordId;
    private String pointId;
    private String pointNo;
    private String pointName;
    private String orderNo;
    private long time;

    private String state;
    private List<PointPhotoRecord> pointPhotoInfos;

    public PatrolPointRecord(){

    }

    public PatrolPointRecord(String patrolRecordId, PatrolIP patrolIP){
        this.patrolRecordId = patrolRecordId;
        pointId = patrolIP.getPointId();
        orderNo = patrolIP.getOrderNo();
//        LogUtil.e("PatrolPointRecord",  patrolRecordId + " kkkk "+patrolIP.getPointId() + " hhhh " + patrolIP.getOrderNo() + " jjj " + patrolIP.getPatrolLineID());
        pointName = patrolIP.getPointName();
        pointNo = patrolIP.getPointNo();
        state = "未巡检";
        pointPhotoInfos = new ArrayList<>();
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


    public String getPointNo()
    {
        return pointNo;
    }

    public void setPointNo(String pointNo)
    {
        this.pointNo = pointNo;
    }

    public String getPointName()
    {
        return pointName;
    }

    public void setPointName(String pointName)
    {
        this.pointName = pointName;
    }

    public List<PointPhotoRecord> getPointPhotoInfos()
    {
        if(pointPhotoInfos == null){
            pointPhotoInfos = LitePal.where("patrolRecordId = ? and pointId = ?",patrolRecordId,pointId).find(PointPhotoRecord.class);
        }
        return pointPhotoInfos;
    }

    public void setPointPhotoInfos(List<PointPhotoRecord> pointPhotoInfos)
    {
        this.pointPhotoInfos = pointPhotoInfos;
    }

    public void addPhoto(String photoPath, long time){
        if(pointPhotoInfos == null){
            pointPhotoInfos = getPointPhotoInfos();
        }
        pointPhotoInfos.add(new PointPhotoRecord(patrolRecordId,pointId,photoPath, time));
    }

    public boolean hasPhoto(){
        if(pointPhotoInfos == null){
            pointPhotoInfos = getPointPhotoInfos();
        }
        return pointPhotoInfos.size() > 0;
    }

    @Override
    public String toString()
    {
        pointPhotoInfos = getPointPhotoInfos();
        String t;
        if(time == 0){
            t = "";
        }else{
            t = String.valueOf(time);
        }
        return "{" +
                "\"patrolRecordId\":\"" + patrolRecordId + "\"" +
                ", \"pointId\":\"" + pointId + "\"" +
                ",\"pointNo\":\"" + pointNo + "\"" +
                ",\"pointName\":\"" + pointName + "\"" +
                ", \"orderNo\":\"" + orderNo + "\"" +
                ", \"pointPhotoInfos\":" + pointPhotoInfos +
                ", \"time\":\"" + t + "\"" +
                "}";
    }
}
