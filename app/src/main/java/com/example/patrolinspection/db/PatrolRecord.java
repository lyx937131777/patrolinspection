package com.example.patrolinspection.db;

import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;
import com.google.gson.annotations.SerializedName;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//巡检记录
public class PatrolRecord extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String patrolScheduleId;
    private String companyId;
    private String policeId;
    private String policeName;
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

    private List<PatrolPointRecord> pointPatrolRecords;

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

    public List<PatrolPointRecord> getPointPatrolRecords()
    {
        return pointPatrolRecords;
    }

    public void setPointPatrolRecords(List<PatrolPointRecord> pointPatrolRecords)
    {
        this.pointPatrolRecords = pointPatrolRecords;
    }

    public String getPoliceName()
    {
        return policeName;
    }

    public void setPoliceName(String policeName)
    {
        this.policeName = policeName;
    }

    public String toHeadString()
    {
        return "{" +
                "\"id\":\"" + internetID + "\"";
    }
    public String toTailString()
    {
        String t;
        if(endTime == 0 ){
            t = "";
        }else{
            t = String.valueOf(endTime);
        }
        return ", \"patrolScheduleId\":\"" + patrolScheduleId + "\"" +
                ", \"companyId\":\"" + companyId + "\"" +
                ", \"policeId\":\"" + policeId + "\"" +
                ", \"policeName\":\"" + policeName + "\"" +
                ", \"equipmentId\":\"" + equipmentId + "\"" +
                ", \"patrolTimeStatus\":\"" + patrolTimeStatus + "\"" +
                ", \"patrolPointStatus\":\"" + patrolPointStatus + "\"" +
                ", \"isnonrmal\":\"" + isnonrmal + "\"" +
                ", \"startTime\":\"" + startTimeLong + "\"" +
                ", \"endTime\":\"" + t + "\"" +
                ", \"pointPatrolRecords\":" + pointPatrolRecords +
                "}";
    }

    @Override
    public String toString()
    {
        String t;
        if(endTime == 0 ){
            t = "";
        }else{
            t = String.valueOf(endTime);
        }
        return "{" +
                "\"id\":\"" + internetID + "\"" +
                ", \"patrolScheduleId\":\"" + patrolScheduleId + "\"" +
                ", \"companyId\":\"" + companyId + "\"" +
                ", \"policeId\":\"" + policeId + "\"" +
                ", \"equipmentId\":\"" + equipmentId + "\"" +
                ", \"patrolTimeStatus\":\"" + patrolTimeStatus + "\"" +
                ", \"patrolPointStatus\":\"" + patrolPointStatus + "\"" +
                ", \"isnonrmal\":\"" + isnonrmal + "\"" +
                ", \"startTime\":\"" + startTimeLong + "\"" +
                ", \"endTime\":\"" + t + "\"" +
                ", \"pointPatrolRecords\":" + pointPatrolRecords +
                "}";
    }

    public int getDuringMin(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        return patrolSchedule.getDuringMin();
    }

    public int getLimit(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        return Integer.valueOf(patrolSchedule.getErrorRange());
    }

    public long getStartTimeHead(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        String sTime = patrolSchedule.getStartTime();
        Date date = new Time(startTimeLong);
        String time =  TimeUtil.dateToString(date,"yyyy-MM-dd")+ " "+sTime;
        Date date2 = TimeUtil.stringToDate(time,"yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.add(Calendar.MINUTE, -getLimit());
        return calendar.getTimeInMillis();
    }

    public long getEndTimeTail(){
        Date date = new Time(startTimeLong);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,getDuringMin() + getLimit());
        return calendar.getTimeInMillis();
    }
    public long getEndLimit(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        String eTime = patrolSchedule.getEndTime();
        Date date = new Time(startTimeLong);
        if(patrolSchedule.isTwoDay()){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,1);
            date = c.getTime();
        }
        String time =  TimeUtil.dateToString(date,"yyyy-MM-dd")+ " "+eTime;
        Date date2 = TimeUtil.stringToDate(time,"yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.add(Calendar.MINUTE, getLimit());
        return calendar.getTimeInMillis();
    }

    public long getRealEndLimit(){
        if(getEndTimeTail() < getEndLimit()){
            return getEndTimeTail();
        }
        return getEndLimit();
    }

    public String getLineName(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        if(patrolSchedule == null){
            return "排班已被删除";
        }
        return patrolSchedule.getLineName();
    }

    public String getPlanType(){
        PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolScheduleId).findFirst(PatrolSchedule.class);
        if(patrolSchedule == null){
            return "排班已被删除";
        }
        return patrolSchedule.getPlanType();
    }
}
