package com.example.patrolinspection.db;

import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;
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

    public String getPointNo(){
        InformationPoint informationPoint = LitePal.where("internetID = ?",pointId).findFirst(InformationPoint.class);
        if(informationPoint != null){
            return informationPoint.getNum();
        }
        return "该信息点已被删除";
    }

    public String getPointName(){
//        LogUtil.e("PatrolIP","11111111111111");
//        LogUtil.e("PatrolIP","11111111111111 : " + pointId);
        InformationPoint informationPoint = LitePal.where("internetID = ?",pointId).findFirst(InformationPoint.class);
//        LogUtil.e("PatrolIP","22222222222222 : " + pointId);
        if(informationPoint != null){
//            LogUtil.e("PatrolIP",informationPoint.getName());
            return informationPoint.getName();
        }
        return "该信息点已被删除";
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
