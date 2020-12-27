package com.example.patrolinspection.db;

import com.example.patrolinspection.util.MapUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//护校事件记录
public class SchoolEventRecord implements Serializable
{
    @SerializedName("id")
    private String internetID;

    private String schoolEventRecordType;
    private String policeId;
    private SchoolEvent schoolEventInfo;

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getSchoolEventRecordType()
    {
        return schoolEventRecordType;
    }

    public void setSchoolEventRecordType(String schoolEventRecordType)
    {
        this.schoolEventRecordType = schoolEventRecordType;
    }

    public String getPoliceId()
    {
        return policeId;
    }

    public void setPoliceId(String policeId)
    {
        this.policeId = policeId;
    }

    public SchoolEvent getSchoolEventInfo()
    {
        return schoolEventInfo;
    }

    public void setSchoolEventInfo(SchoolEvent schoolEventInfo)
    {
        this.schoolEventInfo = schoolEventInfo;
    }

    //获得schoolEvent的相关参数
    public String getSchoolEventStatus(){
        return schoolEventInfo.getSchoolEventStatus();
    }

    public String getSchoolEventType(){
        return schoolEventInfo.getSchoolEventType();
    }

    public String getPhoto(){
        return schoolEventInfo.getPhoto();
    }

    public String getOccurrenceTime(){
        return schoolEventInfo.getOccurrenceTime();
    }

    public String getHandlingPoliceId(){
        return schoolEventInfo.getPoliceId();
    }

    public String getHandlingPoliceName(){
        return schoolEventInfo.getPoliceName();
    }

    //判断
    public boolean isUndispose(){
        return getSchoolEventStatus().equals("undispose") || getSchoolEventStatus().equals("unfound");
    }

    public boolean isSamePolice(){
        return getHandlingPoliceId().equals(policeId);
    }

    public boolean isOtherPoliceHandling(){
        if(getSchoolEventStatus().equals("disposing") && !isSamePolice()){
            return true;
        }
        return false;
    }

    public boolean isOtherPoliceHandled(){
        if(getSchoolEventStatus().equals("disposed") && !isSamePolice()){
            return true;
        }
        return false;
    }

    public boolean isDisposedByMe(){
        if(schoolEventRecordType.equals("unfound") || schoolEventRecordType.equals("undanger") || schoolEventRecordType.equals("danger")){
            return true;
        }
        return false;
    }

    public boolean needToReceive(){
        if(schoolEventRecordType.equals("unoperate")){
            return true;
        }
        return false;
    }

    public boolean needToConcern(){
        if( isUndispose() && schoolEventRecordType.equals("receive")){
            return true;
        }
        return false;
    }

    public boolean needToDoNothing(){
        if(isOtherPoliceHandling() || isOtherPoliceHandled() || isDisposedByMe()){
            return true;
        }
        return false;
    }

    public boolean needToHandle(){
        if(!isUndispose() && isSamePolice() && schoolEventRecordType.equals("concern")){
            return true;
        }
        return false;
    }

    public String getState(){
        if(isOtherPoliceHandling()){
            return getHandlingPoliceName() + "处置中";
        }
        if(isOtherPoliceHandled()){
            return getHandlingPoliceName() + "已处置";
        }
        return MapUtil.getSchoolEventRecordType(schoolEventRecordType);
    }
}
