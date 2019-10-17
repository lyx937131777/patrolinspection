package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

//线路
public class PatrolLine extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String companyId;
    private String patrolLineNo;
    private String patrolLineName;
    private List<String> eventInfoIds;
    private List<Event> eventInfos;
    private List<PatrolIP> pointLineModels;
    private String patrolLineType;
    private String pictureType;
    private boolean iscanJump;

    private List<String> policeIds;


//    public PatrolLine(String internetID, String companyId, String patrolLineNo,
//                      String patrolLineName, List<Event> eventInfos,
//                      List<PatrolIP> pointLineModels, String patrolLineType, String pictureType,
//                      boolean iscanJump)
//    {
//        this.internetID = internetID;
//        this.companyId = companyId;
//        this.patrolLineNo = patrolLineNo;
//        this.patrolLineName = patrolLineName;
//        this.eventInfos = eventInfos;
//        this.pointLineModels = pointLineModels;
//        this.patrolLineType = patrolLineType;
//        this.pictureType = pictureType;
//        this.iscanJump = iscanJump;
//    }


    public List<String> getPoliceIds()
    {
        return policeIds;
    }

    public void setPoliceIds(List<String> policeIds)
    {
        this.policeIds = policeIds;
    }

    public List<String> getEventInfoIds()
    {
        return eventInfoIds;
    }

    public void setEventInfoIds(List<String> eventInfoIds)
    {
        this.eventInfoIds = eventInfoIds;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getPatrolLineNo()
    {
        return patrolLineNo;
    }

    public void setPatrolLineNo(String patrolLineNo)
    {
        this.patrolLineNo = patrolLineNo;
    }

    public String getPatrolLineName()
    {
        return patrolLineName;
    }

    public void setPatrolLineName(String patrolLineName)
    {
        this.patrolLineName = patrolLineName;
    }

    public List<Event> getEventInfos()
    {
        return eventInfos;
    }

    public void setEventInfos(List<Event> eventInfos)
    {
        this.eventInfos = eventInfos;
    }

    public List<PatrolIP> getPointLineModels()
    {
        return pointLineModels;
    }

    public void setPointLineModels(List<PatrolIP> pointLineModels)
    {
        this.pointLineModels = pointLineModels;
    }

    public String getPatrolLineType()
    {
        return patrolLineType;
    }

    public void setPatrolLineType(String patrolLineType)
    {
        this.patrolLineType = patrolLineType;
    }

    public String getPictureType()
    {
        return pictureType;
    }

    public void setPictureType(String pictureType)
    {
        this.pictureType = pictureType;
    }

    public boolean isIscanJump()
    {
        return iscanJump;
    }

    public void setIscanJump(boolean iscanJump)
    {
        this.iscanJump = iscanJump;
    }
}
