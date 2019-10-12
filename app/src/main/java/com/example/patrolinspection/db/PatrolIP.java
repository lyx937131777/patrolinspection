package com.example.patrolinspection.db;

import org.litepal.crud.LitePalSupport;

//巡检路线中的信息点
public class PatrolIP extends LitePalSupport
{
    private String patrolLineID;
    private String orderNo;
    private String pointId;
//    private InformationPoint pointInfo;

//    public PatrolIP(String orderNo, String pointId, InformationPoint pointInfo)
//    {
//        this.orderNo = orderNo;
//        this.pointId = pointId;
//        this.pointInfo = pointInfo;
//    }


    public String getPatrolLineID()
    {
        return patrolLineID;
    }

    public void setPatrolLineID(String patrolLineID)
    {
        this.patrolLineID = patrolLineID;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getPointId()
    {
        return pointId;
    }

    public void setPointId(String pointId)
    {
        this.pointId = pointId;
    }

//    public InformationPoint getPointInfo()
//    {
//        return pointInfo;
//    }
//
//    public void setPointInfo(InformationPoint pointInfo)
//    {
//        this.pointInfo = pointInfo;
//    }
}
